import constants from "../utils/constants";

export const setTaskSearchFetchStatusDispatch = status => dispatch => {
  dispatch(setTaskSearchFetchStatus(status));
};

export const setTaskSearchFetchRequiredDispatch = isRequired => dispatch => {
  dispatch(setTaskSearchFetchRequired(isRequired));
};

export const setTaskSearchResultDispatch = resultArr => dispatch => {
  dispatch(setTaskSearchResult(resultArr));
};

export const addTaskSearchResultDispatch = resultArr => dispatch => {
  dispatch(addTaskSearchResult(resultArr));
};

export const resetTaskSearchDispatch = () => dispatch => {
  dispatch(resetTaskSearch());
};

export const setLastProjectUiIdDispatch = uiid => dispatch => {
  dispatch(setLastProjectUiId(uiid));
};

export const setTaskSearchCountOfAllTasksDispatch = countOfAllTasks => dispatch => {
  dispatch(setTaskSearchCountOfAllTasks(countOfAllTasks));
};

export const setTaskSearchFilterSearchAfterDispatch = searchAfter => dispatch => {
  dispatch(setTaskSearchFilterSearchAfter(searchAfter));
};

export const setTaskSearchLoadingMissingTasksDispatch = status => dispatch => {
  dispatch(setTaskSearchLoadingMissingTasks(status));
};

const setTaskSearchFilterSearchAfter = searchAfter => ({
  payload: searchAfter,
  type: constants.SET_TASK_SEARCH_FILTER_SEARCH_AFTER
});

const setTaskSearchCountOfAllTasks = countOfAllTasks => ({
  payload: countOfAllTasks,
  type: constants.SET_TASK_SEARCH_COUNT_OF_ALL_TASKS
});

const setLastProjectUiId = uiid => ({
  payload: uiid,
  type: constants.SET_TASK_SEARCH_LAST_PROJECT_UI_ID
});



const setTaskSearchFetchStatus = status => ({
  payload: status,
  type: constants.SET_TASK_SEARCH_FETCH_STATUS
});

const setTaskSearchFetchRequired = isRequired => ({
  payload: isRequired,
  type: constants.SET_TASK_SEARCH_FETCH_REQUIRED
});

const addTaskSearchResult = resultArr => ({
  payload: resultArr,
  type: constants.ADD_TASK_SEARCH_RESULT
});

const setTaskSearchResult = resultArr => ({
  payload: resultArr,
  type: constants.SET_TASK_SEARCH_RESULT
});

const resetTaskSearch = () => ({
  type: constants.RESET_TASK_SEARCH
});

const setTaskSearchLoadingMissingTasks = status => ({
  payload: status,
  type: constants.SET_TASK_SEARCH_LOADING_MISSING_TASKS
});
