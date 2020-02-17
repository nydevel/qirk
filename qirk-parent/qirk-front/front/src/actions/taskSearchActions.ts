import constants from "../utils/constants";

export const setTaskSearchFetchStatusDispatch = (status: any) => (
  dispatch: any
) => {
  dispatch(setTaskSearchFetchStatus(status));
};

export const setTaskSearchFetchRequiredDispatch = (isRequired: any) => (
  dispatch: any
) => {
  dispatch(setTaskSearchFetchRequired(isRequired));
};

export const setTaskSearchResultDispatch = (resultArr: any) => (
  dispatch: any
) => {
  dispatch(setTaskSearchResult(resultArr));
};

export const addTaskSearchResultDispatch = (resultArr: any) => (
  dispatch: any
) => {
  dispatch(addTaskSearchResult(resultArr));
};

export const resetTaskSearchDispatch = () => (dispatch: any) => {
  dispatch(resetTaskSearch());
};

export const setLastProjectUiIdDispatch = (uiid: any) => (dispatch: any) => {
  dispatch(setLastProjectUiId(uiid));
};

export const setTaskSearchCountOfAllTasksDispatch = (countOfAllTasks: any) => (
  dispatch: any
) => {
  dispatch(setTaskSearchCountOfAllTasks(countOfAllTasks));
};

export const setTaskSearchFilterSearchAfterDispatch = (searchAfter: any) => (
  dispatch: any
) => {
  dispatch(setTaskSearchFilterSearchAfter(searchAfter));
};

export const setTaskSearchLoadingMissingTasksDispatch = (status: any) => (
  dispatch: any
) => {
  dispatch(setTaskSearchLoadingMissingTasks(status));
};

const setTaskSearchFilterSearchAfter = (searchAfter: any) => ({
  payload: searchAfter,
  type: constants.SET_TASK_SEARCH_FILTER_SEARCH_AFTER
});

const setTaskSearchCountOfAllTasks = (countOfAllTasks: any) => ({
  payload: countOfAllTasks,
  type: constants.SET_TASK_SEARCH_COUNT_OF_ALL_TASKS
});

const setLastProjectUiId = (uiid: any) => ({
  payload: uiid,
  type: constants.SET_TASK_SEARCH_LAST_PROJECT_UI_ID
});

const setTaskSearchFetchStatus = (status: any) => ({
  payload: status,
  type: constants.SET_TASK_SEARCH_FETCH_STATUS
});

const setTaskSearchFetchRequired = (isRequired: any) => ({
  payload: isRequired,
  type: constants.SET_TASK_SEARCH_FETCH_REQUIRED
});

const addTaskSearchResult = (resultArr: any) => ({
  payload: resultArr,
  type: constants.ADD_TASK_SEARCH_RESULT
});

const setTaskSearchResult = (resultArr: any) => ({
  payload: resultArr,
  type: constants.SET_TASK_SEARCH_RESULT
});

const resetTaskSearch = () => ({
  type: constants.RESET_TASK_SEARCH
});

const setTaskSearchLoadingMissingTasks = (status: any) => ({
  payload: status,
  type: constants.SET_TASK_SEARCH_LOADING_MISSING_TASKS
});
