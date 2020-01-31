import constants from "../utils/constants";

const INITIAL_STATE = {
  list: [
    {
      chat_type: "TASK",
      chat_id: 1,
      last_Message: {
        sender_id: 1,
        timestamp: { iso8601: "2019-03-20T17:12:36.3+0500", epoch_milli: 1333 },
        message: "aaa"
      }
    },
    {
      chat_type: "TASK",
      chat_id: 1,
      last_Message: {
        sender_id: 1,
        timestamp: { iso8601: "2019-03-20T17:12:36.3+0500", epoch_milli: 123 },
        message: "bbb"
      }
    }
  ],
  fetchStatus: constants.NOT_REQUESTED
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case constants.SET_MY_CHATS_LIST:
      return { ...state, list: action.payload };
    case constants.SET_MY_CHATS_FETCH_STATUS:
      return { ...state, fetchStatus: action.payload };
    case constants.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };
    default:
      return state;
  }
};
