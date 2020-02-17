import constants from "../utils/constants";
import axios from "../utils/axios";
import { responseIsStatusOk } from "../utils/variousUtils";
import endpoints from "../utils/endpoints";

export const fetchNotificationTokenAndIv = () => async (dispatch: any) => {
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

export const setNotificationsLoadAfterDispatch = (timestamp: any) => (
  dispatch: any
) => {
  dispatch(setNotificationsLoadAfter(timestamp));
};

export const setNotificationsSocketDispatch = (ws: any) => (dispatch: any) => {
  dispatch(setNotificationsSocket(ws));
};

export const setNotificationsCheckedAtTimestampDispatch = (timestamp: any) => (
  dispatch: any
) => {
  dispatch(setNotificationsCheckedAtTimestamp(timestamp));
};

export const addNotificationsDispatch = (notifications: any) => (
  dispatch: any
) => {
  dispatch(addNotifications(notifications));
};

export const setNotificationsNeedToSendCheckAllRequestToSocketDispatch = (
  doWe: any
) => (dispatch: any) => {
  dispatch(setNotificationsNeedToSendCheckAllRequestToSocket(doWe));
};

const setNotificationsNeedToSendCheckAllRequestToSocket = (doWe: any) => ({
  type: constants.SET_NOTIFICATIONS_NEED_TO_SEND_CHECK_ALL_REQUEST_TO_SOCKET,
  payload: doWe
});

const addNotifications = (notifications: any) => ({
  type: constants.NOTIFICATIONS_ADD_NOTIFICATIONS,
  payload: notifications
});

const setNotificationsCheckedAtTimestamp = (timestamp: any) => ({
  type: constants.SET_NOTIFICATIONS_CHECKED_AT_TIMESTAMP,
  payload: timestamp
});

const setNotificationsSocket = (ws: any) => ({
  type: constants.SET_NOTIFICATIONS_SOCKET,
  payload: ws
});

const setNotificationsIv = (iv: any) => ({
  type: constants.SET_NOTIFICATIONS_IV,
  payload: iv
});

const setNotificationsToken = (token: any) => ({
  type: constants.SET_NOTIFICATIONS_TOKEN,
  payload: token
});

const setNotificationsLoadAfter = (timestamp: any) => ({
  type: constants.SET_NOTIFICATIONS_LOAD_AFTER,
  payload: timestamp
});
