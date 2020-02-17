import { push } from "connected-react-router";

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
import snackbarActions from "./snackbarActions";

export const fetchProjectMember = (projMemberId: any) => async (
  dispatch: any
) => {
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

export const editProjectMember = (member: any) => async (dispatch: any) => {
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
      dispatch(snackbarActions.success("saved"));
    } else {
      dispatch(setSelectedProjectMemberRequestStatus(constants.FAILED));
      dispatch(snackbarActions.error("Errors.Error"));
    }
  } catch (e) {
    console.error(e);
    if (errorIsEntityNotFound(e)) {
      dispatch(setSelectedProjectMemberRequestStatus(constants.NOT_FOUND));

      dispatch(snackbarActions.error("not_found"));
    } else {
      dispatch(setSelectedProjectMemberRequestStatus(constants.FAILED));

      dispatch(snackbarActions.error("Errors.Error"));
    }
  }
};

const setSelectedProjectMemberFetchStatus = (status: any) => ({
  payload: status,
  type: constants.SET_SELECTED_PROJECT_MEMBER_FETCH_STATUS
});

const setSelectedProjectMemberRequestStatus = (status: any) => ({
  payload: status,
  type: constants.SET_SELECTED_PROJECT_MEMBER_REQUEST_STATUS
});

const setSelectedProjectMember = (member: any) => ({
  payload: member,
  type: constants.SET_SELECTED_PROJECT_MEMBER
});

export const addProjectMembersDispatch = (members: any) => async (
  dispatch: any
) => {
  dispatch(addProjectMembers(members));
};

const addProjectMembers = (members: any) => ({
  payload: members,
  type: constants.ADD_PROJECT_MEMBERS
});

export const filterOutProjectMemberDispatch = (member: any) => async (
  dispatch: any
) => {
  dispatch(filterOutProjectMember(member));
};

const filterOutProjectMember = (member: any) => ({
  payload: member,
  type: constants.FILTER_OUT_PROJECT_MEMBER
});

export const sendApplicationForMembersipInProject = () => {
  // TODO
};

export const fetchProjectMembers = (project_ui_id: any) => async (
  dispatch: any
) => {
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

const setProjectMembersFetchStatus = (status: any) => ({
  payload: status,
  type: constants.SET_PROJECT_MEMBERS_FETCH_STATUS
});

const setProjectMembers = (members: any) => ({
  payload: members,
  type: constants.SET_PROJECT_MEMBERS
});

export const createProject = (data: any) => async (dispatch: any) => {
  try {
    const response = await axios.post(endpoints.PROJECT, data);
    if (responseIsStatusOk(response)) {
      const project_uiid =
        response &&
        response.data &&
        response.data.data &&
        response.data.data[0] &&
        response.data.data[0].ui_id;
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
              project_uiid
            })
          )
        );
      }
    }
  } catch (e) {
    console.error(e);
  }
};

export const updateProject = (data: any) => async (
  dispatch: any,
  getState: any
) => {
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
            project_uiid: data.ui_id
          })
        )
      );
      dispatch(setProjectUpdateStatus(actions.SUCCESS));
      dispatch(snackbarActions.success("Success"));
    }
  } catch (error) {
    console.error(error);
    if (errorIsConflict(error)) {
      dispatch(setProjectUpdateStatus(actions.CONFLICT));
      dispatch(snackbarActions.error("Errors.Conflict"));
    } else {
      dispatch(setProjectUpdateStatus(actions.FAILED));
      dispatch(snackbarActions.error("Errors.Error"));
    }
  }
};

const setProjectUpdateStatus = (status: any) => ({
  payload: status,
  type: constants.SET_PROJECT_UPDATE_STATUS
});

export const getProjectInfoByUIID = (uiid: any) => async (dispatch: any) => {
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

export const fetchTaskById = (id: any) => async (
  dispatch: any,
  getState: any
) => {
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
        response.data.data[0].project.ui_id
      ) {
        dispatch(
          setProjectInfo({
            ...getState().project.info,
            id: response.data.data[0].project.id,
            name: response.data.data[0].project.name,
            ui_id: response.data.data[0].project.ui_id
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

export const fetchTask = (project_uiid: any, task_number: any) => async (
  dispatch: any,
  getState: any
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
        response.data.data[0].project.ui_id
      ) {
        dispatch(
          setProjectInfo({
            ...getState().project.info,
            id: response.data.data[0].project.id,
            name: response.data.data[0].project.name,
            ui_id: response.data.data[0].project.ui_id
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

      dispatch(snackbarActions.error("Errors.Error"));
    }
  }
};

export const fetchProjectByUIID = (
  ui_id: any,
  include_application = false
) => async (dispatch: any, getState: any) => {
  try {
    dispatch(setProjectInfo(null));
    dispatch(setProjectFetchStatus(actions.WAITING));

    const params = { ui_id, include_application };

    const response = await axios.get(endpoints.GET_PROJECT, { params });

    if (responseIsStatusOk(response)) {
      dispatch(setProjectId(response.data.data[0].id));
      dispatch(setProjectUiid(response.data.data[0].ui_id));
      dispatch(setProjectInfo(response.data.data[0]));

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

export const fetchProjectById = (id: any) => async (
  dispatch: any,
  getState: any
) => {
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

const addAttachmentsToNewCreatedTask = async (
  taskId: any,
  attachmentsList: any
) => {
  try {
    await axios.post(endpoints.ATTACHMENT, {
      task: taskId,
      uuids: attachmentsList.map((item: any) => item.uuid)
    });
  } catch (error) {
    console.error(error);
    //dispatch(snackbarActions.error('Errors.Error')) // todo
  }
};

export const createProjectTask = (
  data: any,
  attachmentsList: any,
  bounceAfterwards = false,
  cleanupOnSuccess = () => {}
) => async (dispatch: any, getState: any) => {
  const state = getState();
  try {
    dispatch(setProjectRequestStatus(actions.WAITING));
    const response = await axios.post(endpoints.TASK, data);
    if (responseIsStatusOk(response)) {
      if (response.data.data && response.data.data.length > 0) {
        // todo task created message

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

export const setProjectSelectedTaskInitialStateDispatch = (task: any) => (
  dispatch: any
) => {
  dispatch(setProjectSelectedTaskInitialState(task));
};

export const editProjectTask = (data: any) => async (
  dispatch: any,
  getState: any
) => {
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
      dispatch(snackbarActions.success("Success"));
    } else {
      dispatch(snackbarActions.error("Errors.Error"));
    }
  } catch (error) {
    if (errorIsConflict(error)) {
      dispatch(snackbarActions.error("Errors.Conflict"));
    } else if (errorIsEntityNotFound(error)) {
      dispatch(snackbarActions.error("not_found"));
    } else {
      dispatch(snackbarActions.error("Errors.Error"));
      dispatch(setProjectRequestStatus(actions.FAILED));
    }
    console.error(error);
  } finally {
    dispatch(setProjectRequestStatus(actions.NOT_REQUESTED));
  }
};

export const updateProjectDocumentation = (documentation: any) => async (
  dispatch: any,
  getState: any
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
            project_uiid: getState().project.info.ui_id
          })
        )
      );
      dispatch(snackbarActions.success("Success"));
    } else {
      dispatch(setProjectDocumentationFetchStatus(actions.FAILED));

      dispatch(snackbarActions.error("Errors.Error"));
    }
  } catch (e) {
    console.error(e);
    dispatch(setProjectDocumentationFetchStatus(actions.FAILED));

    dispatch(snackbarActions.error("Errors.Error"));
  } finally {
    dispatch(setProjectDocumentationFetchStatus(actions.NOT_REQUESTED));
  }
};

export const setProjectInfoDispatch = (info: any) => (dispatch: any) => {
  dispatch(setProjectInfo(info));
};

const setProjectDocumentationFetchStatus = (status: any) => ({
  type: actions.SET_PROJECT_DOCUMENTATION_REQUEST_STATUS,
  payload: status
});

const setProjectSelectedTaskInitialState = (task: any) => ({
  payload: task,
  type: constants.SET_PROJECT_SELECTED_TASK_INITIAL_STATE
});

const setSelectedTaskFetchStatus = (status: any) => ({
  payload: status,
  type: constants.SET_PROJECT_SELECTED_TASK_FETCH_STATUS
});

const setProjectId = (id: any) => ({
  type: actions.SET_PROJECT_ID,
  id
});

const setProjectUiid = (uiid: any) => ({
  type: actions.SET_PROJECT_UIID,
  uiid
});

export const setProjectInfo = (info: any) => ({
  type: actions.SET_PROJECT_INFO,
  info
});

export const setProjectFetchStatus = (fetchStatus: any) => ({
  type: actions.SET_PROJECT_FETCH_STATUS,
  fetchStatus
});

export const setProjectRequestStatus = (requestStatus: any) => ({
  type: actions.SET_PROJECT_REQUEST_STATUS,
  requestStatus
});
