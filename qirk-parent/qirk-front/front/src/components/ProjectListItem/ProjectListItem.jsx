import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { Link } from "react-router-dom";
import classNames from "classnames";
import FavStar from "../FavStar/FavStar";
import paths from "./../../routes/paths";
import "./ProjectListItem.sass";

function ProjectListItem({ p, favProjectsIds, orgUiId }) {
  const [isFav, setIsFav] = useState(false);

  useEffect(() => {
    if (p && p.id && favProjectsIds && favProjectsIds.length > 0) {
      setIsFav(favProjectsIds.some(pId => pId === p.id));
    } else {
      setIsFav(false);
    }
  }, [favProjectsIds, p.id]);

  if (!p) return null;

  return (
    <Link
      className={classNames({
        "project-item": 1,
        "project-item__is-fav": isFav,
        "project-item__is-not-fav": !isFav
      })}
      to={paths.ProjectSingle.toPath({
        project_uiid: p.ui_id
      })}
    >
      <div className="project_content">{p.name}</div>
      <div className="project_add-to-fav">
        <FavStar projectId={p.id} />
      </div>
    </Link>
  );
}

const mapStateToProps = state => ({
  favProjectsIds: state.favorites.listOfFavorites.map(fav => fav.project.id)
});

export default connect(mapStateToProps)(ProjectListItem);
