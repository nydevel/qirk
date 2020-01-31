import actions from "../utils/constants";
import constants from "../utils/constants";

const INITIAL_STATE = {
  info: null,
  fetchStatus: actions.NOT_REQUESTED,
  requestStatus: actions.NOT_REQUESTED,
  members: [],
  membersFetchStatus: constants.NOT_REQUESTED,
  leaveRequestStatus: constants.NOT_REQUESTED
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case actions.FILTER_OUT_ORGANIZATION_MEMBER_BY_USER_ID:
      return {
        ...state,
        members: state.members.filter(
          m => m.user && m.user.id !== action.payload
        )
      };

    case constants.SET_LEAVE_ORGANIZATION_REAUEST_STATUS:
      return { ...state, leaveRequestStatus: action.payload };

    case constants.SET_ORG_MEMBERS_FETCH_STATUS:
      return { ...state, membersFetchStatus: action.payload };
    case actions.SET_ORGANIZATION_INFO:
      return { ...state, info: action.payload };
    case actions.SET_ORGANIZATION_FETCH_STATUS:
      return { ...state, fetchStatus: action.fetchStatus };
    case actions.SET_ORGANIZATION_REQUEST_STATUS:
      return { ...state, requestStatus: action.requestStatus };
    case actions.SET_ORGANIZATION_MEMBERS:
      return { ...state, members: action.payload };

    case actions.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };
    case actions.RESET_ORGANIZATION:
      return { ...INITIAL_STATE };
    default:
      return state;
  }
};
