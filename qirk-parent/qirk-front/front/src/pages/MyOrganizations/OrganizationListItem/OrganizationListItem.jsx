import React from "react";
import { Link } from "react-router-dom";
import paths from "../../../routes/paths";
import "./OrganizationListItem.sass";

function OrganizationListItem({ item }) {
  return (
    <Link
      className="project-item"
      to={paths.OrganizationOverview.toPath({
        organization_uiid: item.ui_id
      })}
    >
      <div>{item.name}</div>
    </Link>
  );
}

export default OrganizationListItem;
