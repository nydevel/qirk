import React, { useState, useEffect } from "react";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import Loading from "../../components/Loading/Loading";
import {
  errorIsNotAuthenticated,
  responseIsStatusOk
} from "../../utils/variousUtils";
import axios from "../../utils/axios";
import endpoints from "../../utils/endpoints";
import { toast } from "react-toastify";
import { t } from "i18next";
import "./DefaultHomePage.sass";
import { withRouter, Link } from "react-router-dom";
import paths from "../../routes/paths";
import classNames from "classnames";
import queryString from "query-string";
import { IconButton, Icon } from "@material-ui/core";

function DefaultHomePage() {
  const { project_uiid } = queryString.parse(location.search);
  const [organizationUiid, setOrganizationUiid] = useState(null);
  const [projects, setProjects] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const fetchOrgsAndProjects = async () => {
    try {
      setIsLoading(true);
      const responseProjects = await axios.get(
        endpoints.GET_PROJECT_LIST_BY_USER
      );
      if (responseIsStatusOk(responseProjects) && responseProjects.data.data) {
        setProjects(responseProjects.data.data);
      }
    } catch (err) {
      if (errorIsNotAuthenticated(err)) {
        // ok, just ignore
      } else {
        toast.error(t("Errors.Error"));
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    projects &&
      projects.length &&
      projects.some(item =>
        item.ui_id == project_uiid
          ? item.organization &&
            item.organization.ui_id &&
            setOrganizationUiid(item.organization.ui_id)
          : null
      );
  }, [projects]);

  return (
    <PrivatePage onLoginConfirmed={fetchOrgsAndProjects}>
      {isLoading && <Loading />}
      {!isLoading && (
        <main className="user-homepage">
          <div
            style={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              width: "100%"
            }}
          >
            <Link
              to={`${paths.ProjectCreateWithoutPredefinedOrganization.toPath()}?${queryString.stringify(
                { redirect_to_defhomepage: true }
              )}`}
              style={{ width: "100%" }}
            >
              <section
                className={classNames({
                  "user-homepage__actions": true,
                  first_step_completed: projects && projects.length
                })}
                style={{
                  flexDirection: "row"
                }}
              >
                {projects && !projects.length && (
                  <div
                    className="user-homepage__actions__circle"
                    style={{ marginRight: 15 }}
                  >
                    <div className="user-homepage__actions__circle-number">
                      1
                    </div>
                  </div>
                )}
                {projects && projects.length > 0 && (
                  <IconButton disabled={true} style={{ color: "#06ab74" }}>
                    <Icon>done</Icon>
                  </IconButton>
                )}
                <div className="user-homepage__actions__step">
                  <span>{t("user_step_1")}</span>
                </div>
              </section>
            </Link>
            <span style={{ fontSize: 60, marginBottom: 10, color: "#666" }}>
              &#8595;
            </span>
            <Link
              isActive={projects && projects.length}
              to={paths.ProjectSingle.toPath({
                organization_uiid:
                  organizationUiid ||
                  (projects &&
                    projects.length &&
                    projects[projects.length - 1].organization.ui_id),
                project_uiid:
                  project_uiid ||
                  (projects &&
                    projects.length &&
                    projects[projects.length - 1].ui_id)
              })}
              style={{ width: "100%" }}
              className={classNames({
                "disabled-link": projects && !projects.length
              })}
            >
              <section
                style={{
                  flexDirection: "row-reverse"
                }}
                className={classNames({
                  "user-homepage__actions": true,
                  disabled__action: projects && !projects.length
                })}
              >
                <div
                  className="user-homepage__actions__circle"
                  style={{ marginLeft: 15 }}
                >
                  <div className="user-homepage__actions__circle-number">2</div>
                </div>
                <div className="user-homepage__actions__step">
                  <span>{t("user_step_2")}</span>
                </div>
              </section>
            </Link>
          </div>
        </main>
      )}
    </PrivatePage>
  );
}

export default withRouter(DefaultHomePage);
