import constants from "../utils/constants";

const INITIAL_STATE = {
  searchFor: "",
  searchResult: [],
  searchResultFetchStatus: constants.NOT_REQUESTED,
  foundProjectInfo: {},
  foundProjectFetchStatus: constants.NOT_REQUESTED
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case constants.SET_SEARCH_FOR_PROJECT:
      return { ...state, searchFor: action.payload };

    case constants.SET_PROJECT_SEARCH_RESULT:
      return { ...state, searchResult: action.payload };
    case constants.SET_PROJECT_SEARCH_RESULT_FETCH_STATUS:
      return { ...state, searchResultFetchStatus: action.payload };

    case constants.SET_FOUND_PROJECT_INFO:
      return { ...state, foundProjectInfo: action.payload };
    case constants.SET_FOUND_PROJECT_INFO_FETCH_STATUS:
      return { ...state, foundProjectFetchStatus: action.payload };

    default:
      return state;
  }
};
