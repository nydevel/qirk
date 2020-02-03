import actions from "../utils/constants";

const INITIAL_STATE = {
  languages: [],
  fetchLanguagesStatus: actions.NOT_REQUESTED,
  requestStatus: actions.NOT_REQUESTED
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case actions.SET_COMMON_REQUEST_STATUS:
      return { ...state, requestStatus: action.requestStatus };
    case actions.SET_FETCH_LANGUAGES_STATUS:
      return { ...state, fetchLanguagesStatus: action.payload };
    case actions.SET_LANGUAGES:
      return { ...state, languages: action.languages };
    case actions.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };
    default:
      return state;
  }
};
