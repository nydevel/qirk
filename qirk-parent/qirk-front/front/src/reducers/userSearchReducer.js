import constants from "../utils/constants";

const INITIAL_STATE = {
  searchFor: "",
  searchResult: [],
  searchResultFetchStatus: constants.NOT_REQUESTED,

  foundUserInfo: {},
  foundUserFetchStatus: constants.NOT_REQUESTED,

  foundUserProjectMemberships: [],
  foundUserProjectMembershipsFetchStatus: constants.NOT_REQUESTED,

  foundUserInviteToProjectOptions: [],
  foundUserInviteToProjectOptionsFetchStatus: constants.NOT_REQUESTED
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case constants.SET_INVITE_TO_PROJECT_OPTION_INVITE_BY_PROJECT_ID:
      return {
        ...state,
        foundUserInviteToProjectOptions: state.foundUserInviteToProjectOptions.map(
          opt =>
            opt.id === action.payload.projectId
              ? {
                  ...opt,
                  invite: action.payload.invite
                }
              : { ...opt }
        )
      };

    case constants.SET_USER_PROJECT_MEMBERSHIPS_FETCH_STATUS:
      return {
        ...state,
        foundUserProjectMembershipsFetchStatus: action.payload
      };

    case constants.SET_USER_PROJECT_MEMBERSHIPS:
      return { ...state, foundUserProjectMemberships: action.payload };

    case constants.SET_SEARCH_FOR_USER:
      return { ...state, searchFor: action.payload };

    case constants.SET_USER_SEARCH_RESULT:
      return { ...state, searchResult: action.payload };
    case constants.SET_USER_SEARCH_RESULT_FETCH_STATUS:
      return { ...state, searchResultFetchStatus: action.payload };

    case constants.SET_FOUND_USER_INFO:
      return { ...state, foundUserInfo: action.payload };

    case constants.SET_FOUND_USER_INFO_FETCH_STATUS:
      return { ...state, foundUserFetchStatus: action.payload };

    case constants.SET_FOUND_USER_INVITE_TO_PROJECT_OPTIONS:
      return { ...state, foundUserInviteToProjectOptions: action.payload };

    case constants.SET_FOUND_USER_INVITE_TO_PROJECT_OPTIONS_FETCH_STATUS:
      return {
        ...state,
        foundUserInviteToProjectOptionsFetchStatus: action.payload
      };

    default:
      return state;
  }
};
