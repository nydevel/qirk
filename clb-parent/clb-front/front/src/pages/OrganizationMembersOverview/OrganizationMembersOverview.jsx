import React, { useState } from "react";
import { t } from "i18next";
import { connect } from "react-redux";
import { withRouter, Link } from "react-router-dom";
import { toast } from "react-toastify";
import IconButton from "@material-ui/core/IconButton";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import MoreVertIcon from "@material-ui/icons/MoreVert";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import {
  removeOrganizationMember,
  leaveOrganization
} from "../../actions/organizationActions";
import Button from "../../components/Button/Button";
import actions from "../../utils/constants";
import paths from "../../routes/paths";
import "./OrganizationMembersOverview.sass";
import Page from "../../components/Page/Page";
import { uiUserFromOrgMember } from "../../utils/variousUtils";
import constants from "../../utils/constants";
import Loading from "../../components/Loading/Loading";
import WithFetch from "../../components/WithFetch/WithFetch";
import { Chip } from "@material-ui/core";

const mapStateToProps = state => ({
  canManage: state.organization.info && state.organization.info.can_manage,
  userId: state.user.id,
  ui_id: state.organization.info && state.organization.info.ui_id,
  organizationMembers: state.organization.members,
  requestStatus: state.organization.requestStatus,
  loadingOrgMembersInProgress:
    state.organization.membersFetchStatus === constants.WAITING,
  leavingInProgress: state.organization.leaveRequestStatus === constants.WAITING
});

function OrganizationMembers({
  canManage,
  userId,
  ui_id,
  organizationMembers,
  requestStatus,
  leaveOrganization,
  removeOrganizationMember,
  loadingOrgMembersInProgress,
  leavingInProgress,
  match: {
    params: { organization_uiid }
  }
}) {
  const removeOrRequestInfo = id => {
    if (requestStatus === actions.WAITING) {
      toast.info(t("this_request_is_already_in_progress"));
      return;
    }
    removeOrganizationMember(id);
  };

  const [anchorEl, setAnchorEl] = useState(null);
  const [menuForItemId, setMenuForItemId] = useState(null);

  function handleClick(event, id) {
    setAnchorEl(event.currentTarget);
    setMenuForItemId(id);
  }

  function handleClose() {
    setAnchorEl(null);
    setMenuForItemId(null);
  }

  return (
    <Page className="organization-members">
      <WithFetch
        fetchList={[
          constants.FETCH_ORGANIZATION,
          constants.FETCH_ORGANIZATION_MEMBERS
        ]}
      />
      <h1>{t("Organization members")}</h1>
      {canManage && ui_id && (
        <div className="organization-members__create-button">
          <Link
            to={paths.OrganizationMemberAdd.toPath({
              organization_uiid: ui_id
            })}
          >
            <Button>{t("Add member")}</Button>
          </Link>
        </div>
      )}
      {loadingOrgMembersInProgress === true && <Loading />}
      {/* ситуация, когда в организации нет участников, невозможна \_(0 u o)_/ */}
      {loadingOrgMembersInProgress === false &&
        organizationMembers &&
        organizationMembers.length > 0 && (
          <Paper>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>{t("User")}</TableCell>
                  <TableCell align="center">{t("Enabled")}</TableCell>
                  <TableCell align="center">{t("Manager")}</TableCell>
                  <TableCell align="right">{t("actions")}</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {organizationMembers &&
                  organizationMembers.map(item => (
                    <TableRow key={item.id}>
                      <TableCell component="th" scope="row">
                        {uiUserFromOrgMember(item)}{" "}
                        {item.owner && <Chip label={t("owner")} />}
                      </TableCell>
                      <TableCell align="center">
                        {item.enabled ? (
                          <span role="img" aria-label={t("yes")}>
                            ✔️
                          </span>
                        ) : (
                          <span role="img" aria-label={t("no")}>
                            ❌️
                          </span>
                        )}
                      </TableCell>
                      <TableCell align="center">
                        {item.manager ? (
                          <span role="img" aria-label={t("yes")}>
                            ✔️
                          </span>
                        ) : (
                          <span role="img" aria-label={t("no")}>
                            ❌️
                          </span>
                        )}
                      </TableCell>
                      <TableCell align="right">
                        {(canManage || item.user.id === userId) &&
                        !item.owner ? (
                          <>
                            <IconButton
                              aria-label="more"
                              aria-controls="long-menu"
                              aria-haspopup="true"
                              onClick={e => handleClick(e, item.id)}
                            >
                              <MoreVertIcon />
                            </IconButton>
                            <Menu
                              id={`actions_${item.id}`}
                              anchorEl={anchorEl}
                              keepMounted
                              open={
                                Boolean(anchorEl) && menuForItemId === item.id
                              }
                              onClose={handleClose}
                            >
                              {ui_id && canManage && (
                                <Link
                                  to={paths.OrganizationMemberEdit.toPath({
                                    organization_uiid: ui_id,
                                    member_id: item.id
                                  })}
                                  onClick={e => {
                                    handleClose(e);
                                  }}
                                >
                                  <MenuItem>{t("edit")}</MenuItem>
                                </Link>
                              )}
                              {item.id &&
                                organization_uiid &&
                                (canManage || item.user.id === userId) && (
                                  <MenuItem
                                    onClick={e => {
                                      handleClose(e);
                                      if (item.user.id === userId) {
                                        if (leavingInProgress === false) {
                                          leaveOrganization(organization_uiid);
                                        } else {
                                          toast.error(
                                            t(
                                              "this_request_is_already_in_progress"
                                            )
                                          );
                                        }
                                      } else if (canManage) {
                                        removeOrRequestInfo(item.id);
                                      }
                                    }}
                                  >
                                    {item.user.id === userId
                                      ? t("leave")
                                      : t("remove")}
                                  </MenuItem>
                                )}
                            </Menu>
                          </>
                        ) : (
                          <>{` `}</>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
              </TableBody>
            </Table>
          </Paper>
        )}
    </Page>
  );
}

export default withRouter(
  connect(
    mapStateToProps,
    { removeOrganizationMember, leaveOrganization }
  )(OrganizationMembers)
);
