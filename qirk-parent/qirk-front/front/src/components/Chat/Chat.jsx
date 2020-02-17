import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import throttle from "lodash/throttle";
import { v4 } from "uuid";

import { toast } from "react-toastify";
import {
  fetchChatToken,
  addChatMessagesSortDispatch,
  chatResetDispatch,
  setChatEarliestMessageTimeOnLoadBeforeDispatch,
  setChatSocketDispatch,
  deleteChatMessagesByExternalUuidListDispatch,
  setChatIdDispatch,
  setChatTypeDispatch,
  setChatResendingUncomfirmedMessagesRequiredDispatch,
  setChatTokenRefreshRequiredDispatch,
  chatConfirmMessagesDeliveryByExternalUuidsDispatch,
  setChatSocketTokenNotificationRequiredDispatch,
  setChatSocketTokenNotificationTypeDispatch,
  setChatLoadBeforeRequiredDispatch,
  setChatIgnoreMatchingEarliestMessageTimeOnloadBeforeDispatch,
  setChatIsLoadingDispatch
} from "../../actions/chatActions";
import MessagesList from "./Message/MessagesList";
import Loading from "../Loading/Loading";
import useInterval from "../../utils/hooks/useInterval";
import endpoints from "../../utils/endpoints";
import constants from "../../utils/constants";
import "./Chat.sass";
import Button1 from "../Button/Button";
import TextareaAutosize from "@material-ui/core/TextareaAutosize";
import { debugDateTime } from "../../utils/timeUtils";
import { useTranslation } from "react-i18next";

const time = () => debugDateTime();

function Chat(props) {
  const{t}=useTranslation()
  const [textMessage, setTextMessage] = useState("");

  const stringifyAndSendToSocket = (message, socket = props.ws) => {
    if (!socket) {
      console.error(
        `${time()} | WebSocket was supposed to be defined at this point`
      );
      return;
    }

    if (WebSocket.OPEN !== socket.readyState) {
      console.error(
        `${time()} | WebSocket was supposed to be in open state at this point`
      );
      return;
    }

    console.log(`${time()} | [socket] > send`, message);

    socket.send(JSON.stringify(message));
  };

  useEffect(() => {
    if (props.messages && props.messages.length > 0) {
      props.setChatIsLoadingDispatch(false);
    }
  }, [props.messages]);

  useEffect(() => {
    if (props.ws && WebSocket.OPEN !== props.ws.readyState) {
      props.setChatIsLoadingDispatch(false);
    }
  }, [props.ws, props.ws && props.ws.readyState]);

  const parseAndDealWithSocketIncomingMessage = message => {
    const messageObj = JSON.parse(message);

    console.log(`${time()} | [socket] > recieved`, messageObj);

    if (
      messageObj.status_ok === true &&
      messageObj.status_code === constants.OK
    ) {
      if (
        messageObj.response_type === constants.MESSAGE_ACCEPTED &&
        messageObj.data &&
        messageObj.data.length > 0
      ) {
        props.chatConfirmMessagesDeliveryByExternalUuidsDispatch(
          messageObj.data.map(o => o.external_uuid)
        );
      }

      if (constants.MESSAGES === messageObj.response_type) {
        props.setChatIsLoadingDispatch(false);
      }

      if (
        constants.MESSAGES === messageObj.response_type &&
        messageObj.data &&
        messageObj.data.length > 0 &&
        messageObj.meta &&
        messageObj.meta.chat_id === props.chatId &&
        messageObj.meta.chat_type === props.chatType
      ) {
        const possiblyUuidsOfMessagesToDelete = messageObj.data
          .map(m => m.external_uuid)
          .filter(m1 => m1 !== undefined);

        if (possiblyUuidsOfMessagesToDelete.length > 0) {
          props.deleteChatMessagesByExternalUuidListDispatch(
            possiblyUuidsOfMessagesToDelete
          );
        }
        props.addChatMessagesSortDispatch([...messageObj.data]);
      }

      if (messageObj.response_type === constants.REFRESH_TOKEN) {
        props.setChatTokenRefreshRequiredDispatch(true);
        props.setChatSocketTokenNotificationTypeDispatch(null);
        props.setChatSocketTokenNotificationRequiredDispatch(true);
      }

      if (messageObj.response_type === constants.UNSUBSCRIBED) {
        // not supposed to happen
      }
    } else if (
      messageObj.status_code === constants.INVALID_TOKEN &&
      messageObj.status_ok === false
    ) {
      props.setChatTokenRefreshRequiredDispatch(true);
      props.setChatSocketTokenNotificationTypeDispatch(
        messageObj.response_type ? messageObj.response_type : null
      );
      props.setChatSocketTokenNotificationRequiredDispatch(true);
    } else if (
      messageObj.status_code === constants.ERROR &&
      messageObj.status_ok === false
    ) {
      toast.error(t("Errors.Error"));
    }
  };

  useEffect(() => {
    if (
      props.chatType !== props.chatTypeFromStoreState ||
      props.chatId !== props.chatIdFromStoreState
    ) {
      props.chatResetDispatch();
      props.setChatIdDispatch(props.chatId);
      props.setChatTypeDispatch(props.chatType);
    }
  }, [
    props.chatId,
    props.chatType,
    props.chatTypeFromStoreState,
    props.chatIdFromStoreState
  ]);

  useEffect(() => {
    if (
      true === props.tokenRefreshRequired &&
      constants.WAITING !== props.chatTokenRequestStatus &&
      props.chatId &&
      props.chatType &&
      props.chatTypeFromStoreState === props.chatType &&
      props.chatIdFromStoreState === props.chatId
    ) {
      props.fetchChatToken(props.chatId, props.chatType);
    }
  }, [
    props.tokenRefreshRequired,
    props.chatTokenRequestStatus,
    props.chatId,
    props.chatIdFromStoreState,
    props.chatType,
    props.chatTypeFromStoreState
  ]);

  useEffect(() => {
    if (
      true === props.socketTokenNotificationRequired &&
      constants.SUCCESS === props.chatTokenRequestStatus &&
      false === props.tokenRefreshRequired &&
      props.chatToken &&
      props.chatIV &&
      props.chatType &&
      props.chatId &&
      props.chatIdFromStoreState === props.chatId &&
      props.chatTypeFromStoreState === props.chatType
    ) {
      if (constants.SEND_MESSAGE === props.socketTokenNotificationType) {
        props.setChatResendingUncomfirmedMessagesRequiredDispatch(true);
      } else if (constants.GET_HISTORY === props.socketTokenNotificationType) {
        loadPrev(true);
      } else {
        stringifyAndSendToSocket({
          request_type: constants.REFRESH_TOKEN,
          chat_type: props.chatType,
          chat_id: props.chatId,
          token: props.chatToken,
          IV: props.chatIV
        });
      }

      props.setChatSocketTokenNotificationRequiredDispatch(false);
      props.setChatSocketTokenNotificationTypeDispatch(null);
    }
  }, [
    props.socketTokenNotificationRequired,
    props.socketTokenNotificationType,
    props.tokenRefreshRequired,
    props.chatTokenRequestStatus,
    props.chatToken,
    props.chatIV,
    props.chatType,
    props.chatId,
    props.chatIdFromStoreState,
    props.chatTypeFromStoreState
  ]);

  useEffect(() => {
    if (props.resendingUncomfirmedMessagesRequired && props.ws) {
      retrySendingUncomfirmedMessages(props.messages);
    }
  }, [props.resendingUncomfirmedMessagesRequired, props.messages, props.ws]);

  useEffect(() => {
    if (
      constants.SUCCESS === props.chatTokenRequestStatus &&
      props.chatToken &&
      props.chatIV &&
      props.chatIdFromStoreState === props.chatId &&
      props.chatTypeFromStoreState === props.chatType &&
      !props.ws
    ) {
      const socket = new WebSocket(
        endpoints.WS_CHAT.replace("{domain}", window.location.hostname).replace(
          "{port}",
          window.location.port
        )
      );

      socket.onmessage = ({ data }) => {
        parseAndDealWithSocketIncomingMessage(data);
      };

      socket.onopen = () => {
        stringifyAndSendToSocket(
          {
            request_type: constants.GET_HISTORY,
            chat_type: props.chatType,
            chat_id: props.chatId,
            token: props.chatToken,
            IV: props.chatIV
          },
          socket
        );
      };

      socket.onerror = e => {
        console.error(`${time()} | ws:onerror`, e);
      };

      socket.onclose = e => {
        console.log(`${time()} | ws:onclose`);
        console.log(`${time()} | onclose event:`, e);
      };

      props.setChatSocketDispatch(socket);
    }
  }, [
    props.chatId,
    props.chatType,
    props.chatToken,
    props.chatIV,
    props.ws,
    props.chatTokenRequestStatus,
    props.chatIdFromStoreState,
    props.chatTypeFromStoreState
  ]);

  useEffect(() => {
    if (
      props.loadBeforeRequired &&
      props.ws &&
      WebSocket.OPEN === props.ws.readyState
    ) {
      const earliestMessageTime =
        props.messages.length > 0
          ? props.messages[0].timestamp.epoch_milli
          : Date.now() + 1000 * 60 * 60 * 24 * 365 * 20;

      if (
        true === props.ignoreMatchingEarliestMessageTimeOnloadBefore ||
        earliestMessageTime !== props.earliestMessageTimeOnLoadBefore
      ) {
        stringifyAndSendToSocket({
          request_type: constants.GET_HISTORY,
          chat_type: props.chatType,
          chat_id: props.chatId,
          token: props.chatToken,
          IV: props.chatIV,
          timestamp: earliestMessageTime
        });
      }

      props.setChatLoadBeforeRequiredDispatch(false);

      if (true === props.ignoreMatchingEarliestMessageTimeOnloadBefore) {
        props.setChatIgnoreMatchingEarliestMessageTimeOnloadBeforeDispatch(
          false
        );
      }

      if (props.earliestMessageTime !== earliestMessageTime) {
        props.setChatEarliestMessageTimeOnLoadBeforeDispatch(
          earliestMessageTime
        );
      }
    }
  }, [
    props.loadBeforeRequired,
    props.ignoreMatchingEarliestMessageTimeOnloadBefore,
    props.earliestMessageTimeOnLoadBefore,
    props.messages,
    props.ws
  ]);

  const sendMessage = () => {
    const textValue = textMessage.trim();
    if (props.ws && textValue) {
      const externalUuid = v4();
      props.addChatMessagesSortDispatch([
        {
          delivery_not_confirmed: true,
          message: textValue,
          external_uuid: externalUuid,
          sender_id: props.myId,
          timestamp: { epoch_milli: Date.now() + 1000 * 60 * 60 * 24 }
        }
      ]);

      stringifyAndSendToSocket({
        request_type: constants.SEND_MESSAGE,
        chat_type: props.chatType,
        message: textValue,
        external_uuid: externalUuid,
        token: props.chatToken,
        IV: props.chatIV,
        chat_id: props.chatId
      });

      setTextMessage("");
    }
  };

  const onSubmit = e => {
    e.preventDefault();
    sendMessage();
  };

  const loadPrev = ignoreDoubleLoadBeforeSameTime => {
    const earliestMessage = props.messages[0];
    if (
      (earliestMessage &&
        earliestMessage.timestamp.epoch_milli !==
          props.earliestMessageTimeOnLoadBefore) ||
      ignoreDoubleLoadBeforeSameTime
    ) {
      if (
        ignoreDoubleLoadBeforeSameTime !==
        props.ignoreMatchingEarliestMessageTimeOnloadBefore
      ) {
        props.setChatIgnoreMatchingEarliestMessageTimeOnloadBeforeDispatch(
          ignoreDoubleLoadBeforeSameTime
        );
      }
      props.setChatLoadBeforeRequiredDispatch(true);
    }
  };

  const retrySendingUncomfirmedMessages = allMessages => {
    const uncomfirmedMessages = allMessages.filter(
      m => m.delivery_not_confirmed === true && m.external_uuid
    );

    if (uncomfirmedMessages.length > 0) {
      uncomfirmedMessages.forEach(m => {
        stringifyAndSendToSocket({
          ...m,
          request_type: constants.SEND_MESSAGE,
          chat_type: props.chatType,
          token: props.chatToken,
          IV: props.chatIV,
          chat_id: props.chatId
        });
      });
    }

    props.setChatResendingUncomfirmedMessagesRequiredDispatch(false);
  };

  useEffect(() => {
    if (props.messages.length > 0) {
      const messageListElement = document.querySelector(".messages-list");

      if (messageListElement !== null) {
        const onScrollListener = throttle(() => {
          if (messageListElement.scrollTop < 500) {
            loadPrev(false);
          }
        }, 300);
        messageListElement.addEventListener("scroll", onScrollListener);

        return () => {
          messageListElement.removeEventListener("scroll", onScrollListener);
        };
      }
    }
  }, [props.messages]);

  const handleKeyPress = e => {
    if (e.key === "Enter" && e.shiftKey) {
      //new line
    } else if (e.key === "Enter") {
      e.preventDefault();
      sendMessage();
    }
  };

  const pingIfOpen = () => {
    console.log(`${time()} | trying to send ping`);
    stringifyAndSendToSocket({
      request_type: constants.PING
    });
  };

  useInterval(() => {
    pingIfOpen();
  }, 5 * 60 * 1000); // TODO Denis

  return (
    <div className="chat">
      {!props.noLabel &&
        !props.label &&
        !props.isLoading &&
        (props.canWrite || (props.messages && props.messages.length > 0)) && (
          <h3>{t("discussion_label")}</h3>
        )}
      {!props.noLabel &&
        props.label &&
        !props.isLoading &&
        (props.canWrite || (props.messages && props.messages.length > 0)) && (
          <h3>{props.label}</h3>
        )}
      {props.isLoading && !props.hideLoadingSpinner && <Loading />}
      {!props.isLoading && <MessagesList messages={props.messages} />}
      {props.canWrite && !props.isLoading && (
        <div className="form-wrapper">
          <form onSubmit={onSubmit}>
            <div className="textarea-wrapper">
              <TextareaAutosize
                style={{
                  width: "100%",
                  maxWidth: "inherit",
                  overflow: "auto",
                  resize: "none"
                }}
                rows={0}
                rowsMax={4}
                id="chat-textarea"
                maxLength={1023}
                required
                value={textMessage}
                onChange={e => setTextMessage(e.target.value)}
                onKeyPress={handleKeyPress}
              />
            </div>
            <div className="btn-holder">
              <Button1 type="submit">{t("chat_form_submit_btn_name")}</Button1>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}

const mapStateToProps = state => ({
  isLoading: state.chat.isLoading,
  canWrite: state.chat.canWrite,
  chatToken: state.chat.chatToken,
  chatIV: state.chat.chatIV,
  messages: state.chat.messages,
  resendingUncomfirmedMessagesRequired:
    state.chat.resendingUncomfirmedMessagesRequired,
  earliestMessageTimeOnLoadBefore: state.chat.earliestMessageTimeOnLoadBefore,
  ws: state.chat.socket,
  myId: state.user.id,
  chatIdFromStoreState: state.chat.chatId,
  chatTypeFromStoreState: state.chat.chatType,
  tokenRefreshRequired: state.chat.tokenRefreshRequired,
  chatTokenRequestStatus: state.chat.chatTokenRequestStatus,
  lastTimeTokenFetched: state.chat.lastTimeTokenFetched,
  socketTokenNotificationType: state.chat.socketTokenNotificationType,
  socketTokenNotificationRequired: state.chat.socketTokenNotificationRequired,
  loadBeforeRequired: state.chat.loadBeforeRequired,
  ignoreMatchingEarliestMessageTimeOnloadBefore:
    state.chat.ignoreMatchingEarliestMessageTimeOnloadBefore
});

export default withRouter(
  connect(
    mapStateToProps,
    {
      fetchChatToken,
      addChatMessagesSortDispatch,
      chatResetDispatch,
      setChatEarliestMessageTimeOnLoadBeforeDispatch,
      setChatSocketDispatch,
      deleteChatMessagesByExternalUuidListDispatch,
      setChatIdDispatch,
      setChatTypeDispatch,
      setChatResendingUncomfirmedMessagesRequiredDispatch,
      setChatTokenRefreshRequiredDispatch,
      chatConfirmMessagesDeliveryByExternalUuidsDispatch,
      setChatSocketTokenNotificationRequiredDispatch,
      setChatSocketTokenNotificationTypeDispatch,
      setChatLoadBeforeRequiredDispatch,
      setChatIgnoreMatchingEarliestMessageTimeOnloadBeforeDispatch,
      setChatIsLoadingDispatch
    }
  )(Chat)
);
