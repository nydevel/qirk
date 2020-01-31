import React, { useEffect, useState } from "react";
import classNames from "classnames";
import { connect } from "react-redux";
import ic_star_active from "./../../assets/icons/ic_star_active.svg";
import ic_star_unactive from "./../../assets/icons/ic_star_unactive.svg";
import "./FavStar.sass";
import {
  removeFavorite,
  addProjectToFavorites
} from "../../actions/commonActions";

function FavStar({
  projectId,
  removeFavorite,
  addProjectToFavorites,
  favProjectsIds,
  favorites,
  isSignedIn
}) {
  const [isFav, setIsFav] = useState(false);
  const [favId, setFavId] = useState(null);

  useEffect(() => {
    if (projectId && favProjectsIds && favProjectsIds.length > 0) {
      setIsFav(favProjectsIds.some(pId => pId === projectId));
    } else {
      setIsFav(false);
    }
  }, [favProjectsIds, projectId]);

  useEffect(() => {
    if (isFav && favorites && favorites.length > 0) {
      const thisFav = favorites.find(fav => fav.project.id === projectId);
      if (thisFav && thisFav.id) {
        setFavId(thisFav.id);
      } else {
        setFavId(null);
      }
    } else {
      setFavId(null);
    }
  }, [favorites, projectId, isFav]);

  const onFavClicked = e => {
    e.preventDefault();

    if (isFav) {
      removeFavorite(favId);
    } else {
      addProjectToFavorites(projectId);
    }
  };

  if (!isSignedIn) {
    return null;
  }

  return (
    <div
      className={classNames({
        "fav-star__wrapper": true,
        "fav-star__is-fav": isFav,
        "fav-star__is-not-fav": !isFav
      })}
      onClick={onFavClicked}
    >
      <img className="fav-start__img_unactive" alt="" src={ic_star_unactive} />
      <img className="fav-start__img_active" alt="" src={ic_star_active} />
    </div>
  );
}

const mapStateToProps = state => ({
  favProjectsIds: state.favorites.listOfFavorites.map(fav => fav.project.id),
  favorites: state.favorites.listOfFavorites,
  isSignedIn: state.auth.isSignedIn
});

export default connect(
  mapStateToProps,
  { removeFavorite, addProjectToFavorites }
)(FavStar);
