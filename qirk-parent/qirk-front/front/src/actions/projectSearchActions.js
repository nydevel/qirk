import constants from "../utils/constants";
import endpoints from "../utils/endpoints";
import axios from "../utils/axios";
import { responseIsStatusOk } from "../utils/variousUtils";

export const searchProjects = (
  searchString = "",
  dropIfEmpty = false
) => async dispatch => {
  if (!searchString && dropIfEmpty) {
    dispatch(setProjectSearchResult([]));
    dispatch(setProjectSearchStatus(constants.NOT_REQUESTED));
    return;
  }

  try {
    dispatch(setProjectSearchStatus(constants.WAITING));
    dispatch(setProjectSearchResult([]));
    dispatch(setSearchFor(searchString));

    let response = null;

    if (searchString) {
      response = await axios.get(endpoints.GET_SEARCH_PROJECT, {
        params: {
          text: searchString
        }
      });
    } else {
      response = await axios.get(endpoints.GET_PROJECT_TOP);
    }

    if (responseIsStatusOk(response) && response.data.data) {
      dispatch(setProjectSearchResult(response.data.data));
      dispatch(setProjectSearchStatus(constants.SUCCESS));
    } else {
      dispatch(setProjectSearchStatus(constants.FAILED));
    }
  } catch {
    dispatch(setProjectSearchStatus(constants.FAILED));
  }
};

export const setProjectSearchResult = resultList => ({
  type: constants.SET_PROJECT_SEARCH_RESULT,
  payload: resultList
});

export const setSearchFor = searchString => ({
  type: constants.SET_SEARCH_FOR_PROJECT,
  payload: searchString
});

const setProjectSearchStatus = status => ({
  type: constants.SET_PROJECT_SEARCH_RESULT_FETCH_STATUS,
  payload: status
});
