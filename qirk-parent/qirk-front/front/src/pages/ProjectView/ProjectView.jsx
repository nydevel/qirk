import React, { useState } from "react";
import { withRouter, Link } from "react-router-dom";
import { connect } from "react-redux";
import { t } from "i18next";
import Button from "../../components/Button/Button";
import TaskSearch from "../../components/TaskSearch/TaskSearch";
import Page from "../../components/Page/Page";
import WithFetch from "../../components/WithFetch/WithFetch";
import Loading from "../../components/Loading/Loading";
import constants from "../../utils/constants";
import paths from "../../routes/paths";
import IconButton from "@material-ui/core/IconButton";
import Icon from "@material-ui/core/Icon";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import "./ProjectView.sass";

const mapStateToProps = state => ({
  isSignedIn: state.auth.isSignedIn,
  projectInfo: state.project.info,
  lastTasks: state.project.lastTasks,
  isLoading: state.project.fetchStatus === constants.WAITING,
  canUpdateProject: state.project.info && state.project.info.can_manage,
  canCreateTask: state.project.info && state.project.info.write_allowed
});

function ProjectView({
  projectInfo,
  isSignedIn,
  canUpdateProject,
  canCreateTask,
  isLoading,
  match: { params }
}) {
  const [anchorEl, setAnchorEl] = useState(null);

  function handleClick(event) {
    setAnchorEl(event.currentTarget);
  }

  function handleClose() {
    setAnchorEl(null);
  }
  return (
    <Page>
      <WithFetch includeApplication fetchList={[constants.FETCH_PROJECT]}>
        {isLoading && (
          <Loading
            size={50}
            style={{
              display: "flex",
              width: "100%",
              height: "calc(100vh - 220px)",
              justifyContent: "center",
              alignItems: "center"
            }}
          />
        )}
        {!isLoading && projectInfo && (
          <div /* WithFetch loaded and projectInfo present */>
            <div
              /* buttons-and-menu */ style={{
                display: "flex",
                paddingBottom: 32
              }}
            >
              <div /* buttons */ style={{ display: "flex", flex: 1 }}>
                {canCreateTask && (
                  <Link
                    style={{ display: "flex", marginRight: 8 }}
                    to={paths.TaskCreate.toPath({
                      organization_uiid: params.organization_uiid || "a",
                      project_uiid: params.project_uiid || "a"
                    })}
                  >
                    <Button
                      secondary
                      style={{
                        margin: "auto",
                        height: "40px"
                      }}
                    >
                      {t("add_task")}
                    </Button>
                  </Link>
                )}
                {/* {(projectInfo.documentation_html || canUpdateProject) && (
                  <Link
                    style={{ display: "flex", marginRight: 8 }}
                    to={paths.Documentation.toPath({
                      organization_uiid: params && params.organization_uiid,
                      project_uiid: params && params.project_uiid
                    })}
                  >
                    <Button
                      style={{
                        margin: "auto",
                        height: "40px"
                      }}
                      dense
                      raised
                    >
                      {t("Documentation")}
                    </Button>
                  </Link>
                )} */}
                <Link
                  style={{ display: "flex", marginRight: 8 }}
                  to={paths.ProjectDiscussion.toPath({
                    organization_uiid: params && params.organization_uiid,
                    project_uiid: params && params.project_uiid
                  })}
                >
                  <Button
                    style={{
                      margin: "auto",
                      height: "40px"
                    }}
                    dense
                    raised
                  >
                    {t("discussion_label")}
                  </Button>
                </Link>
                {canUpdateProject && (
                  <Link
                    style={{ display: "flex", marginRight: 8 }}
                    to={paths.ProjectRoadmap.toPath({
                      organization_uiid: params && params.organization_uiid,
                      project_uiid: params && params.project_uiid
                    })}
                  >
                    <Button
                      style={{
                        margin: "auto",
                        height: "40px"
                      }}
                      dense
                      raised
                    >
                      {t("roadmap")}
                    </Button>
                  </Link>
                )}
              </div>
              <div /* menu */>
                <>
                  <IconButton
                    aria-label="more"
                    aria-controls="long-menu"
                    aria-haspopup="true"
                    onClick={handleClick}
                  >
                    <Icon>settings</Icon>
                  </IconButton>
                  <Menu
                    id={`project_actions`}
                    anchorEl={anchorEl}
                    anchorOrigin={{
                      vertical: "bottom",
                      horizontal: "right"
                    }}
                    transformOrigin={{
                      vertical: "top",
                      horizontal: "right"
                    }}
                    open={Boolean(anchorEl)}
                    onClose={handleClose}
                  >
                    {isSignedIn && canUpdateProject && (
                      <Link
                        onClick={e => {
                          handleClose(e);
                        }}
                        to={paths.ProjectEdit.toPath({
                          organization_uiid: params && params.organization_uiid,
                          project_uiid: params && params.project_uiid
                        })}
                      >
                        <MenuItem>{t("settings")}</MenuItem>
                      </Link>
                    )}
                    <Link
                      onClick={e => {
                        handleClose(e);
                      }}
                      to={paths.ProjectMembers.toPath({
                        organization_uiid: params && params.organization_uiid,
                        project_uiid: params && params.project_uiid
                      })}
                    >
                      <MenuItem>{t("Members")}</MenuItem>
                    </Link>
                  </Menu>
                </>
              </div>
            </div>
            <TaskSearch />
          </div>
        )}
      </WithFetch>
    </Page>
  );
}

export default withRouter(connect(mapStateToProps)(ProjectView));
