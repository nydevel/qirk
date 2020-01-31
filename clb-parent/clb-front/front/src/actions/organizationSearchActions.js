import constants from "../utils/constants";
import endpoints from "../utils/endpoints";
import axios from "../utils/axios";
import { responseIsStatusOk } from "../utils/variousUtils";

export const searchOrganizations = (
  searchString = "",
  dropIfEmpty = false
) => async dispatch => {
  if (!searchString && dropIfEmpty) {
    dispatch(setOrganizationSearchResult([]));
    dispatch(setOrganizationSearchStatus(constants.NOT_REQUESTED));
    return;
  }

  try {
    dispatch(setOrganizationSearchStatus(constants.WAITING));
    dispatch(setOrganizationSearchResult([]));
    dispatch(setSearchFor(searchString));

    const response = await axios.get(endpoints.GET_SEARCH_ORGANIZATION, {
      params: {
        prefix: searchString
      }
    });

    if (responseIsStatusOk(response)) {
      dispatch(setOrganizationSearchResult(response.data.data));
      dispatch(setOrganizationSearchStatus(constants.SUCCESS));
    } else {
      dispatch(setOrganizationSearchStatus(constants.FAILED));
    }
  } catch {
    dispatch(setOrganizationSearchStatus(constants.FAILED));
  }
};

const setOrganizationSearchResult = resultList => ({
  type: constants.SET_ORGANIZATION_SEARCH_RESULT,
  payload: resultList
});

const setSearchFor = searchString => ({
  type: constants.SET_SEARCH_FOR_ORGANIZATION,
  payload: searchString
});

const setOrganizationSearchStatus = status => ({
  type: constants.SET_ORGANIZATION_SEARCH_RESULT_FETCH_STATUS,
  payload: status
});
