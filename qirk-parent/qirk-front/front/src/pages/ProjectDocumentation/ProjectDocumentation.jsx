import React from "react";
import { connect } from "react-redux";
import { t } from "i18next";
import { Link } from "react-router-dom";
import paths from "../../routes/paths";
import Page from "../../components/Page/Page";
import WithFetch from "../../components/WithFetch/WithFetch";
import constants from "../../utils/constants";
import Loading from "../../components/Loading/Loading";
import Button1 from "../../components/Button/Button";

function ProjectDocumentation(props) {
  return (
    <Page>
      <WithFetch fetchList={[constants.FETCH_PROJECT]}>
        {props.loadingInProgress && <Loading />}
        {props.loadingInProgress === false &&
          props.projectInfo &&
          props.orgInfo && (
            <div>
              <h1>
                {props.projectInfo.name}: {t("Documentation")}
              </h1>
              {props.canUpdateProject && (
                <Link
                  to={paths.DocumentationEdit.toPath({
                    organization_uiid: props.orgInfo.ui_id,
                    project_uiid: props.projectInfo.ui_id
                  })}
                >
                  <Button1>{t("settings")}</Button1>
                </Link>
              )}
              {props.projectInfo.documentation_html && (
                <div
                  dangerouslySetInnerHTML={{
                    __html: props.projectInfo.documentation_html
                  }}
                />
              )}
              {!props.projectInfo.documentation_html && (
                <div style={{ marginTop: 25 }}>{t("no_documentation")}</div>
              )}
            </div>
          )}
      </WithFetch>
    </Page>
  );
}

export default connect(state => ({
  projectInfo: state.project.info,
  orgInfo: state.organization.info,
  canUpdateProject: state.project.info && state.project.info.can_manage,
  loadingInProgress: state.project.fetchStatus === constants.WAITING
}))(ProjectDocumentation);
