import actions from "../utils/constants";
import constants from "../utils/constants";
import { Project } from "../utils/types/Project";
import { Task } from "../utils/types/Task";
import { RequestStatus } from "../utils/enums/RequestStatus";
import { ProjectMember } from "../utils/types/ProjectMember";
import { Action } from "../utils/types/Action";

export type ProjectReducerState = {
  info: Project | null;
  projectPermissions: any; // todo, how long have that been sitting in here?
  lastTasks: any; // todo, and, btw, wut??
  tasks: any; //Task[]; // todo, whatt??
  selectedTaskInitialState: Task | null;
  selectedTaskFetchStatus: RequestStatus;
  fetchStatus: RequestStatus;
  requestStatus: RequestStatus;
  documentationRequestStatus: RequestStatus;
  updateProjectRequestStatus: RequestStatus;
  membersFetchStatus: RequestStatus;
  members: ProjectMember[];
  selectedProjectMember: null | ProjectMember;
  selectedProjectMemberFetchStatus: RequestStatus;
  selectedProjectMemberRequestStatus: RequestStatus;
};

const INITIAL_STATE: ProjectReducerState = {
  info: null,
  projectPermissions: {},
  lastTasks: [],
  tasks: {},
  selectedTaskInitialState: null,
  selectedTaskFetchStatus: RequestStatus.NOT_REQUESTED,
  fetchStatus: RequestStatus.NOT_REQUESTED,
  requestStatus: RequestStatus.NOT_REQUESTED,
  documentationRequestStatus: RequestStatus.NOT_REQUESTED,
  updateProjectRequestStatus: RequestStatus.NOT_REQUESTED,
  membersFetchStatus: RequestStatus.NOT_REQUESTED,
  members: [],
  selectedProjectMember: null,
  selectedProjectMemberFetchStatus: RequestStatus.NOT_REQUESTED,
  selectedProjectMemberRequestStatus: RequestStatus.NOT_REQUESTED
};

export default (
  state: ProjectReducerState = INITIAL_STATE,
  action: Action | any // todo
): ProjectReducerState => {
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
