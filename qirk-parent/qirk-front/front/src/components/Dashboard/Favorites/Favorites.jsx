import React from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { t } from "i18next";
import {
  removeFavorite,
  setFavoriteIdOrder
} from "../../../actions/commonActions";
import Favorite from "./Favorite";
import { DragDropContext, Droppable } from "react-beautiful-dnd";
import {
  ListDivider,
  ListGroup,
  ListGroupSubheader
} from "@material/react-list";
import classNames from "classnames";

function Favorites({
  favoritesList,
  favoriteIdOrder,
  setFavoriteIdOrder,
  page = false,
  divider = true,
  heading = true
}) {
  const getFavById = favId => favoritesList.find(fav => fav.id === favId);

  const onDragEnd = result => {
    const { destination, source, draggableId } = result;
    if (!destination) {
      return;
    }
    if (
      destination.droppableId === source.droppableId &&
      destination.index === source.index
    ) {
      return;
    }
    const newOrder = Array.from(favoriteIdOrder);

    newOrder.splice(source.index, 1);
    newOrder.splice(destination.index, 0, draggableId);

    let previous = null;

    if (destination.index) {
      previous = newOrder[destination.index - 1];
    }

    const putData = {
      id: draggableId,
      previous: previous
    };

    setFavoriteIdOrder(newOrder, putData);
  };

  if (favoritesList.length === 0) {
    return null;
  }

  return (
    <DragDropContext onDragEnd={onDragEnd}>
      <Droppable droppableId="1">
        {provided => (
          <div
            ref={provided.innerRef}
            {...provided.droppableProps}
            className={classNames({
              "favorites-page": page,
              "dashboard__favorites-items": true
            })}
          >
            <ListGroup>
              {divider && <ListDivider />}
              {heading && (
                <ListGroupSubheader tag="h2">
                  {t("Favorites")}
                </ListGroupSubheader>
              )}
              {favoriteIdOrder.map((favId, index) => {
                let fav = getFavById(favId);
                return (
                  <Favorite
                    index={index}
                    key={fav.id}
                    favorite={fav}
                    projectId={fav.project.id}
                  />
                );
              })}
              {provided.placeholder}
            </ListGroup>
          </div>
        )}
      </Droppable>
    </DragDropContext>
  );
}

const mapStateToProps = state => {
  return {
    favoritesList: state.favorites.listOfFavorites,
    favoriteIdOrder: state.favorites.favoriteIdOrder,
    requestStatus: state.common.requestStatus,
    isSignedIn: state.auth.isSignedIn,
    orgWithProjects: state.common.dashboardProjects
  };
};

export default withRouter(
  connect(
    mapStateToProps,
    {
      removeFavorite,
      setFavoriteIdOrder
    }
  )(Favorites)
);
