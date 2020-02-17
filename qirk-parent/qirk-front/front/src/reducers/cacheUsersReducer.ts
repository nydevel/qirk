import actions from "../utils/constants";
import { Action } from "../utils/types/Action";
import { User } from "../utils/types/User";

export type CacheUsersReducerState = {
  usersList: { [key: string]: User };
};

const INITIAL_STATE: CacheUsersReducerState = {
  usersList: {}
};

export default (
  state: CacheUsersReducerState = INITIAL_STATE,
  action: Action
): CacheUsersReducerState => {
  switch (action.type) {
    case actions.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };

    case actions.ADD_CACHE_USERS_LIST:
      return { ...state, usersList: { ...state.usersList, ...action.payload } };

    default:
      return state;
  }
};
