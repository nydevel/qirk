import { push } from "connected-react-router";
import { toast } from "react-toastify";
import { t } from "i18next";
import axios from "../utils/axios";
import endpoints from "../utils/endpoints";
import actions from "../utils/constants";
import paths from "../routes/paths";
import constants from "../utils/constants";
import {
  errorIsConflict,
  responseIsStatusOk,
  errorIsEntityNotFound
} from "../utils/variousUtils";

const filterOutOrganizationMemberByUserId = id => ({
  payload: id,
  type: constants.FILTER_OUT_ORGANIZATION_MEMBER_BY_USER_ID
});

export const leaveOrganization = orgUiId => async (dispatch, getState) => {
  try {
    dispatch(setLeaveRequestStatus(actions.WAITING));

    const response = await axios.delete(endpoints.ORG_MEMBER_LEAVE, {
      data: { organization: { ui_id: orgUiId } }
    });

    if (responseIsStatusOk(response)) {
      const currentOrg = getState().organization.info;
      if (currentOrg && currentOrg.ui_id === orgUiId) {
        dispatch(
          setOrganizationInfo({
            ...currentOrg,
            is_member: false,
            can_manage: false
          })
        );
        dispatch(filterOutOrganizationMemberByUserId(getState().user.id));
      }
      toast.success(t("you_left_organization"));
      dispatch(setLeaveRequestStatus(actions.SUCCESS));
    }
  } catch (e) {
    console.error(e);
    dispatch(setLeaveRequestStatus(actions.FAILED));
    toast.error(t("Errors.Error"));
  }
};

const setLeaveRequestStatus = status => ({
  payload: status,
  type: constants.SET_LEAVE_ORGANIZATION_REAUEST_STATUS
});

export const createOrganization = data => async dispatch => {
  try {
    dispatch(setOrgRequestStatus(actions.WAITING));
    const response = await axios.post(endpoints.ORGANIZATION, {
      name: data.name,
      ui_id: data.ui_id,
      private: data.private,
      languages: data.languages
    });
    if (
      responseIsStatusOk(response) &&
      response.data.data &&
      response.data.data.length > 0
    ) {
      dispatch(setOrgRequestStatus(actions.SUCCESS));
      dispatch(push(paths.MyOrganizations.toPath()));
    }
  } catch {
    dispatch(setOrgRequestStatus(actions.FAILED));
  } finally {
    dispatch(setOrgRequestStatus(actions.NOT_REQUESTED));
  }
};

export const editOrganization = data => async dispatch => {
  try {
    dispatch(setOrgRequestStatus(actions.WAITING));
    const response = await axios.put(endpoints.ORGANIZATION, data);
    if (
      responseIsStatusOk(response) &&
      response.data.data &&
      response.data.data.length > 0
    ) {
      dispatch(setOrganizationInfo(response.data.data[0]));
      dispatch(setOrgRequestStatus(actions.SUCCESS));
    }
  } catch (error) {
    if (errorIsConflict(error)) {
      dispatch(setOrgRequestStatus(actions.CONFLICT));
      dispatch(
        push(
          paths.OrganizationOverview.toPath({
            organization_uiid: data && data.ui_id
          })
        )
      );
    } else {
      dispatch(setOrgRequestStatus(actions.FAILED));
    }
  } finally {
    dispatch(setOrgRequestStatus(actions.NOT_REQUESTED));
  }
};

export const fetchOrganizationByUIID = uiid => async dispatch => {
  try {
    dispatch(resetOrganization());
    dispatch(setOrgFetchStatus(actions.WAITING));
    const response = await axios.get(
      endpoints.ORGANIZATION_BY_UIID.replace("{ui_id}", uiid)
    );
    if (responseIsStatusOk(response)) {
      dispatch(setOrganizationInfo(response.data.data[0]));
      dispatch(setOrgFetchStatus(actions.SUCCESS));
    }
  } catch (error) {
    if (errorIsEntityNotFound(error)) {
      dispatch(setOrgFetchStatus(actions.NOT_FOUND));
    } else {
      dispatch(setOrgFetchStatus(actions.FAILED));
    }
  }
};

export const fetchOrganizationById = id => async (dispatch, getState) => {
  try {
    dispatch(setOrgFetchStatus(actions.WAITING));
    const orgId = id ? id : getState().organization.id;
    const response = await axios.get(
      endpoints.ORGANIZATION_BY_ID.replace("{id}", orgId)
    );
    if (responseIsStatusOk(response)) {
      dispatch(setOrganizationInfo(response.data.data[0]));
      dispatch(setOrgFetchStatus(actions.SUCCESS));
    }
  } catch (error) {
    if (errorIsEntityNotFound(error)) {
      dispatch(setOrgFetchStatus(actions.NOT_FOUND));
    } else {
      dispatch(setOrgFetchStatus(actions.FAILED));
    }
  }
};

export const fetchOrganizationMembers = organization_uiid => async dispatch => {
  try {
    dispatch(setOrgMembersFetchStatus(actions.WAITING));
    dispatch(setOrganizationMembers([]));
    const response = await axios.get(
      endpoints.ORG_MEMBERS_LIST.replace("{org_uiid}", organization_uiid)
    );
    if (responseIsStatusOk(response) && response.data.data) {
      dispatch(setOrganizationMembers(response.data.data));
      dispatch(setOrgMembersFetchStatus(actions.SUCCESS));
    }
  } catch {
    dispatch(setOrgMembersFetchStatus(actions.FAILED));
  } finally {
    dispatch(setOrgMembersFetchStatus(actions.NOT_REQUESTED));
  }
};

const setOrgMembersFetchStatus = status => ({
  payload: status,
  type: constants.SET_ORG_MEMBERS_FETCH_STATUS
});

export const addOrganizationMember = data => async (dispatch, getState) => {
  const state = getState();
  try {
    dispatch(setOrgRequestStatus(actions.WAITING));
    const response = await axios.post(endpoints.ORG_MEMBER, data);
    if (responseIsStatusOk(response)) {
      dispatch(setOrgRequestStatus(actions.SUCCESS));
      dispatch(
        push(
          paths.OrganizationMembers.toPath({
            organization_uiid:
              (state.organization.info && state.organization.info.ui_id) ||
              "undefined-organization"
          })
        )
      );
    }
  } catch {
    dispatch(setOrgRequestStatus(actions.FAILED));
  } finally {
    dispatch(setOrgRequestStatus(actions.NOT_REQUESTED));
  }
};

export const editOrganizationMember = data => async (dispatch, getState) => {
  const state = getState();
  try {
    dispatch(setOrgRequestStatus(actions.WAITING));
    const response = await axios.put(endpoints.ORG_MEMBER, data);
    if (responseIsStatusOk(response)) {
      dispatch(setOrgRequestStatus(actions.SUCCESS));
      dispatch(
        push(
          paths.OrganizationMembers.toPath({
            organization_uiid: state.organization.info.ui_id
          })
        )
      );
    }
  } catch (error) {
    if (errorIsConflict(error)) {
      dispatch(setOrgRequestStatus(actions.CONFLICT));
      dispatch(
        push(
          paths.OrganizationMembers.toPath({
            organization_uiid: state.organization.info.ui_id
          })
        )
      );
    } else {
      dispatch(setOrgRequestStatus(actions.FAILED));
    }
  } finally {
    dispatch(setOrgRequestStatus(actions.NOT_REQUESTED));
  }
};

export const removeOrganizationMember = memberId => async (
  dispatch,
  getState
) => {
  try {
    dispatch(setOrgRequestStatus(actions.WAITING));
    const response = await axios.delete(endpoints.ORG_MEMBER, {
      data: { id: memberId }
    });
    if (responseIsStatusOk(response)) {
      dispatch(
        setOrganizationMembers(
          getState().organization.members.filter(e => e.id !== memberId)
        )
      );
      dispatch(setOrgRequestStatus(actions.SUCCESS));
    }
  } catch {
    dispatch(setOrgRequestStatus(actions.FAILED));
  } finally {
    dispatch(setOrgRequestStatus(actions.NOT_REQUESTED));
  }
};

export const setOrganizationInfoDispatch = info => dispatch => {
  dispatch(setOrganizationInfo(info));
};

export const setOrganizationInfo = info => ({
  payload: info,
  type: actions.SET_ORGANIZATION_INFO
});

const setOrganizationMembers = organizationMembers => ({
  payload: organizationMembers,
  type: actions.SET_ORGANIZATION_MEMBERS
});

const setOrganizationProjects = organizationProjects => ({
  type: actions.SET_ORGANIZATION_PROJECTS,
  organizationProjects
});

const resetOrganization = () => ({
  type: actions.RESET_ORGANIZATION
});

export const setOrgFetchStatus = fetchStatus => ({
  type: actions.SET_ORGANIZATION_FETCH_STATUS,
  fetchStatus
});

export const setOrgRequestStatus = requestStatus => ({
  type: actions.SET_ORGANIZATION_REQUEST_STATUS,
  requestStatus
});
