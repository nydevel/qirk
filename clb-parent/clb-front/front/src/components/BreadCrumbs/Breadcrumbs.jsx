import React from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { t } from "i18next";
import Crumb from "./Crumb/Crumb";
import paths from "./../../routes/paths";
import "./Breadcrumbs-new.sass";
import { uiDateTime } from "../../utils/timeUtils";

function Breadcrumbs({
  match: { params, path },
  orgInfo,
  projInfo,
  taskNumber
}) {
  const bc = [];
  if (
    path === paths.ProjectMemberEditing.url &&
    orgInfo &&
    orgInfo.ui_id &&
    orgInfo.name &&
    projInfo &&
    projInfo.ui_id &&
    projInfo.name
  ) {
    bc.length = 0;
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.ProjectSingle.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      url: paths.ProjectMembers.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: t("Members")
    });
    bc.push({
      name: t("editing_project_member")
    });
  } else if (
    path === paths.ProjectPredefinedPermissionsInviteEdit.url &&
    orgInfo &&
    orgInfo.ui_id &&
    orgInfo.name &&
    projInfo &&
    projInfo.ui_id &&
    projInfo.name
  ) {
    bc.length = 0;
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.ProjectSingle.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      url: paths.ProjectMembers.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: t("Members")
    });
    bc.push({
      name: t("editing_invite")
    });
  } else if (path === paths.MyProfile.url) {
    bc.push({
      url: paths.MyProfile.toPath(),
      name: t("Profile")
    });
  } else if (path === paths.OrganizationOverview.url && orgInfo) {
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
  } else if (path === paths.OrganizationCreate.url) {
    bc.push({
      url: paths.OrganizationCreate.toPath(),
      name: t("Organization creation")
    });
  } else if (path === paths.OrganizationEdit.url && orgInfo) {
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.OrganizationEdit.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: t("Organization edit")
    });
  } else if (path === paths.OrganizationMembers.url && orgInfo) {
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      name: t("Members"),
      url: paths.OrganizationMembers.toPath({
        organization_uiid: orgInfo.ui_id
      })
    });
  } else if (path === paths.OrganizationMemberEdit.url && orgInfo) {
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      name: t("Members"),
      url: paths.OrganizationMembers.toPath({
        organization_uiid: orgInfo.ui_id
      })
    });
    bc.push({
      name: t("Editing member")
    });
  } else if (path === paths.OrganizationMemberAdd.url && orgInfo) {
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      name: t("Members"),
      url: paths.OrganizationMembers.toPath({
        organization_uiid: orgInfo.ui_id
      })
    });
    bc.push({
      name: t("Adding a member")
    });
  } else if (
    path === paths.ProjectSingle.url &&
    orgInfo &&
    projInfo &&
    projInfo.name
  ) {
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      name: projInfo.name
    });
  } else if (path === paths.ProjectCreate.url && orgInfo) {
    bc.length = 0;
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.ProjectCreate.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: t("Project creation")
    });
  } else if (
    path === paths.TaskContent.url &&
    orgInfo &&
    projInfo &&
    ((params && params.task_number) || taskNumber)
  ) {
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.ProjectSingle.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      name: `${t("task")} #${(params && params.task_number) || taskNumber}`
    });
  } else if (path === paths.ProjectEdit.url && orgInfo && projInfo) {
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.ProjectSingle.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      name: t("Project edit"),
      url: paths.ProjectEdit.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      })
    });
  } else if (path === paths.Documentation.url && orgInfo && projInfo) {
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.ProjectSingle.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      name: t("Documentation"),
      url: paths.Documentation.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      })
    });
  } else if (path === paths.DocumentationEdit.url && orgInfo && projInfo) {
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.ProjectSingle.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      name: t("Documentation"),
      url: paths.Documentation.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      })
    });
    bc.push({
      name: t("settings")
    });
  } else if (path === paths.ProjectDiscussion.url && orgInfo && projInfo) {
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.ProjectSingle.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      name: t("discussion_label"),
      url: paths.ProjectDiscussion.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      })
    });
  } else if (path === paths.TaskCreate.url && orgInfo && projInfo) {
    bc.length = 0;
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.ProjectSingle.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      name: t("Task create")
    });
  } else if (
    path === paths.ProjectMembers.url &&
    orgInfo &&
    orgInfo.ui_id &&
    orgInfo.name &&
    projInfo &&
    projInfo.ui_id &&
    projInfo.name
  ) {
    bc.length = 0;
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.ProjectSingle.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      url: paths.ProjectMembers.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: t("Members")
    });
  } else if (
    path === paths.ProjectRoadmap.url &&
    orgInfo &&
    orgInfo.ui_id &&
    orgInfo.name &&
    projInfo &&
    projInfo.ui_id &&
    projInfo.name
  ) {
    bc.length = 0;
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.ProjectSingle.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      url: paths.ProjectMembers.toPath({
        organization_uiid: orgInfo.ui_id,
        project_uiid: projInfo.ui_id
      }),
      name: t("roadmap")
    });
  } else if (
    path === paths.JiraImport.url &&
    orgInfo &&
    orgInfo.ui_id &&
    orgInfo.name
  ) {
    bc.length = 0;
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      name: "Import from Jira"
    });
  } else if (
    path === paths.JiraImportMapping.url &&
    orgInfo &&
    orgInfo.ui_id &&
    orgInfo.name &&
    params.timestamp
  ) {
    bc.length = 0;
    bc.push({
      url: paths.OrganizationOverview.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: orgInfo.name
    });
    bc.push({
      url: paths.JiraImport.toPath({
        organization_uiid: orgInfo.ui_id
      }),
      name: "Import from Jira"
    });
    bc.push({
      name: uiDateTime(Number(params.timestamp))
    });
  }

  return (
    <>
      {bc && bc.length > 0 && (
        <div style={{ paddingBottom: 24 }} className="breadcrumbs">
          {bc.map((crumb, index) => (
            <Crumb
              key={index}
              url={crumb.url}
              name={crumb.name}
              hasNext={index < bc.length - 1}
            />
          ))}
        </div>
      )}
    </>
  );
}

export default withRouter(
  connect(state => ({
    orgInfo: state.organization.info,
    projInfo: state.project.info,
    foundUserInfo: state.userSearch.foundUserInfo,
    taskNumber:
      state.project.selectedTaskInitialState &&
      state.project.selectedTaskInitialState.number
  }))(Breadcrumbs)
);
