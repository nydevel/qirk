import constants from "../utils/constants";
import { Action } from "../utils/types/Action";
import { NotificationLocal } from "../utils/types/Notification";

export type NotificationReducerState = {
  notifications: NotificationLocal[];
  checkedAtTimestamp: number;
  socket: null | WebSocket;
  iv: null | string;
  token: null | string;
  needToCheckAll: boolean;
  loadAfter: null | any; // todo
};

const INITIAL_STATE: NotificationReducerState = {
  notifications: [],
  checkedAtTimestamp: 1,
  socket: null,
  iv: null,
  token: null,
  needToCheckAll: false,
  loadAfter: null
};

export default (
  state: NotificationReducerState = INITIAL_STATE,
  action: Action
): NotificationReducerState => {
  switch (action.type) {
    case constants.SET_NOTIFICATIONS_LOAD_AFTER:
      return { ...state, loadAfter: action.payload };

    case constants.SET_NOTIFICATIONS_NEED_TO_SEND_CHECK_ALL_REQUEST_TO_SOCKET:
      return { ...state, needToCheckAll: action.payload };

    case constants.NOTIFICATIONS_ADD_NOTIFICATIONS:
      return {
        ...state,
        notifications: [...state.notifications, ...action.payload].sort(
          (a, b) => b.epoch_milli - a.epoch_milli
        )
      };

    case constants.SET_NOTIFICATIONS_CHECKED_AT_TIMESTAMP:
      return { ...state, checkedAtTimestamp: action.payload };

    case constants.SET_NOTIFICATIONS_SOCKET:
      return { ...state, socket: action.payload };

    case constants.SET_NOTIFICATIONS_IV:
      return { ...state, iv: action.payload };

    case constants.SET_NOTIFICATIONS_TOKEN:
      return { ...state, token: action.payload };

    case constants.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };

    default:
      return state;
  }
};
