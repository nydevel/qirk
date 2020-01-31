import constants from "../utils/constants";
import endpoints from "../utils/endpoints";
import axios from "../utils/axios";
import {
  errorIsEntityNotFound,
  responseIsStatusOk
} from "../utils/variousUtils";

export const searchUsers = (
  searchString = "",
  dropIfEmpty = false
) => async dispatch => {
  if (!searchString && dropIfEmpty) {
    dispatch(setUserSearchResult([]));
    dispatch(setUserSearchStatus(constants.NOT_REQUESTED));

    return;
  }

  const data = {
    alias: searchString
  };

  try {
    dispatch(setUserSearchStatus(constants.WAITING));
    dispatch(setUserSearchResult([]));
    dispatch(setSearchFor(searchString));

    const response = await axios.get(endpoints.GET_USER_SEARCH_BY_ALIAS, {
      params: data
    });

    if (responseIsStatusOk(response)) {
      dispatch(setUserSearchResult(response.data.data));
      dispatch(setUserSearchStatus(constants.SUCCESS));
    } else {
      dispatch(setUserSearchStatus(constants.FAILED));
    }
  } catch {
    dispatch(setUserSearchStatus(constants.FAILED));
  }
};

export const fetchFoundUserInfo = userId => async dispatch => {
  try {
    dispatch(setFoundUserInfo({}));
    dispatch(setFoundUserInfoFetchStatus(constants.WAITING));
    const response = await axios.get(endpoints.GET_USER, {
      params: {
        id: userId
      }
    });

    if (responseIsStatusOk(response)) {
      dispatch(setFoundUserInfo(response.data.data[0]));
      dispatch(setFoundUserInfoFetchStatus(constants.SUCCESS));
    } else {
      dispatch(setFoundUserInfoFetchStatus(constants.FAILED));
    }
  } catch (e) {
    if (errorIsEntityNotFound(e)) {
      dispatch(setFoundUserInfoFetchStatus(constants.NOT_FOUND));
    } else {
      dispatch(setFoundUserInfoFetchStatus(constants.FAILED));
    }
  }
};

export const fetchUserProjectMemberships = user_id => async dispatch => {
  try {
    dispatch(setUserProjectMemberships([]));
    dispatch(setUserProjectMembershipsFetchStatus(constants.WAITING));

    const response = await axios.get(
      endpoints.GET_PROJECT_MEMBERS_LIST_BY_USER,
      {
        params: {
          user_id
        }
      }
    );

    if (responseIsStatusOk(response) && response.data.data) {
      dispatch(setUserProjectMemberships(response.data.data));
      dispatch(setUserProjectMembershipsFetchStatus(constants.SUCCESS));
    } else {
      dispatch(setUserProjectMembershipsFetchStatus(constants.FAILED));
    }
  } catch (e) {
    if (errorIsEntityNotFound(e)) {
      dispatch(setUserProjectMembershipsFetchStatus(constants.NOT_FOUND));
    } else {
      dispatch(setUserProjectMembershipsFetchStatus(constants.FAILED));
    }
  }
};

export const fetchFoundUserInviteToProjectOptions = user_id => async dispatch => {
  try {
    dispatch(setFoundUserInviteToProjectOptionsFetchStatus(constants.WAITING));
    const response = await axios.get(
      endpoints.GET_USER_INVITE_TO_PROJECT_OPTIONS,
      {
        params: {
          user_id
        }
      }
    );

    if (responseIsStatusOk(response) && response.data.data) {
      dispatch(setFoundUserInviteToProjectOptions(response.data.data));
      dispatch(
        setFoundUserInviteToProjectOptionsFetchStatus(constants.SUCCESS)
      );
    } else {
      dispatch(setFoundUserInviteToProjectOptionsFetchStatus(constants.FAILED));
    }
  } catch {
    dispatch(setFoundUserInviteToProjectOptionsFetchStatus(constants.FAILED));
  }
};

export const setInviteToProjectOptionInviteByProjectId = (
  projectId,
  invite
) => ({
  payload: { projectId, invite },
  type: constants.SET_INVITE_TO_PROJECT_OPTION_INVITE_BY_PROJECT_ID
});

const setFoundUserInviteToProjectOptions = projectOptions => ({
  payload: projectOptions,
  type: constants.SET_FOUND_USER_INVITE_TO_PROJECT_OPTIONS
});

const setFoundUserInviteToProjectOptionsFetchStatus = status => ({
  payload: status,
  type: constants.SET_FOUND_USER_INVITE_TO_PROJECT_OPTIONS_FETCH_STATUS
});

const setUserProjectMemberships = memberships => ({
  payload: memberships,
  type: constants.SET_USER_PROJECT_MEMBERSHIPS
});

const setUserProjectMembershipsFetchStatus = status => ({
  payload: status,
  type: constants.SET_USER_PROJECT_MEMBERSHIPS_FETCH_STATUS
});

const setUserSearchResult = resultList => ({
  type: constants.SET_USER_SEARCH_RESULT,
  payload: resultList
});

const setSearchFor = searchString => ({
  type: constants.SET_SEARCH_FOR_USER,
  payload: searchString
});

const setUserSearchStatus = status => ({
  type: constants.SET_USER_SEARCH_RESULT_FETCH_STATUS,
  payload: status
});

const setFoundUserInfo = userInfo => ({
  type: constants.SET_FOUND_USER_INFO,
  payload: userInfo
});

const setFoundUserInfoFetchStatus = status => ({
  type: constants.SET_FOUND_USER_INFO_FETCH_STATUS,
  payload: status
});
