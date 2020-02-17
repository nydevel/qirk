import React, { useState } from "react";
import { connect } from "react-redux";
import { Link, withRouter } from "react-router-dom";
import IconButton from "@material-ui/core/IconButton";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import MoreVertIcon from "@material-ui/icons/MoreVert";
import TableCell from "@material-ui/core/TableCell";
import TableRow from "@material-ui/core/TableRow";
import axios from "../../utils/axios";
import paths from "../../routes/paths";
import { uiUserFromUser, responseIsStatusOk } from "../../utils/variousUtils";
import endpoints from "../../utils/endpoints";
import { filterOutProjectMemberDispatch } from "../../actions/projectActions";
import { toast } from "react-toastify";
import { useTranslation } from "react-i18next";

function ProjectMemberListItem({
  member,
  filterOutProjectMemberDispatch,
  canManage,
  userId,
  projectId,
  match: { params }
}) {const{t}=useTranslation()
  const [deleteReqStarted, setDeleteReqStarted] = useState(false);
  const [leaveReqStarted, setLeaveReqStarted] = useState(false);

  const removeProjMember = async () => {
    if (deleteReqStarted === true) {
      toast.info(t("this_request_is_already_in_progress"));
      return;
    }

    try {
      setDeleteReqStarted(true);

      const response = await axios.delete(endpoints.DELETE_PROJECT_MEMBER, {
        data: { id: member.id }
      });

      if (responseIsStatusOk(response)) {
        filterOutProjectMemberDispatch(member);
        toast.success(t("Success"));
      } else {
        toast.error(t("Errors.Error"));
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setDeleteReqStarted(false);
    }
  };

  const leaveProject = async () => {
    if (!projectId) {
      return toast.error(t("Errors.Error"));
    }

    if (true === leaveReqStarted) {
      return toast.error(t("this_request_is_already_in_progress"));
    }

    try {
      setLeaveReqStarted(true);

      const response = await axios.delete(
        endpoints.DELETE_PROJECT_MEMBER_LEAVE,
        {
          data: {
            project: projectId
          }
        }
      );
      if (responseIsStatusOk(response)) {
        filterOutProjectMemberDispatch(member);
        toast.success(t("Success"));
      } else {
        toast.error(t("Errors.Error"));
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setLeaveReqStarted(false);
    }
  };

  const [anchorEl, setAnchorEl] = useState(null);

  function handleClick(event) {
    setAnchorEl(event.currentTarget);
  }

  function handleClose() {
    setAnchorEl(null);
  }

  return (
    <TableRow>
      <TableCell component="th" scope="row">
        {uiUserFromUser(member.user)}
      </TableCell>
      <TableCell>{t("member")}</TableCell>
      <TableCell align="center">
        {member.write_allowed && (
          <span role="img" aria-label={t("yes")}>
            ✔️
          </span>
        )}
        {!member.write_allowed && (
          <span role="img" aria-label={t("no")}>
            ❌️
          </span>
        )}
      </TableCell>
      <TableCell align="center">
        {member.manager && (
          <span role="img" aria-label={t("yes")}>
            ✔️
          </span>
        )}
        {!member.manager && (
          <span role="img" aria-label={t("no")}>
            ❌️
          </span>
        )}
      </TableCell>
      <TableCell align="right">
        {canManage || member.user.id === userId ? (
          <>
            <IconButton
              aria-label="more"
              aria-controls="long-menu"
              aria-haspopup="true"
              onClick={handleClick}
            >
              <MoreVertIcon />
            </IconButton>
            <Menu
              id={`actions_${member.id}`}
              anchorEl={anchorEl}
              keepMounted
              open={Boolean(anchorEl)}
              onClose={handleClose}
            >
              {canManage && (
                <Link
                  to={paths.ProjectMemberEditing.toPath({
                    project_member_id: member.id,
                    project_uiid: params.project_uiid
                  })}
                  onClick={e => {
                    handleClose(e);
                  }}
                >
                  <MenuItem>{t("edit")}</MenuItem>
                </Link>
              )}
              {(canManage || member.user.id === userId) && (
                <MenuItem
                  onClick={e => {
                    handleClose(e);
                    if (member.user.id === userId) {
                      leaveProject();
                    } else if (canManage) {
                      removeProjMember();
                    }
                  }}
                >
                  {member.user.id === userId ? t("leave") : t("remove")}
                </MenuItem>
              )}
            </Menu>
          </>
        ) : (
          <>{` `}</>
        )}
      </TableCell>
    </TableRow>
  );
}

export default withRouter(
  connect(
    state => ({
      canManage: state.project.info && state.project.info.can_manage,
      userId: state.user.id,
      projectId: state.project.info && state.project.info.id
    }),
    { filterOutProjectMemberDispatch }
  )(ProjectMemberListItem)
);
