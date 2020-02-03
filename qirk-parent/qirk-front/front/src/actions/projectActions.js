import { t } from "i18next";
import { push } from "connected-react-router";
import { toast } from "react-toastify";
import {
  setOrgRequestStatus,
  setOrganizationInfo
} from "./organizationActions";
import axios from "../utils/axios";
import endpoints from "../utils/endpoints";
import actions from "../utils/constants";
import constants from "../utils/constants";
import paths from "../routes/paths";
import {
  errorIsEntityNotFound,
  errorIsConflict,
  responseIsStatusOk,
  errorIsPermissionDenied
} from "../utils/variousUtils";
import queryString from "query-string";
import { takeItemFromLocalStorageSafe } from "../utils/variousUtils";
import { addProjectToFavorites } from "./commonActions";

export const fetchProjectMember = projMemberId => async dispatch => {
  try {
    dispatch(setSelectedProjectMemberFetchStatus(constants.WAITING));

    const response = await axios.get(endpoints.GET_PROJECT_MEMBER, {
      params: {
        id: projMemberId
      }
    });

    if (
      responseIsStatusOk(response) &&
      response.data.data &&
      response.data.data.length > 0
    ) {
      dispatch(setSelectedProjectMember(response.data.data[0]));
      dispatch(setSelectedProjectMemberFetchStatus(constants.SUCCESS));
    } else {
      dispatch(setSelectedProjectMemberFetchStatus(constants.FAILED));
    }
  } catch (e) {
    console.error(e);
    if (errorIsEntityNotFound(e)) {
      dispatch(setSelectedProjectMemberFetchStatus(constants.NOT_FOUND));
    } else {
      dispatch(setSelectedProjectMemberFetchStatus(constants.FAILED));
    }
  }
};

export const editProjectMember = member => async dispatch => {
  try {
    dispatch(setSelectedProjectMemberRequestStatus(constants.WAITING));

    const response = await axios.put(endpoints.PUT_PROJECT_MEMBER, {
      ...member
    });

    if (
      responseIsStatusOk(response) &&
      response.data.data &&
      response.data.data.length > 0
    ) {
      dispatch(setSelectedProjectMember(response.data.data[0]));
      dispatch(setSelectedProjectMemberRequestStatus(constants.SUCCESS));
      toast.success(t("saved"));
    } else {
      dispatch(setSelectedProjectMemberRequestStatus(constants.FAILED));
      toast.error(t("Errors.Error"));
    }
  } catch (e) {
    console.error(e);
    if (errorIsEntityNotFound(e)) {
      dispatch(setSelectedProjectMemberRequestStatus(constants.NOT_FOUND));
      toast.error(t("not_found"));
    } else {
      dispatch(setSelectedProjectMemberRequestStatus(constants.FAILED));
      toast.error(t("Errors.Error"));
    }
  }
};

const setSelectedProjectMemberFetchStatus = status => ({
  payload: status,
  type: constants.SET_SELECTED_PROJECT_MEMBER_FETCH_STATUS
});

const setSelectedProjectMemberRequestStatus = status => ({
  payload: status,
  type: constants.SET_SELECTED_PROJECT_MEMBER_REQUEST_STATUS
});

const setSelectedProjectMember = member => ({
  payload: member,
  type: constants.SET_SELECTED_PROJECT_MEMBER
});

export const addProjectMembersDispatch = members => async dispatch => {
  dispatch(addProjectMembers(members));
};
const addProjectMembers = members => ({
  payload: members,
  type: constants.ADD_PROJECT_MEMBERS
});

export const filterOutProjectMemberDispatch = member => async dispatch => {
  dispatch(filterOutProjectMember(member));
};
const filterOutProjectMember = member => ({
  payload: member,
  type: constants.FILTER_OUT_PROJECT_MEMBER
});

export const sendApplicationForMembersipInProject = () => {
  // TODO
};

export const fetchProjectMembers = project_ui_id => async dispatch => {
  try {
    dispatch(setProjectMembers([]));
    dispatch(setProjectMembersFetchStatus(actions.WAITING));
    const response = await axios.get(
      endpoints.GET_PROJECT_MEMBERS_LIST_BY_PROJECT,
      {
        params: { project_ui_id }
      }
    );
    if (responseIsStatusOk(response) && response.data.data) {
      dispatch(setProjectMembers(response.data.data));
      dispatch(setProjectMembersFetchStatus(actions.SUCCESS));
    }
  } catch (error) {
    console.error(error);
    dispatch(setProjectMembersFetchStatus(actions.FAILED));
  }
};

const setProjectMembersFetchStatus = status => ({
  payload: status,
  type: constants.SET_PROJECT_MEMBERS_FETCH_STATUS
});

const setProjectMembers = members => ({
  payload: members,
  type: constants.SET_PROJECT_MEMBERS
});

export const createProject = data => async (dispatch, getState) => {
  const state = getState();
  try {
    dispatch(setOrgRequestStatus(actions.WAITING));
    const response = await axios.post(endpoints.PROJECT, data);
    if (responseIsStatusOk(response)) {
      const project_uiid =
        response &&
        response.data &&
        response.data.data &&
        response.data.data[0] &&
        response.data.data[0].ui_id;
      dispatch(setOrgRequestStatus(actions.SUCCESS));
      if (data.redirect_to_defhomepage) {
        response &&
          response.data &&
          response.data.data &&
          response.data.data[0] &&
          response.data.data[0].id &&
          dispatch(addProjectToFavorites(response.data.data[0].id));
        dispatch(
          push(
            `${paths.Home.toPath()}?${queryString.stringify({
              project_uiid: project_uiid
            })}`
          )
        );
      } else {
        dispatch(
          push(
            paths.ProjectSingle.toPath({
              organization_uiid:
                (state.organization &&
                  state.organization.info &&
                  state.organization.info.ui_id) ||
                data.organization.ui_id,
              project_uiid
            })
          )
        );
      }
    }
  } catch (e) {
    console.error(e);
    dispatch(setOrgRequestStatus(actions.FAILED));
  } finally {
    dispatch(setOrgRequestStatus(actions.NOT_REQUESTED));
  }
};

export const updateProject = data => async (dispatch, getState) => {
  try {
    dispatch(setProjectUpdateStatus(actions.WAITING));
    const response = await axios.put(endpoints.PROJECT, data);
    if (
      responseIsStatusOk(response) &&
      response.data.data &&
      response.data.data.length > 0
    ) {
      dispatch(setProjectInfo(response.data.data[0]));

      dispatch(
        push(
          paths.ProjectSingle.toPath({
            project_uiid: data.ui_id,
            organization_uiid: getState().organization.info.ui_id
          })
        )
      );
      dispatch(setProjectUpdateStatus(actions.SUCCESS));
      toast.success(t("Success"));
    }
  } catch (error) {
    console.error(error);
    if (errorIsConflict(error)) {
      dispatch(setProjectUpdateStatus(actions.CONFLICT));
      toast.success(t("Errors.Conflict"));
    } else {
      dispatch(setProjectUpdateStatus(actions.FAILED));
      toast.success(t("Errors.Error"));
    }
  }
};

const setProjectUpdateStatus = status => ({
  payload: status,
  type: constants.SET_PROJECT_UPDATE_STATUS
});

export const getProjectInfoByUIID = uiid => async dispatch => {
  try {
    dispatch(setProjectFetchStatus(actions.WAITING));
    const response = await axios.get(endpoints.GET_PROJECT, {
      params: { ui_id: uiid }
    });
    if (responseIsStatusOk(response)) {
      dispatch(setProjectFetchStatus(actions.SUCCESS));
      return response.data.data[0];
    }
  } catch (error) {
    console.error(error);
    dispatch(setProjectFetchStatus(actions.FAILED));
  } finally {
    dispatch(setProjectFetchStatus(actions.NOT_REQUESTED));
  }
};

export const fetchTaskById = id => async (dispatch, getState) => {
  try {
    dispatch(setProjectSelectedTaskInitialState({}));
    dispatch(setSelectedTaskFetchStatus(constants.WAITING));

    const response = await axios.get(endpoints.TASK, {
      params: { id }
    });

    if (
      responseIsStatusOk(response) &&
      response.data.data &&
      response.data.data.length > 0
    ) {
      if (
        response.data.data[0].project &&
        response.data.data[0].project.id &&
        response.data.data[0].project.name &&
        response.data.data[0].project.ui_id &&
        response.data.data[0].project.organization &&
        response.data.data[0].project.organization.id &&
        response.data.data[0].project.organization.name &&
        response.data.data[0].project.organization.ui_id
      ) {
        dispatch(
          setProjectInfo({
            ...getState().project.info,
            id: response.data.data[0].project.id,
            name: response.data.data[0].project.name,
            ui_id: response.data.data[0].project.ui_id
          })
        );
        dispatch(
          setOrganizationInfo({
            ...getState().organization.info,
            id: response.data.data[0].project.organization.id,
            name: response.data.data[0].project.organization.name,
            ui_id: response.data.data[0].project.organization.ui_id
          })
        );
      }
      dispatch(setProjectSelectedTaskInitialState(response.data.data[0]));
      dispatch(setSelectedTaskFetchStatus(actions.SUCCESS));
    }
  } catch (e) {
    console.error(e);
    if (errorIsEntityNotFound(e)) {
      dispatch(setSelectedTaskFetchStatus(constants.NOT_FOUND));
    } else {
      dispatch(setSelectedTaskFetchStatus(constants.FAILED));
    }
  }
};

export const fetchTask = (project_uiid, task_number) => async (
  dispatch,
  getState
) => {
  try {
    dispatch(setProjectSelectedTaskInitialState({}));
    dispatch(setSelectedTaskFetchStatus(constants.WAITING));

    const response = await axios.get(endpoints.TASK, {
      params: { project_ui_id: project_uiid, number: task_number }
    });

    if (
      responseIsStatusOk(response) &&
      response.data.data &&
      response.data.data.length > 0
    ) {
      if (
        response.data.data[0].project &&
        response.data.data[0].project.id &&
        response.data.data[0].project.name &&
        response.data.data[0].project.ui_id &&
        response.data.data[0].project.organization &&
        response.data.data[0].project.organization.id &&
        response.data.data[0].project.organization.name &&
        response.data.data[0].project.organization.ui_id
      ) {
        dispatch(
          setProjectInfo({
            ...getState().project.info,
            id: response.data.data[0].project.id,
            name: response.data.data[0].project.name,
            ui_id: response.data.data[0].project.ui_id
          })
        );
        dispatch(
          setOrganizationInfo({
            ...getState().organization.info,
            id: response.data.data[0].project.organization.id,
            name: response.data.data[0].project.organization.name,
            ui_id: response.data.data[0].project.organization.ui_id
          })
        );
      }
      dispatch(setProjectSelectedTaskInitialState(response.data.data[0]));
      dispatch(setSelectedTaskFetchStatus(actions.SUCCESS));
    }
  } catch (e) {
    console.error(e);
    if (errorIsEntityNotFound(e)) {
      dispatch(setSelectedTaskFetchStatus(constants.NOT_FOUND));
    } else {
      dispatch(setSelectedTaskFetchStatus(constants.FAILED));
      toast.error(t("Errors.Error"));
    }
  }
};

export const fetchProjectByUIID = (ui_id, includeApplication = false) => async (
  dispatch,
  getState
) => {
  try {
    dispatch(setProjectInfo(null));
    dispatch(setProjectFetchStatus(actions.WAITING));

    const params = { ui_id };

    if (includeApplication) {
      params.include_application = true;
    }

    const response = await axios.get(endpoints.GET_PROJECT, { params });

    if (responseIsStatusOk(response)) {
      dispatch(setProjectId(response.data.data[0].id));
      dispatch(setProjectUiid(response.data.data[0].ui_id));
      dispatch(setProjectInfo(response.data.data[0]));
      if (response.data.data[0].organization) {
        dispatch(
          setOrganizationInfo({
            ...getState().organization.info,
            ...response.data.data[0].organization
          })
        );
      }
      dispatch(setProjectFetchStatus(actions.SUCCESS));
    }
  } catch (error) {
    console.error(error);
    if (errorIsPermissionDenied(error)) {
      dispatch(setProjectFetchStatus(actions.PERMISSION_DENIED));

      if (getState().auth.isSignedIn && getState().user.id) {
        // logged in, user info is present
        dispatch(push("/"));
      } else {
        // logged out or no valid user info
        dispatch(push(paths.Login.toPath()));
      }
    } else if (errorIsEntityNotFound(error)) {
      dispatch(setProjectFetchStatus(actions.NOT_FOUND));
    } else {
      dispatch(setProjectFetchStatus(actions.FAILED));
    }
  }
};

export const fetchProjectById = id => async (dispatch, getState) => {
  try {
    dispatch(setProjectFetchStatus(actions.WAITING));
    const projectId = id ? id : getState().project.id;
    const response = await axios.get(
      endpoints.PROJECT_BY_ID.replace("{id}", projectId)
    );
    if (responseIsStatusOk(response)) {
      dispatch(setProjectId(response.data.data[0].id));
      dispatch(setProjectUiid(response.data.data[0].ui_id));
      dispatch(setProjectInfo(response.data.data[0]));
      if (response.data.data[0].organization) {
        dispatch(
          setOrganizationInfo({
            ...getState().organization.info,
            ...response.data.data[0].organization
          })
        );
      }
      dispatch(setProjectFetchStatus(actions.SUCCESS));
    }
  } catch (error) {
    console.error(error);
    if (errorIsEntityNotFound(error)) {
      dispatch(setProjectFetchStatus(actions.NOT_FOUND));
    } else {
      dispatch(setProjectFetchStatus(actions.FAILED));
    }
  } finally {
    dispatch(setProjectFetchStatus(actions.NOT_REQUESTED));
  }
};

const addAttachmentsToNewCreatedTask = async (taskId, attachmentsList) => {
  try {
    const response = await axios.post(endpoints.ATTACHMENT, {
      task: taskId,
      uuids: attachmentsList.map(item => item.uuid)
    });
  } catch (error) {
    console.error(error);
    toast.error(t("Errors.Error"));
  }
};

export const createProjectTask = (
  data,
  attachmentsList,
  bounceAfterwards = false,
  cleanupOnSuccess = () => {}
) => async (dispatch, getState) => {
  const state = getState();
  try {
    dispatch(setProjectRequestStatus(actions.WAITING));
    const response = await axios.post(endpoints.TASK, data);
    if (responseIsStatusOk(response)) {
      if (response.data.data && response.data.data.length > 0) {
        toast.success(
          `${t("task")} #${response.data.data[0].number} ${t("created_lc")}`
        );
        if (attachmentsList && attachmentsList.length > 0) {
          addAttachmentsToNewCreatedTask(
            response.data.data[0].id,
            attachmentsList
          );
        }

        cleanupOnSuccess();
      }
      if (bounceAfterwards) {
        const filterParams = takeItemFromLocalStorageSafe("filter_params", ""); //fulters from localStorage in URL to TaskSearch
        dispatch(
          push(
            `${paths.ProjectSingle.toPath({
              organization_uiid: state.organization.info.ui_id,
              project_uiid: state.project.info.ui_id
            })}?${queryString.stringify(filterParams, {
              arrayFormat: "comma"
            })}`
          )
        );
      }
    }
  } catch (error) {
    console.error(error);
    if (errorIsEntityNotFound(error)) {
      dispatch(push(paths.MyProjects.toPath()));
      dispatch(setProjectRequestStatus(actions.NOT_FOUND));
    } else {
      dispatch(setProjectRequestStatus(actions.FAILED));
    }
  } finally {
    dispatch(setProjectRequestStatus(actions.NOT_REQUESTED));
  }
};

export const setProjectSelectedTaskInitialStateDispatch = task => dispatch => {
  dispatch(setProjectSelectedTaskInitialState(task));
};

export const editProjectTask = data => async (dispatch, getState) => {
  try {
    const state = getState();
    dispatch(setProjectRequestStatus(actions.WAITING));
    const response = await axios.put(endpoints.TASK, data);
    if (responseIsStatusOk(response)) {
      dispatch(
        setProjectSelectedTaskInitialState({
          ...state.project.selectedTaskInitialState,
          ...response.data.data[0],
          assignee: response.data.data[0].assignee,
          updated_at: Date.now(),
          linked_tasks: state.helpForSelectedTask.linkedList
        })
      );
      dispatch(setProjectRequestStatus(actions.SUCCESS));
      toast.success(t("Success"));
    } else {
      toast.error(t("Errors.Error"));
    }
  } catch (error) {
    if (errorIsConflict(error)) {
      toast.error(t("Errors.Conflict"));
    } else if (errorIsEntityNotFound(error)) {
      toast.error(t("not_found"));
    } else {
      toast.error(t("Errors.Error"));
      dispatch(setProjectRequestStatus(actions.FAILED));
    }
    console.error(error);
  } finally {
    dispatch(setProjectRequestStatus(actions.NOT_REQUESTED));
  }
};

export const updateProjectDocumentation = documentation => async (
  dispatch,
  getState
) => {
  try {
    dispatch(setProjectDocumentationFetchStatus(actions.WAITING));

    const result = await axios.put(endpoints.PUT_PROJECT_DOCUMENTATION, {
      id: getState().project.info.id,
      record_version: getState().project.info.record_version,
      documentation
    });

    if (
      responseIsStatusOk(result) &&
      result.data.data &&
      result.data.data.length > 0
    ) {
      dispatch(setProjectInfo(result.data.data[0]));
      dispatch(setProjectDocumentationFetchStatus(actions.SUCCESS));
      dispatch(
        push(
          paths.Documentation.toPath({
            organization_uiid: getState().organization.info.ui_id,
            project_uiid: getState().project.info.ui_id
          })
        )
      );
      toast.success(t("Success"));
    } else {
      dispatch(setProjectDocumentationFetchStatus(actions.FAILED));
      toast.error(t("Errors.Error"));
    }
  } catch (e) {
    console.error(e);
    dispatch(setProjectDocumentationFetchStatus(actions.FAILED));
    toast.error(t("Errors.Error"));
  } finally {
    dispatch(setProjectDocumentationFetchStatus(actions.NOT_REQUESTED));
  }
};

export const setProjectInfoDispatch = info => dispatch => {
  dispatch(setProjectInfo(info));
};

const setProjectDocumentationFetchStatus = status => ({
  type: actions.SET_PROJECT_DOCUMENTATION_REQUEST_STATUS,
  payload: status
});

const setProjectSelectedTaskInitialState = task => ({
  payload: task,
  type: constants.SET_PROJECT_SELECTED_TASK_INITIAL_STATE
});

const setSelectedTaskFetchStatus = status => ({
  payload: status,
  type: constants.SET_PROJECT_SELECTED_TASK_FETCH_STATUS
});

const setProjectId = id => ({
  type: actions.SET_PROJECT_ID,
  id
});

const setProjectUiid = uiid => ({
  type: actions.SET_PROJECT_UIID,
  uiid
});

export const setProjectInfo = info => ({
  type: actions.SET_PROJECT_INFO,
  info
});

export const setProjectFetchStatus = fetchStatus => ({
  type: actions.SET_PROJECT_FETCH_STATUS,
  fetchStatus
});

export const setProjectRequestStatus = requestStatus => ({
  type: actions.SET_PROJECT_REQUEST_STATUS,
  requestStatus
});
