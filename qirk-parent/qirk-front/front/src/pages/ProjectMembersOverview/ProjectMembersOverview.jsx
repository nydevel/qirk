import React, { useEffect, useState } from "react";

import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import Page from "../../components/Page/Page";
import Loading from "../../components/Loading/Loading";
import ProjectMemberListItem from "./ProjectMemberListItem";
import WithFetch from "../../components/WithFetch/WithFetch";
import constants from "../../utils/constants";
import axios from "../../utils/axios";
import AddProjectMemberForm from "../../components/AddProjectMemberForm/AddProjectMemberForm";
import ProjectMemberInviteListItem from "./ProjectMemberInviteListItem";
import { toast } from "react-toastify";
import endpoints from "../../utils/endpoints";
import { responseIsStatusOk } from "../../utils/variousUtils";
import { useTranslation } from "react-i18next";

function ProjectMembersOverview({
  fetchInProgress,
  members,
  canManage,
  projectInfo,
  match: {
    params: { project_uiid }
  }
}) {const{t}=useTranslation()
  const [invites, setInvites] = useState([]);
  const [isFetchingInvites, setIsFetchingInvites] = useState(false);
  const [cancelRequstInProgress, setCancelRequestInProgress] = useState(false);

  const cancelInvite = async id => {
    if (cancelRequstInProgress) {
      return toast.error(t("this_request_is_already_in_progress"));
    }

    try {
      setCancelRequestInProgress(true);

      const response = await axios.delete(
        endpoints.DELETE_GRANTED_PERMISSIONS_PROJECT_INVITE_CANCEL,
        {
          data: {
            id
          }
        }
      );
      if (responseIsStatusOk(response)) {
        setInvites(invites.filter(inv => inv.id !== id));
        toast.success(t("Success"));
      } else {
        toast.error(t("Errors.Error"));
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setCancelRequestInProgress(false);
    }
  };

  const fetchPredefinedPermissionsInvites = async () => {
    try {
      setIsFetchingInvites(true);

      const response = await axios.get(
        endpoints.GET_GRANTED_PERMISSIONS_PROJECT_INVITE_LIST_BY_PROJECT,
        {
          params: {
            project_ui_id: project_uiid,
            include_email: true
          }
        }
      );

      if (responseIsStatusOk(response)) {
        console.log("response.data.data", response.data.data);
        setInvites(
          response.data.data.filter(
            inv => inv.status && inv.status.name_code === "PENDING"
          )
        );
      }
    } catch (e) {
      toast.error(t("Errors.Error"));
      console.error(e);
    } finally {
      setIsFetchingInvites(false);
    }
  };

  useEffect(() => {
    if (
      projectInfo &&
      projectInfo.ui_id === project_uiid &&
      projectInfo.can_manage
    ) {
      fetchPredefinedPermissionsInvites();
    }
  }, [projectInfo, project_uiid]);

  return (
    <Page>
      <WithFetch
        fetchList={[constants.FETCH_PROJECT_MEMBERS, constants.FETCH_PROJECT]}
      >
        <h1>{t("project_members")}</h1>

        {(fetchInProgress || isFetchingInvites) && <Loading />}

        {canManage && (
          <AddProjectMemberForm
            onInvitePosted={invite => setInvites([...invites, { ...invite }])}
          />
        )}

        {((members && members.length > 0) || (invites && invites.length > 0)) &&
          !fetchInProgress &&
          !isFetchingInvites && (
            <Paper>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>{t("user")}</TableCell>
                    <TableCell>{t("status")}</TableCell>
                    <TableCell align="center">{t("writing_allowed")}</TableCell>
                    <TableCell align="center">{t("project_manager")}</TableCell>
                    <TableCell align="right">{t("actions")}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {invites.map(invite => (
                    <ProjectMemberInviteListItem
                      invite={invite}
                      projectUiId={project_uiid}
                      cancelInvite={cancelInvite}
                      key={`invite_${invite.id}`}
                    />
                  ))}
                  {members.map(member => (
                    <ProjectMemberListItem member={member} key={member.id} />
                  ))}
                </TableBody>
              </Table>
            </Paper>
          )}
        {members &&
          members.length === 0 &&
          invites &&
          invites.length === 0 &&
          !fetchInProgress &&
          !isFetchingInvites && <div>{t("no_project_members_found")}</div>}
      </WithFetch>
    </Page>
  );
}

export default withRouter(
  connect(state => ({
    fetchInProgress: state.project.membersFetchStatus === constants.WAITING,
    members: state.project.members,
    canManage: state.project.info && state.project.info.can_manage,
    projectInfo: state.project.info
  }))(ProjectMembersOverview)
);
