import constants from "../utils/constants";

const INITIAL_STATE = {
  searchFor: "",
  searchResult: [],
  searchResultFetchStatus: constants.NOT_REQUESTED
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case constants.SET_SEARCH_FOR_ORGANIZATION:
      return { ...state, searchFor: action.payload };

    case constants.SET_ORGANIZATION_SEARCH_RESULT:
      return { ...state, searchResult: action.payload };

    case constants.SET_ORGANIZATION_SEARCH_RESULT_FETCH_STATUS:
      return { ...state, searchResultFetchStatus: action.payload };

    default:
      return state;
  }
};
