import actions from "../utils/constants";
import constants from "../utils/constants";

const INITIAL_STATE = {
  id: null,
  username: null,
  projectManagerProjects: [],
  projectManagerFetchRequestStatus: constants.NOT_REQUESTED
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case actions.SET_USER:
      return { ...state, ...action.user };
    case actions.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };
    case constants.SET_USER_PROJECT_MANAGER_PROJECTS:
      return { ...state, projectManagerProjects: action.payload };
    case constants.SET_USER_PROJECT_MANAGER_FETCH_REQUEST_STATUS:
      return { ...state, projectManagerFetchRequestStatus: action.payload };
    default:
      return state;
  }
};
