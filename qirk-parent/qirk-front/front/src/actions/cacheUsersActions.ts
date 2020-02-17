import actions from "../utils/constants";
import { User } from "../utils/types/User";
import { Dispatch } from "react";
import { Action } from "../utils/types/Action";

export const addCacheUsersDispatch = (usersList: User[]) => async (
  dispatch: Dispatch<Action>
) => {
  dispatch(addCacheUsers(usersList));
};

const addCacheUsers = (usersList: User[]) => ({
  payload: usersList,
  type: actions.ADD_CACHE_USERS_LIST
});
