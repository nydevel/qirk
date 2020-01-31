import React from "react";
import { Link } from "react-router-dom";
import { Draggable } from "react-beautiful-dnd";
import { t } from "i18next";
import paths from "./../../../routes/paths";
import { ListItem, ListItemText } from "@material/react-list";
import Button from "../../Button/Button";
import "./Favorite.sass";

function Favorite({ favorite, index }) {
  return (
    <Draggable draggableId={favorite.id} index={index}>
      {provided => (
        <div
          className="favorite-item"
          key={favorite.id}
          ref={provided.innerRef}
          {...provided.dragHandleProps}
          {...provided.draggableProps}
        >
          <ListItem>
            <Link
              className="favorite-item__project-name"
              to={paths.ProjectSingle.toPath({
                organization_uiid: favorite.project.organization.ui_id,
                project_uiid: favorite.project.ui_id
              })}
            >
              <ListItemText primaryText={favorite.project.name} />
            </Link>
            {favorite.can_create_task && (
              <Link
                to={paths.TaskCreate.toPath({
                  organization_uiid: favorite.project.organization.ui_id || "a",
                  project_uiid: favorite.project.ui_id || "a"
                })}
              >
                <Button secondary className="favorite-item__add-task-button">
                  {t("add_task")}
                </Button>
              </Link>
            )}
          </ListItem>
        </div>
      )}
    </Draggable>
  );
}

export default Favorite;
