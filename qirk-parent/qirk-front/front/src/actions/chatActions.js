import constants from "./../utils/constants";
import endpoints from "./../utils/endpoints";
import axios from "./../utils/axios";
import { responseIsStatusOk } from "./../utils/variousUtils";

export const fetchChatToken = (
  chatId,
  chatType = "Not-specified"
) => async dispatch => {
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

export const fetchSenderInfo = userId => async dispatch => {
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

export const chatResetDispatch = () => dispatch => {
  dispatch(chatReset());
};

export const addChatMessagesSortDispatch = messages => dispatch => {
  dispatch(addChatMessagesSort(messages));
};

export const setChatSocketDispatch = socket => dispatch => {
  dispatch(setChatSocket(socket));
};

export const setChatEarliestMessageTimeOnLoadBeforeDispatch = time => dispatch => {
  dispatch(setChatEarliestMessageTimeOnLoadBefore(time));
};

export const deleteChatMessagesByExternalUuidListDispatch = uuidList => dispatch => {
  dispatch(deleteChatMessagesByExternalUuidList(uuidList));
};

export const setChatIdDispatch = id => dispatch => {
  dispatch(setChatId(id));
};

export const setChatTypeDispatch = type => dispatch => {
  dispatch(setChatType(type));
};

export const setChatResendingUncomfirmedMessagesRequiredDispatch = isRequired => dispatch => {
  dispatch(setChatResendingUncomfirmedMessagesRequired(isRequired));
};

export const setChatTokenRefreshRequiredDispatch = isRequired => dispatch => {
  dispatch(setChatTokenRefreshRequired(isRequired));
};

export const chatConfirmMessagesDeliveryByExternalUuidsDispatch = uuids => dispatch => {
  dispatch(chatConfirmMessagesDeliveryByExternalUuids(uuids));
};

export const setChatSocketTokenNotificationRequiredDispatch = isRequired => dispatch => {
  dispatch(setChatSocketTokenNotificationRequired(isRequired));
};

export const setChatSocketTokenNotificationTypeDispatch = type => dispatch => {
  dispatch(setChatSocketTokenNotificationType(type));
};

export const setChatIgnoreMatchingEarliestMessageTimeOnloadBeforeDispatch = ignoreOrNot => dispatch => {
  dispatch(setChatIgnoreMatchingEarliestMessageTimeOnloadBefore(ignoreOrNot));
};

export const setChatLoadBeforeRequiredDispatch = isRequired => dispatch => {
  dispatch(setChatLoadBeforeRequired(isRequired));
};

export const setChatIsLoadingDispatch = isLoading => dispatch => {
  dispatch(setChatIsLoading(isLoading));
};

const setChatIsLoading = isLoading => ({
  payload: isLoading,
  type: constants.SET_CHAT_IS_LOADING
});

const setChatIgnoreMatchingEarliestMessageTimeOnloadBefore = ignoreOrNot => ({
  payload: ignoreOrNot,
  type: constants.SET_CHAT_IGNORE_MATCHING_EARLIEST_MESSAGE_TIME_ONLOAD_BEFORE
});

const setChatLoadBeforeRequired = isRequired => ({
  payload: isRequired,
  type: constants.SET_CHAT_LOAD_BEFORE_REQUIRED
});

const setChatSocketTokenNotificationRequired = isRequired => ({
  payload: isRequired,
  type: constants.SET_CHAT_SOCKET_TOKEN_NOTIFICATION_REQUIRED
});

const setChatSocketTokenNotificationType = type => ({
  payload: type,
  type: constants.SET_CHAT_SOCKET_TOKEN_NOTIFICATION_TYPE
});

const setChatLastTimeTokenFetched = timeMillis => ({
  payload: timeMillis,
  type: constants.SET_CHAT_LAST_TIME_TOKEN_FETCHED
});

const chatConfirmMessagesDeliveryByExternalUuids = uuids => ({
  payload: uuids,
  type: constants.CHAT_CONFIRM_MESSAGES_DELIVERY_BY_EXTERNAL_UUIDS
});

const setChatTokenRefreshRequired = isRequired => ({
  payload: isRequired,
  type: constants.SET_CHAT_TOKEN_REFRESH_REQUIRED
});

const setChatResendingUncomfirmedMessagesRequired = isRequired => ({
  payload: isRequired,
  type: constants.SET_CHAT_RESENDING_UNCONFIRMED_MESSAGES_REQUIRED
});

const setChatId = id => ({
  payload: id,
  type: constants.SET_CHAT_ID
});

const setChatType = type => ({
  payload: type,
  type: constants.SET_CHAT_TYPE
});

const deleteChatMessagesByExternalUuidList = uuidList => ({
  payload: uuidList,
  type: constants.DELETE_CHAT_MESSAGES_BY_EXTERNAL_UUID_LIST
});

const chatReset = () => ({
  type: constants.CHAT_RESET
});

const addChatMessagesSort = messages => ({
  payload: messages,
  type: constants.ADD_CHAT_MESSAGES_SORT
});

const setChatTokenRequestStatus = status => ({
  payload: status,
  type: constants.SET_CHAT_TOKEN_REQUEST_STATUS
});

const setChatSocket = socket => ({
  payload: socket,
  type: constants.SET_CHAT_SOCKET
});

const setChatEarliestMessageTimeOnLoadBefore = time => ({
  payload: time,
  type: constants.SET_CHAT_EARLIEST_MESSAGE_TIME_ONLOAD_BEFORE
});

const setToken = token => ({
  payload: token,
  type: constants.SET_CHAT_TOKEN
});

const setIV = IV => ({
  payload: IV,
  type: constants.SET_CHAT_IV
});

const setChatCanWrite = canIt => ({
  payload: canIt,
  type: constants.SET_CHAT_CAN_WRITE
});

const addSender = sender => ({
  payload: sender,
  type: constants.ADD_SENDER
});

const updateSender = sender => ({
  payload: sender,
  type: constants.UPDATE_SENDER
});
