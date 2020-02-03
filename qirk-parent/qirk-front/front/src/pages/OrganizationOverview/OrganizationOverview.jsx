import React from "react";
import { connect } from "react-redux";
import { withRouter, Link } from "react-router-dom";
import { t } from "i18next";
import Button from "@material/react-button";
import paths from "./../../routes/paths";
import ProjectListItem from "../../components/ProjectListItem/ProjectListItem";
import constants from "../../utils/constants";
import Page from "../../components/Page/Page";
import WithFetch from "../../components/WithFetch/WithFetch";
import Loading from "../../components/Loading/Loading";
import "./OrganizationOverview.sass";

function OrganizationOverview({ info, fetchInProgress, favProjectsIds }) {
  const isProjFav = projectId => favProjectsIds.some(pId => pId === projectId);

  return (
    <Page>
      <WithFetch fetchList={[constants.FETCH_ORGANIZATION]}>
        {fetchInProgress === true && <Loading />}

        {info && fetchInProgress === false && (
          <div className="org-overview_wrapper">
            <div className="org-overview_left">
              <h1>{info.name}</h1>
              <div>
                {info.can_manage && (
                  <Link
                    to={paths.ProjectCreate.toPath({
                      organization_uiid: info.ui_id
                    })}
                  >
                    <Button dense raised>
                      {t("Create project")}
                    </Button>
                  </Link>
                )}
              </div>
              {info.projects &&
                info.projects.length > 0 &&
                info.projects
                  .sort((p1, p2) =>
                    !isProjFav(p1.id) && isProjFav(p2.id) ? 1 : -1
                  )
                  .map(p => (
                    <ProjectListItem key={p.id} p={p} orgUiId={info.ui_id} />
                  ))}
            </div>
            <div className="org-overview_right">
              {info.can_manage && (
                <div>
                  <Link
                    to={paths.OrganizationEdit.toPath({
                      organization_uiid: info.ui_id
                    })}
                  >
                    <Button dense raised>
                      {t("settings")}
                    </Button>
                  </Link>
                </div>
              )}
              {info.is_owner && (
                <div>
                  <Link
                    to={paths.JiraImport.toPath({
                      organization_uiid: info.ui_id
                    })}
                  >
                    <Button dense raised>
                      Import tasks
                    </Button>
                  </Link>
                </div>
              )}
              <div>
                <Link
                  to={paths.OrganizationMembers.toPath({
                    organization_uiid: info.ui_id
                  })}
                >
                  <Button dense raised>
                    {t("Members")}
                  </Button>
                </Link>
              </div>
            </div>
          </div>
        )}
      </WithFetch>
    </Page>
  );
}

export default withRouter(
  connect(state => ({
    info: state.organization.info,
    fetchInProgress: state.organization.fetchStatus === constants.WAITING,
    favProjectsIds: state.favorites.listOfFavorites.map(fav => fav.project.id)
  }))(OrganizationOverview)
);
