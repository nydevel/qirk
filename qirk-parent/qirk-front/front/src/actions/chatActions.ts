import constants from "./../utils/constants";
import endpoints from "./../utils/endpoints";
import axios from "./../utils/axios";
import { responseIsStatusOk } from "./../utils/variousUtils";
import { Dispatch } from "react";
import { Action } from "../utils/types/Action";
import { User } from "../utils/types/User";

export const fetchChatToken = (
  chatId: number | string,
  chatType = "Not-specified"
) => async (dispatch: Dispatch<Action>) => {
  dispatch(setChatTokenRequestStatus(constants.WAITING));
  try {
    const result = await axios.get(
      endpoints.GET_CHAT_TOKEN.replace(
        "{lc_chat_type}",
        chatType.toLowerCase()
      ),
      {
        params: { id: chatId }
      }
    );
    if (
      responseIsStatusOk(result) &&
      result.data.data &&
      result.data.data.length > 0
    ) {
      dispatch(setToken(result.data.data[0].token));
      dispatch(setIV(result.data.data[0].IV));
      dispatch(setChatCanWrite(result.data.data[0].can_write));
      dispatch(setChatLastTimeTokenFetched(Date.now()));
      dispatch(setChatTokenRefreshRequired(false));
      dispatch(setChatTokenRequestStatus(constants.SUCCESS));
    } else {
      dispatch(setChatTokenRequestStatus(constants.FAILED));
    }
  } catch {
    dispatch(setChatTokenRequestStatus(constants.FAILED));
  }
};

export const fetchSenderInfo = (userId: User["id"]) => async (
  dispatch: Dispatch<Action>
) => {
  dispatch(addSender({ id: userId, alias: "   ...   " }));
  try {
    const response = await axios.get(endpoints.GET_USER, {
      params: {
        id: userId
      }
    });
    if (
      responseIsStatusOk(response) &&
      response.data.data &&
      response.data.data.length > 0
    ) {
      dispatch(updateSender(response.data.data[0]));
    }
  } catch {
    // don't panic \<*n*>/
  }
};

export const chatResetDispatch = () => (dispatch: Dispatch<Action>) => {
  dispatch(chatReset());
};

export const addChatMessagesSortDispatch = (messages: any) => (
  dispatch: Dispatch<Action>
) => {
  dispatch(addChatMessagesSort(messages));
};

export const setChatSocketDispatch = (socket: any) => (dispatch: any) => {
  dispatch(setChatSocket(socket));
};

export const setChatEarliestMessageTimeOnLoadBeforeDispatch = (time: any) => (
  dispatch: any
) => {
  dispatch(setChatEarliestMessageTimeOnLoadBefore(time));
};

export const deleteChatMessagesByExternalUuidListDispatch = (uuidList: any) => (
  dispatch: any
) => {
  dispatch(deleteChatMessagesByExternalUuidList(uuidList));
};

export const setChatIdDispatch = (id: any) => (dispatch: any) => {
  dispatch(setChatId(id));
};

export const setChatTypeDispatch = (type: any) => (dispatch: any) => {
  dispatch(setChatType(type));
};

export const setChatResendingUncomfirmedMessagesRequiredDispatch = (
  isRequired: any
) => (dispatch: any) => {
  dispatch(setChatResendingUncomfirmedMessagesRequired(isRequired));
};

export const setChatTokenRefreshRequiredDispatch = (isRequired: any) => (
  dispatch: any
) => {
  dispatch(setChatTokenRefreshRequired(isRequired));
};

export const chatConfirmMessagesDeliveryByExternalUuidsDispatch = (
  uuids: any
) => (dispatch: any) => {
  dispatch(chatConfirmMessagesDeliveryByExternalUuids(uuids));
};

export const setChatSocketTokenNotificationRequiredDispatch = (
  isRequired: any
) => (dispatch: any) => {
  dispatch(setChatSocketTokenNotificationRequired(isRequired));
};

export const setChatSocketTokenNotificationTypeDispatch = (type: any) => (
  dispatch: any
) => {
  dispatch(setChatSocketTokenNotificationType(type));
};

export const setChatIgnoreMatchingEarliestMessageTimeOnloadBeforeDispatch = (
  ignoreOrNot: any
) => (dispatch: any) => {
  dispatch(setChatIgnoreMatchingEarliestMessageTimeOnloadBefore(ignoreOrNot));
};

export const setChatLoadBeforeRequiredDispatch = (isRequired: any) => (
  dispatch: any
) => {
  dispatch(setChatLoadBeforeRequired(isRequired));
};

export const setChatIsLoadingDispatch = (isLoading: any) => (dispatch: any) => {
  dispatch(setChatIsLoading(isLoading));
};

const setChatIsLoading = (isLoading: any) => ({
  payload: isLoading,
  type: constants.SET_CHAT_IS_LOADING
});

const setChatIgnoreMatchingEarliestMessageTimeOnloadBefore = (
  ignoreOrNot: any
) => ({
  payload: ignoreOrNot,
  type: constants.SET_CHAT_IGNORE_MATCHING_EARLIEST_MESSAGE_TIME_ONLOAD_BEFORE
});

const setChatLoadBeforeRequired = (isRequired: any) => ({
  payload: isRequired,
  type: constants.SET_CHAT_LOAD_BEFORE_REQUIRED
});

const setChatSocketTokenNotificationRequired = (isRequired: any) => ({
  payload: isRequired,
  type: constants.SET_CHAT_SOCKET_TOKEN_NOTIFICATION_REQUIRED
});

const setChatSocketTokenNotificationType = (type: any) => ({
  payload: type,
  type: constants.SET_CHAT_SOCKET_TOKEN_NOTIFICATION_TYPE
});

const setChatLastTimeTokenFetched = (timeMillis: any) => ({
  payload: timeMillis,
  type: constants.SET_CHAT_LAST_TIME_TOKEN_FETCHED
});

const chatConfirmMessagesDeliveryByExternalUuids = (uuids: any) => ({
  payload: uuids,
  type: constants.CHAT_CONFIRM_MESSAGES_DELIVERY_BY_EXTERNAL_UUIDS
});

const setChatTokenRefreshRequired = (isRequired: any) => ({
  payload: isRequired,
  type: constants.SET_CHAT_TOKEN_REFRESH_REQUIRED
});

const setChatResendingUncomfirmedMessagesRequired = (isRequired: any) => ({
  payload: isRequired,
  type: constants.SET_CHAT_RESENDING_UNCONFIRMED_MESSAGES_REQUIRED
});

const setChatId = (id: any) => ({
  payload: id,
  type: constants.SET_CHAT_ID
});

const setChatType = (type: any) => ({
  payload: type,
  type: constants.SET_CHAT_TYPE
});

const deleteChatMessagesByExternalUuidList = (uuidList: any) => ({
  payload: uuidList,
  type: constants.DELETE_CHAT_MESSAGES_BY_EXTERNAL_UUID_LIST
});

const chatReset = () => ({
  type: constants.CHAT_RESET
});

const addChatMessagesSort = (messages: any) => ({
  payload: messages,
  type: constants.ADD_CHAT_MESSAGES_SORT
});

const setChatTokenRequestStatus = (status: any) => ({
  payload: status,
  type: constants.SET_CHAT_TOKEN_REQUEST_STATUS
});

const setChatSocket = (socket: any) => ({
  payload: socket,
  type: constants.SET_CHAT_SOCKET
});

const setChatEarliestMessageTimeOnLoadBefore = (time: any) => ({
  payload: time,
  type: constants.SET_CHAT_EARLIEST_MESSAGE_TIME_ONLOAD_BEFORE
});

const setToken = (token: any) => ({
  payload: token,
  type: constants.SET_CHAT_TOKEN
});

const setIV = (IV: any) => ({
  payload: IV,
  type: constants.SET_CHAT_IV
});

const setChatCanWrite = (canIt: any) => ({
  payload: canIt,
  type: constants.SET_CHAT_CAN_WRITE
});

const addSender = (sender: any) => ({
  payload: sender,
  type: constants.ADD_SENDER
});

const updateSender = (sender: any) => ({
  payload: sender,
  type: constants.UPDATE_SENDER
});
