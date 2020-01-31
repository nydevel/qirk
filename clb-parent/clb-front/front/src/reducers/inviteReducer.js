import constants from "../utils/constants";

const INITIAL_STATE = {
  inviteUserToProjectRequestStatus: constants.NOT_REQUESTED
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case constants.SET_INVITE_USER_TO_PROJECT_REQUEST_STATUS:
      return { ...state, inviteUserToProjectRequestStatus: action.payload };

    case constants.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };

    default:
      return state;
  }
};
