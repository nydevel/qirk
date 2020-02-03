import constants from "../utils/constants";
import axios from "../utils/axios";
import { responseIsStatusOk } from "../utils/variousUtils";
import endpoints from "../utils/endpoints";

export const fetchNotificationTokenAndIv = () => async dispatch => {
  try {
    const response = await axios.get(endpoints.GET_USER_NOTIFICATION_TOKEN);

    if (
      responseIsStatusOk(response) &&
      response.data.data &&
      response.data.data[0] &&
      response.data.data[0].IV &&
      response.data.data[0].token
    ) {
      dispatch(setNotificationsToken(response.data.data[0].token));
      dispatch(setNotificationsIv(response.data.data[0].IV));
    } else {
      console.error("Unexpected response: ", response);
    }
  } catch (e) {
    console.error(e);
  }
};

export const setNotificationsLoadAfterDispatch = timestamp => dispatch => {
  dispatch(setNotificationsLoadAfter(timestamp));
};

export const setNotificationsSocketDispatch = ws => dispatch => {
  dispatch(setNotificationsSocket(ws));
};

export const setNotificationsCheckedAtTimestampDispatch = timestamp => dispatch => {
  dispatch(setNotificationsCheckedAtTimestamp(timestamp));
};

export const addNotificationsDispatch = notifications => dispatch => {
  dispatch(addNotifications(notifications));
};

export const setNotificationsNeedToSendCheckAllRequestToSocketDispatch = doWe => dispatch => {
  dispatch(setNotificationsNeedToSendCheckAllRequestToSocket(doWe));
};

const setNotificationsNeedToSendCheckAllRequestToSocket = doWe => ({
  type: constants.SET_NOTIFICATIONS_NEED_TO_SEND_CHECK_ALL_REQUEST_TO_SOCKET,
  payload: doWe
});

const addNotifications = notifications => ({
  type: constants.NOTIFICATIONS_ADD_NOTIFICATIONS,
  payload: notifications
});

const setNotificationsCheckedAtTimestamp = timestamp => ({
  type: constants.SET_NOTIFICATIONS_CHECKED_AT_TIMESTAMP,
  payload: timestamp
});

const setNotificationsSocket = ws => ({
  type: constants.SET_NOTIFICATIONS_SOCKET,
  payload: ws
});

const setNotificationsIv = iv => ({
  type: constants.SET_NOTIFICATIONS_IV,
  payload: iv
});

const setNotificationsToken = token => ({
  type: constants.SET_NOTIFICATIONS_TOKEN,
  payload: token
});

const setNotificationsLoadAfter = timestamp => ({
  type: constants.SET_NOTIFICATIONS_LOAD_AFTER,
  payload: timestamp
});
