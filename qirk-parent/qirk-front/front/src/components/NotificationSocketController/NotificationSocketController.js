import { useEffect } from "react";
import { connect } from "react-redux";
import {
  setNotificationsSocketDispatch,
  fetchNotificationTokenAndIv,
  addNotificationsDispatch,
  setNotificationsCheckedAtTimestampDispatch,
  setNotificationsNeedToSendCheckAllRequestToSocketDispatch
} from "../../actions/notificationActions";
import endpoints from "../../utils/endpoints";
import useInterval from "../../utils/hooks/useInterval";
import constants from "../../utils/constants";
import { debugDateTime } from "../../utils/timeUtils";

const PING_INTERVAL_MIN = 14;

const time = () => debugDateTime();

const NotficationIcon = ({
  iv,
  token,
  ws,
  needToCheckAll,
  isSignedIn,
  setNotificationsSocketDispatch,
  fetchNotificationTokenAndIv,
  addNotificationsDispatch,
  setNotificationsCheckedAtTimestampDispatch,
  setNotificationsNeedToSendCheckAllRequestToSocketDispatch,
  loadAfter
}) => {
  const processIncomingNotification = notification => {
    if (!notification) {
      console.error(
        `processIncomingNotification() called on falsy notification: ${notification}`
      );
      return false;
    }

    const json = notification.json;

    // TODO Denis remove when fixed
    if (json && json === "json") {
      // bug at backend. Ignore this nonsense
      return false;
    }

    if (json) {
      try {
        const parsed = JSON.parse(notification.json);
        const { epoch_milli, iso8601 } = notification.timestamp;

        const processedNotification = {
          ...parsed,
          ...notification,
          epoch_milli,
          iso8601
        };

        delete processedNotification.json;
        delete processedNotification.timestamp;

        return { ...processedNotification };
      } catch (e) {
        console.error(e);
        return false;
      }
    }
  };

  const parseAndDealWithSocketIncomingMessage = message => {
    try {
      const messageObj = JSON.parse(message);

      console.log(`${time()} | [socket] > recieved (Notification)`, messageObj);

      const meta = messageObj.meta;
      if (meta && meta.last_check && meta.last_check) {
        const lastCheck = meta.last_check;
        setNotificationsCheckedAtTimestampDispatch(lastCheck);
      }

      const incomingNotifications = messageObj.data;
      if (
        incomingNotifications &&
        incomingNotifications.length > 0 &&
        messageObj.response_type === constants.NOTIFICATIONS
      ) {
        addNotificationsDispatch(
          incomingNotifications
            .map(n => processIncomingNotification(n))
            .filter(no => !!no)
        );
      }
    } catch (e) {
      console.error(e);
    }
  };

  const stringifyAndSendToSocket = (message, socket = ws) => {
    try {
      if (!socket) {
        return console.error(
          `${time()} | WebSocket was supposed to be defined at this point (Notification)`
        );
      }

      if (WebSocket.OPEN !== socket.readyState) {
        return console.error(
          `${time()} | WebSocket was supposed to be in open state at this point (Notification)`
        );
      }

      socket.send(JSON.stringify(message));
      console.log(`${time()} | [socket] > send (Notification)`, message);
    } catch (e) {
      console.error(e);
    }
  };

  const setupWsConnection = () => {
    try {
      const socket = new WebSocket(
        endpoints.WS_NOTIFICATIONS.replace(
          "{domain}",
          window.location.hostname
        ).replace("{port}", window.location.port)
      );

      socket.onmessage = ({ data }) => {
        parseAndDealWithSocketIncomingMessage(data);
      };

      socket.onopen = () => {
        stringifyAndSendToSocket(
          {
            request_type: constants.GET_HISTORY,
            token: token,
            IV: iv
          },
          socket
        );
      };

      socket.onerror = e => {
        console.error(`${time()} | ws:onerror (Notification)`, e);
      };

      socket.onclose = e => {
        console.log(`${time()} | ws:onclose (Notification)`, e);
      };

      setNotificationsSocketDispatch(socket);
    } catch (e) {
      console.error(e);
    }
  };

  const pingIfOpen = () => {
    console.log(`${time()} | trying to send ping (Notification)`);
    stringifyAndSendToSocket({
      request_type: constants.PING
    });
  };

  useEffect(() => {
    if (isSignedIn) {
      fetchNotificationTokenAndIv();
    }
  }, [isSignedIn]);

  useEffect(() => {
    if (isSignedIn && null !== iv && null !== token) {
      setupWsConnection();
    }
  }, [iv, token, isSignedIn]);

  useEffect(() => {
    if (loadAfter) {
      stringifyAndSendToSocket({
        request_type: constants.GET_HISTORY,
        token: token,
        IV: iv,
        timestamp: loadAfter
      });
    }
  }, [loadAfter]);

  useEffect(() => {
    if (needToCheckAll && ws && WebSocket.OPEN === ws.readyState) {
      stringifyAndSendToSocket({
        request_type: constants.CHECK_ALL_NOTIFICATIONS
      });
      setNotificationsNeedToSendCheckAllRequestToSocketDispatch(false);
    }
  }, [needToCheckAll, ws]);

  useInterval(() => {
    pingIfOpen();
  }, PING_INTERVAL_MIN * 60 * 1000);

  return null;
};

export default connect(
  state => ({
    iv: state.notifications.iv,
    token: state.notifications.token,
    ws: state.notifications.socket,
    needToCheckAll: state.notifications.needToCheckAll,
    isSignedIn: state.auth.isSignedIn,
    loadAfter: state.notifications.loadAfter
  }),
  {
    setNotificationsSocketDispatch,
    fetchNotificationTokenAndIv,
    addNotificationsDispatch,
    setNotificationsCheckedAtTimestampDispatch,
    setNotificationsNeedToSendCheckAllRequestToSocketDispatch
  }
)(NotficationIcon);
