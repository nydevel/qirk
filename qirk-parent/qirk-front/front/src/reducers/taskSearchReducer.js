import constants from "../utils/constants";
const INITIAL_STATE = {
  searchAfter: null,
  fetchRequired: false,
  fetchStatus: constants.NOT_REQUESTED,
  countOfAllTasks: 0,
  result: [],
  lastOrganizationUiId: null,
  lastProjectUiId: null,
  loadingMissingTasks: false
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case constants.RESET_TASK_SEARCH:
      return { ...INITIAL_STATE };

    case constants.SET_TASK_SEARCH_LAST_ORGANIZATION_UI_ID:
      return { ...state, lastOrganizationUiId: action.payload };
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
