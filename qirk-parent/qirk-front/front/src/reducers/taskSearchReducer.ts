import constants from "../utils/constants";
import { RequestStatus } from "../utils/enums/RequestStatus";
import { Task } from "../utils/types/Task";
import { Project } from "../utils/types/Project";
import { Action } from "../utils/types/Action";

export type TaskSearchReducerState = {
  searchAfter: any; // todo
  fetchRequired: boolean;
  fetchStatus: RequestStatus;
  countOfAllTasks: number;
  result: Task[]; // todo ?
  lastProjectUiId: Project["ui_id"] | null;
  loadingMissingTasks: boolean;
};

const INITIAL_STATE: TaskSearchReducerState = {
  searchAfter: null,
  fetchRequired: false,
  fetchStatus: RequestStatus.NOT_REQUESTED,
  countOfAllTasks: 0,
  result: [],
  lastProjectUiId: null,
  loadingMissingTasks: false
};

export default (
  state: TaskSearchReducerState = INITIAL_STATE,
  action: Action
): TaskSearchReducerState => {
  switch (action.type) {
    case constants.RESET_TASK_SEARCH:
      return { ...INITIAL_STATE };
    case constants.SET_TASK_SEARCH_LAST_PROJECT_UI_ID:
      return { ...state, lastProjectUiId: action.payload };
    case constants.SET_TASK_SEARCH_FETCH_REQUIRED:
      return { ...state, fetchRequired: action.payload };
    case constants.SET_TASK_SEARCH_FETCH_STATUS:
      return { ...state, fetchStatus: action.payload };
    case constants.SET_TASK_SEARCH_RESULT:
      return { ...state, result: action.payload };
    case constants.ADD_TASK_SEARCH_RESULT:
      return { ...state, result: [...state.result, ...action.payload] };
    case constants.SET_TASK_SEARCH_COUNT_OF_ALL_TASKS:
      return { ...state, countOfAllTasks: action.payload };
    case constants.SET_TASK_SEARCH_LOADING_MISSING_TASKS:
      return { ...state, loadingMissingTasks: action.payload };
    case constants.SET_TASK_SEARCH_FILTER_SEARCH_AFTER:
      return {
        ...state,
        searchAfter: action.payload
      };
    case constants.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };

    default:
      return state;
  }
};
