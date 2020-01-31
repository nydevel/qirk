import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import {
  fetchProjectByUIID,
  fetchProjectMembers,
  fetchProjectMember,
  fetchTask,
  fetchTaskById
} from "./../../actions/projectActions";
import {
  fetchOrganizationByUIID,
  fetchOrganizationMembers
} from "./../../actions/organizationActions";
import { fetchLanguages } from "./../../actions/commonActions";
import {
  fetchFoundUserInfo,
  fetchUserProjectMemberships
} from "../../actions/userSearchActions";
import { fetchUserProjectManagerProjects } from "../../actions/userActions";
import { fetchFoundUserInviteToProjectOptions } from "../../actions/userSearchActions";
import constants from "../../utils/constants";
import NotFoundPage from "../../pages/NotFoundPage/NotFoundPage";

function WithFetch({
  children,
  fetchList,
  fetchLanguagesStatus,
  languages,
  fetchOrganizationByUIID,
  fetchProjectByUIID,
  fetchProjectMembers,
  fetchProjectMember,
  fetchLanguages,
  fetchOrganizationMembers,
  fetchTask,
  fetchTaskById,
  fetchFoundUserInfo,
  fetchUserProjectMemberships,
  organizationNotFound,
  projectNotFound,
  taskNotFound,
  projectMemberNotFound,
  fetchUserProjectManagerProjects,
  fetchFoundUserInviteToProjectOptions,
  includeApplication,
  match: { params }
}) {
  const [notFoundPageRequired, setNotFoundPageRequired] = useState(false);

  useEffect(() => {
    if (
      fetchList.some(fetchEnt => fetchEnt === constants.FETCH_PROJECT_MEMBER) &&
      params.project_member_id
    ) {
      fetchProjectMember(params.project_member_id);
    }
  }, [
    fetchList.some(fetchEnt => fetchEnt === constants.FETCH_PROJECT_MEMBER),
    params.project_member_id
  ]);

  useEffect(() => {
    if (
      fetchList.some(fetchEnt => fetchEnt === constants.FETCH_PROJECT_MEMBER) &&
      projectMemberNotFound
    ) {
      setNotFoundPageRequired(true);
    } else {
      setNotFoundPageRequired(false);
    }
  }, [
    fetchList.some(fetchEnt => fetchEnt === constants.FETCH_PROJECT_MEMBER),
    projectMemberNotFound
  ]);

  useEffect(() => {
    if (
      fetchList.some(
        fetchEnt =>
          fetchEnt === constants.FETCH_FOUND_USER_INVITE_TO_PROJECT_OPTIONS
      ) &&
      params.userId
    ) {
      fetchFoundUserInviteToProjectOptions(params.userId);
    }
  }, [
    fetchList.some(
      fetchEnt =>
        fetchEnt === constants.FETCH_FOUND_USER_INVITE_TO_PROJECT_OPTIONS
    ),
    params.userId
  ]);

  useEffect(() => {
    if (
      fetchList.some(
        fetchEnt => fetchEnt === constants.FETCH_USER_PROJECT_MANAGER_PROJECTS
      )
    ) {
      fetchUserProjectManagerProjects();
    }
  }, [
    fetchList.some(
      fetchEnt => fetchEnt === constants.FETCH_USER_PROJECT_MANAGER_PROJECTS
    )
  ]);

  useEffect(() => {
    if (
      fetchList.some(fetchEnt => fetchEnt === constants.LAZY_FETCH_LANGUAGES) &&
      fetchLanguagesStatus === constants.NOT_REQUESTED &&
      (!languages || !languages.length)
    ) {
      fetchLanguages();
    }
  }, [
    fetchList.some(fetchEnt => fetchEnt === constants.LAZY_FETCH_LANGUAGES),
    fetchLanguagesStatus
  ]);

  useEffect(() => {
    if (
      fetchList.some(fetchEnt => fetchEnt === constants.FETCH_ORGANIZATION) &&
      params.organization_uiid
    ) {
      fetchOrganizationByUIID(params.organization_uiid);
    }
  }, [
    fetchList.some(fetchEnt => fetchEnt === constants.FETCH_ORGANIZATION),
    params.organization_uiid
  ]);

  useEffect(() => {
    if (
      fetchList.some(fetchEnt => fetchEnt === constants.FETCH_PROJECT) &&
      params.project_uiid
    ) {
      fetchProjectByUIID(params.project_uiid, includeApplication);
    }
  }, [
    fetchList.some(fetchEnt => fetchEnt === constants.FETCH_PROJECT),
    params.project_uiid
  ]);

  useEffect(() => {
    if (
      fetchList.some(fetchEnt => fetchEnt === constants.FETCH_TASK) &&
      params.project_uiid &&
      params.task_number
    ) {
      fetchTask(params.project_uiid, params.task_number);
    }
  }, [
    fetchList.some(fetchEnt => fetchEnt === constants.FETCH_TASK),
    params.project_uiid,
    params.task_number
  ]);

  useEffect(() => {
    if (
      fetchList.some(fetchEnt => fetchEnt === constants.FETCH_TASK) &&
      params.task_id
    ) {
      fetchTaskById(params.task_id);
    }
  }, [
    fetchList.some(fetchEnt => fetchEnt === constants.FETCH_TASK),
    params.task_id
  ]);

  useEffect(() => {
    if (
      fetchList.some(fetchEnt => fetchEnt === constants.FETCH_USER) &&
      params.userId
    ) {
      fetchFoundUserInfo(params.userId);
    }
  }, [
    fetchList.some(fetchEnt => fetchEnt === constants.FETCH_USER),
    params.userId
  ]);

  useEffect(() => {
    if (
      fetchList.some(
        fetchEnt => fetchEnt === constants.FETCH_USER_PROJECT_MEMBERSHIP
      ) &&
      params.userId
    ) {
      fetchUserProjectMemberships(params.userId);
    }
  }, [
    fetchList.some(
      fetchEnt => fetchEnt === constants.FETCH_USER_PROJECT_MEMBERSHIP
    ),
    params.userId
  ]);

  useEffect(() => {
    if (
      fetchList.some(
        fetchEnt => fetchEnt === constants.FETCH_ORGANIZATION_MEMBERS
      ) &&
      params.organization_uiid
    ) {
      fetchOrganizationMembers(params.organization_uiid);
    }
  }, [
    fetchList.some(
      fetchEnt => fetchEnt === constants.FETCH_ORGANIZATION_MEMBERS
    ),
    params.organization_uiid
  ]);

  useEffect(() => {
    if (
      fetchList.some(
        fetchEnt => fetchEnt === constants.FETCH_PROJECT_MEMBERS
      ) &&
      params.project_uiid
    ) {
      fetchProjectMembers(params.project_uiid);
    }
  }, [
    fetchList.some(fetchEnt => fetchEnt === constants.FETCH_PROJECT_MEMBERS),
    params.project_uiid
  ]);

  useEffect(() => {
    if (
      fetchList.some(fetchEnt => fetchEnt === constants.FETCH_ORGANIZATION) &&
      organizationNotFound
    ) {
      setNotFoundPageRequired(true);
    } else {
      setNotFoundPageRequired(false);
    }
  }, [
    fetchList.some(fetchEnt => fetchEnt === constants.FETCH_ORGANIZATION),
    organizationNotFound
  ]);

  useEffect(() => {
    if (
      fetchList.some(fetchEnt => fetchEnt === constants.FETCH_PROJECT) &&
      projectNotFound
    ) {
      setNotFoundPageRequired(true);
    } else {
      setNotFoundPageRequired(false);
    }
  }, [
    fetchList.some(fetchEnt => fetchEnt === constants.FETCH_PROJECT),
    projectNotFound
  ]);

  useEffect(() => {
    if (
      fetchList.some(fetchEnt => fetchEnt === constants.FETCH_TASK) &&
      taskNotFound
    ) {
      setNotFoundPageRequired(true);
    } else {
      setNotFoundPageRequired(false);
    }
  }, [
    fetchList.some(fetchEnt => fetchEnt === constants.FETCH_TASK),
    taskNotFound
  ]);

  if (notFoundPageRequired) {
    return <NotFoundPage />;
  }

  if (children) {
    return children;
  } else {
    return null;
  }
}

export default withRouter(
  connect(
    state => ({
      fetchLanguagesStatus: state.common.fetchLanguagesStatus,
      languages: state.common.languages,
      organizationNotFound:
        state.organization.fetchStatus === constants.NOT_FOUND,
      projectNotFound: state.project.fetchStatus === constants.NOT_FOUND,
      taskNotFound: state.selectedTask,
      projectMemberNotFound:
        state.project.selectedProjectMemberFetchStatus === constants.NOT_FOUND
    }),
    {
      fetchOrganizationByUIID,
      fetchProjectByUIID,
      fetchProjectMembers,
      fetchProjectMember,
      fetchTask,
      fetchTaskById,
      fetchLanguages,
      fetchOrganizationMembers,
      fetchFoundUserInfo,
      fetchUserProjectMemberships,
      fetchUserProjectManagerProjects,
      fetchFoundUserInviteToProjectOptions
    }
  )(WithFetch)
);
