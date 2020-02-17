import React from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import Crumb from "./Crumb/Crumb";
import paths from "./../../routes/paths";
import "./Breadcrumbs-new.sass";
import { uiDateTime } from "../../utils/timeUtils";
import { useTranslation } from "react-i18next";

function Breadcrumbs({
  match: { params, path },

  projInfo,
  taskNumber
}) {
  const{t}=useTranslation()
  const bc = [];
  if (
    path === paths.ProjectMemberEditing.url &&
    projInfo &&
    projInfo.ui_id &&
    projInfo.name
  ) {
    bc.length = 0;
    bc.push({
      url: paths.ProjectSingle.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      url: paths.ProjectMembers.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: t("Members")
    });
    bc.push({
      name: t("editing_project_member")
    });
  } else if (
    path === paths.ProjectPredefinedPermissionsInviteEdit.url &&
    projInfo &&
    projInfo.ui_id &&
    projInfo.name
  ) {
    bc.length = 0;

    bc.push({
      url: paths.ProjectSingle.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      url: paths.ProjectMembers.toPath({
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
  } else if (path === paths.ProjectSingle.url && projInfo && projInfo.name) {
    bc.push({
      name: projInfo.name
    });
  } else if (path === paths.ProjectCreate.url) {
    bc.length = 0;

    bc.push({
      url: paths.ProjectCreate.toPath({}),
      name: t("Project creation")
    });
  } else if (
    path === paths.TaskContent.url &&
    projInfo &&
    ((params && params.task_number) || taskNumber)
  ) {
    bc.push({
      url: paths.ProjectSingle.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      name: `${t("task")} #${(params && params.task_number) || taskNumber}`
    });
  } else if (path === paths.ProjectEdit.url && projInfo) {
    bc.push({
      url: paths.ProjectSingle.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      name: t("Project edit"),
      url: paths.ProjectEdit.toPath({
        project_uiid: projInfo.ui_id
      })
    });
  } else if (path === paths.Documentation.url && projInfo) {
    bc.push({
      url: paths.ProjectSingle.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      name: t("Documentation"),
      url: paths.Documentation.toPath({
        project_uiid: projInfo.ui_id
      })
    });
  } else if (path === paths.DocumentationEdit.url && projInfo) {
    bc.push({
      url: paths.ProjectSingle.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      name: t("Documentation"),
      url: paths.Documentation.toPath({
        project_uiid: projInfo.ui_id
      })
    });
    bc.push({
      name: t("settings")
    });
  } else if (path === paths.ProjectDiscussion.url && projInfo) {
    bc.push({
      url: paths.ProjectSingle.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      name: t("discussion_label"),
      url: paths.ProjectDiscussion.toPath({
        project_uiid: projInfo.ui_id
      })
    });
  } else if (path === paths.TaskCreate.url && projInfo) {
    bc.length = 0;

    bc.push({
      url: paths.ProjectSingle.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      name: t("Task create")
    });
  } else if (
    path === paths.ProjectMembers.url &&
    projInfo &&
    projInfo.ui_id &&
    projInfo.name
  ) {
    bc.length = 0;

    bc.push({
      url: paths.ProjectSingle.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      url: paths.ProjectMembers.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: t("Members")
    });
  } else if (
    path === paths.ProjectRoadmap.url &&
    projInfo &&
    projInfo.ui_id &&
    projInfo.name
  ) {
    bc.length = 0;

    bc.push({
      url: paths.ProjectSingle.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: projInfo.name
    });
    bc.push({
      url: paths.ProjectMembers.toPath({
        project_uiid: projInfo.ui_id
      }),
      name: t("roadmap")
    });
  } else if (path === paths.JiraImport.url) {
    bc.length = 0;

    bc.push({
      name: "Import from Jira"
    });
  } else if (path === paths.JiraImportMapping.url && params.timestamp) {
    bc.length = 0;

    bc.push({
      url: paths.JiraImport.toPath({}),
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
    projInfo: state.project.info,
    taskNumber:
      state.project.selectedTaskInitialState &&
      state.project.selectedTaskInitialState.number
  }))(Breadcrumbs)
);
