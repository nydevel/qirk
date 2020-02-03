import constants from "../utils/constants";

const INITIAL_STATE = {
  chatTokenRequestStatus: constants.NOT_REQUESTED,
  chatToken: "",
  chatIV: "",
  chatId: null,
  chatType: null,
  canWrite: false,
  messages: [],
  senders: [],
  earliestMessageTimeOnLoadBefore: Date.now(),
  socket: null,
  resendingUncomfirmedMessagesRequired: false,
  tokenRefreshRequired: true,
  lastTimeTokenFetched: 0,
  socketTokenNotificationRequired: false,
  socketTokenNotificationType: null,
  loadBeforeRequired: false,
  ignoreMatchingEarliestMessageTimeOnloadBefore: false,
  isLoading: true
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case constants.SET_CHAT_IS_LOADING:
      return { ...state, isLoading: action.payload };

    case constants.SET_CHAT_CAN_WRITE:
      return { ...state, canWrite: action.payload };

    case constants.SET_CHAT_IGNORE_MATCHING_EARLIEST_MESSAGE_TIME_ONLOAD_BEFORE:
      return {
        ...state,
        ignoreMatchingEarliestMessageTimeOnloadBefore: action.payload
      };

    case constants.SET_CHAT_LOAD_BEFORE_REQUIRED:
      return { ...state, loadBeforeRequired: action.payload };

    case constants.SET_CHAT_SOCKET_TOKEN_NOTIFICATION_TYPE:
      return { ...state, socketTokenNotificationType: action.payload };

    case constants.SET_CHAT_SOCKET_TOKEN_NOTIFICATION_REQUIRED:
      return { ...state, socketTokenNotificationRequired: action.payload };

    case constants.SET_CHAT_LAST_TIME_TOKEN_FETCHED:
      return { ...state, lastTimeTokenFetched: action.payload };

    case constants.CHAT_CONFIRM_MESSAGES_DELIVERY_BY_EXTERNAL_UUIDS:
      return {
        ...state,
        messages: state.messages.map(m =>
          action.payload.some(uuid => uuid === m.external_uuid)
            ? { ...m, delivery_not_confirmed: false }
            : { ...m }
        )
      };

    case constants.SET_CHAT_TOKEN_REFRESH_REQUIRED:
      return { ...state, tokenRefreshRequired: action.payload };

    case constants.SET_CHAT_RESENDING_UNCONFIRMED_MESSAGES_REQUIRED:
      return { ...state, resendingUncomfirmedMessagesRequired: action.payload };

    case constants.SET_CHAT_ID:
      return { ...state, chatId: action.payload };

    case constants.SET_CHAT_TYPE:
      return { ...state, chatType: action.payload };

    case constants.SET_CHAT_EARLIEST_MESSAGE_TIME_ONLOAD_BEFORE:
      return { ...state, earliestMessageTimeOnLoadBefore: action.payload };

    case constants.SET_CHAT_SOCKET:
      return { ...state, socket: action.payload };

    case constants.SET_CHAT_TOKEN_REQUEST_STATUS:
      return { ...state, chatTokenRequestStatus: action.payload };

    case constants.SET_CHAT_TOKEN:
      return { ...state, chatToken: action.payload };

    case constants.SET_CHAT_IV:
      return { ...state, chatIV: action.payload };

    case constants.SET_CHAT_MESSAGES:
      return { ...state, messages: action.payload };

    case constants.ADD_CHAT_MESSAGES_SORT:
      return {
        ...state,
        messages: [...state.messages, ...action.payload].sort((a, b) =>
          a.timestamp.epoch_milli > b.timestamp.epoch_milli ? 1 : -1
        )
      };

    case constants.SET_SENDERS:
      return { ...state, senders: action.payload };

    case constants.ADD_SENDER:
      return { ...state, senders: [...state.senders, action.payload] };

    case constants.UPDATE_SENDER:
      return {
        ...state,
        senders: state.senders.map(sender =>
          sender.id === action.payload.id ? action.payload : sender
        )
      };

    case constants.CHAT_RESET:
      return {
        ...INITIAL_STATE,
        senders: [...state.senders],
        chatId: state.chatId,
        chatType: state.chatType,
        earliestMessageTimeOnLoadBefore: Date.now()
      };

    case constants.DELETE_CHAT_MESSAGES_BY_EXTERNAL_UUID_LIST:
      return {
        ...state,
        messages: state.messages.filter(
          cMessage =>
            !action.payload.some(
              uuidToSearch => uuidToSearch === cMessage.external_uuid
            )
        )
      };

    case constants.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };

    default:
      return state;
  }
};
