import actions from "../utils/constants";
import constants from "../utils/constants";
import { User } from "../utils/types/User";
import { RequestStatus } from "../utils/enums/RequestStatus";
// import { Action } from "../utils/types/Action";

export type UserReducerState = {
  id: null | User["id"];
  username: null | User["username"];
  projectManagerProjects: any; // todo
  projectManagerFetchRequestStatus: RequestStatus;
};

const INITIAL_STATE: UserReducerState = {
  id: null,
  username: null,
  projectManagerProjects: [],
  projectManagerFetchRequestStatus: RequestStatus.NOT_REQUESTED
};

export default (
  state: UserReducerState = INITIAL_STATE,
  action: any // Action // todo
): UserReducerState => {
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
