import actions from "../utils/constants";

const INITIAL_STATE = {
  usersList: {}
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case actions.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };

    case actions.ADD_CACHE_USERS_LIST:
      return { ...state, usersList: { ...state.usersList, ...action.payload } };

    default:
      return state;
  }
};
