import actions from "../utils/constants";
import constants from "../utils/constants";

const INITIAL_STATE = {
  id: null,
  uiid: null,
  info: null,
  projectPermissions: {},
  lastTasks: [],
  tasks: {},
  selectedTaskInitialState: null,
  selectedTaskFetchStatus: constants.NOT_REQUESTED,
  fetchStatus: actions.NOT_REQUESTED,
  requestStatus: actions.NOT_REQUESTED,
  documentationRequestStatus: actions.NOT_REQUESTED,
  updateProjectRequestStatus: constants.NOT_REQUESTED,
  membersFetchStatus: constants.NOT_REQUESTED,
  members: [],
  selectedProjectMember: null,
  selectedProjectMemberFetchStatus: constants.NOT_REQUESTED,
  selectedProjectMemberRequestStatus: constants.NOT_REQUESTED
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case constants.SET_SELECTED_PROJECT_MEMBER_REQUEST_STATUS:
      return {
        ...state,
        selectedProjectMemberRequestStatus: action.payload
      };
    case constants.SET_SELECTED_PROJECT_MEMBER_FETCH_STATUS:
      return {
        ...state,
        selectedProjectMemberFetchStatus: action.payload
      };

    case constants.SET_SELECTED_PROJECT_MEMBER:
      return {
        ...state,
        selectedProjectMember: action.payload
      };

    case constants.ADD_PROJECT_MEMBERS:
      return { ...state, members: [...state.members, ...action.payload] };

    case constants.FILTER_OUT_PROJECT_MEMBER:
      return {
        ...state,
        members: state.members.filter(m => m.id !== action.payload.id)
      };

    case constants.SET_PROJECT_MEMBERS_FETCH_STATUS:
      return { ...state, membersFetchStatus: action.payload };

    case constants.SET_PROJECT_MEMBERS:
      return { ...state, members: action.payload };

    case constants.SET_PROJECT_UPDATE_STATUS:
      return { ...state, updateProjectRequestStatus: action.payload };

    case actions.SET_PROJECT_SELECTED_TASK_INITIAL_STATE:
      return { ...state, selectedTaskInitialState: action.payload };
    case actions.SET_PROJECT_SELECTED_TASK_FETCH_STATUS:
      return { ...state, selectedTaskFetchStatus: action.payload };
    case actions.SET_PROJECT_ID:
      return { ...state, id: action.id };
    case actions.SET_PROJECT_UIID:
      return { ...state, uiid: action.uiid };
    case actions.SET_PROJECT_INFO:
      return { ...state, info: action.info };
    case actions.SET_PROJECT_TASKS:
      return { ...state, tasks: action.tasks };
    case actions.SET_LAST_TASKS:
      return { ...state, lastTasks: action.lastTasks };
    case actions.SET_CURRENT_PROJECT_PERMISSIONS:
      return { ...state, projectPermissions: action.projectPermissions };
    case actions.SET_PROJECT_FETCH_STATUS:
      return { ...state, fetchStatus: action.fetchStatus };
    case actions.SET_PROJECT_REQUEST_STATUS:
      return { ...state, requestStatus: action.requestStatus };
    case actions.SET_PROJECT_DOCUMENTATION_REQUEST_STATUS:
      return { ...state, documentationRequestStatus: action.payload };
    case actions.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };
    default:
      return state;
  }
};
