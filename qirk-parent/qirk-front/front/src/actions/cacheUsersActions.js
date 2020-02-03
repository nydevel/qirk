import actions from "../utils/constants";

export const addCacheUsersDispatch = usersList => async dispatch => {
  dispatch(addCacheUsers(usersList));
};

const addCacheUsers = usersList => ({
  payload: usersList,
  type: actions.ADD_CACHE_USERS_LIST
});
