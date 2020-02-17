import React, { useState, useEffect } from "react";
import axios from "../../utils/axios";
import endpoints from "../../utils/endpoints";
import { Link } from "react-router-dom";
import { connect } from "react-redux";
import { toast } from "react-toastify";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import ProjectListItem from "../../components/ProjectListItem/ProjectListItem";
import paths from "../../routes/paths";
import Loading from "../../components/Loading/Loading";
import {
  responseIsStatusOk,
  errorIsNotAuthenticated
} from "../../utils/variousUtils";
import "./MyProjects.sass";
import Button1 from "../../components/Button/Button";
import { useTranslation } from "react-i18next";

function MyProjects({ favProjects, favIdOrder }) { const{t}=useTranslation()
  const getFavId = projId => {
    const fav = favProjects.find(fav => fav.project.id === projId);

    return fav ? fav.id : -1;
  };
  const getFavIndex = projId => {
    const index = favIdOrder.findIndex(favId => favId === getFavId(projId));

    return index === -1 ? Infinity : index;
  };

  const [projects, setProjects] = useState([]);

  const [isLoading, setIsLoading] = useState(true);

  const fetchMyProjects = async () => {
    try {
      setIsLoading(true);
      const response = await axios.get(endpoints.GET_PROJECT_LIST_BY_USER);
      if (responseIsStatusOk(response) && response.data.data) {
        setProjects(response.data.data);
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

  return (
    <PrivatePage
      className="organization-list"
      onLoginConfirmed={() => {
        fetchMyProjects();
      }}
    >
      <div>
        <h1>{t("my_projects_label")}</h1>

        <div style={{ paddingBottom: 10 }}>
          <Link to={paths.ProjectCreateWithoutPredefinedOrganization.toPath()}>
            <Button1>{t("Create project")}</Button1>
          </Link>
        </div>

        {isLoading && <Loading />}
        {!isLoading &&
          projects &&
          projects.length > 0 &&
          projects
            .sort((p1, p2) => getFavIndex(p1.id) - getFavIndex(p2.id))
            .map(p => (
              <ProjectListItem
                key={p.id}
                p={p}
                orgUiId={(p.organization && p.organization.ui_id) || "org"}
              />
            ))}

        {!isLoading && projects && projects.length === 0 && (
          <div>
            <div style={{ paddingBottom: 10 }}>
              {t("you_are_not_a_member_of_any_project")}
            </div>
          </div>
        )}
      </div>
    </PrivatePage>
  );
}

export default connect(state => ({
  favIdOrder: state.favorites.favoriteIdOrder,
  favProjects: state.favorites.listOfFavorites
}))(MyProjects);
