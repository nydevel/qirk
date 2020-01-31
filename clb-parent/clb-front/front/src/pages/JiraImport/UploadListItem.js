import React from "react";
import { Link } from "react-router-dom";
import paths from "../../routes/paths";
import { uiDateTime } from "../../utils/timeUtils";
import { TableRow, TableCell } from "@material-ui/core";

function UploadListItem({ item, organizationUiId }) {
  if (!item) {
    return null;
  }

  const { archive_filename, imported_projects, timestamp } = item;

  return (
    <TableRow>
      <TableCell component="th" scope="row">
        <Link
          to={paths.JiraImportMapping.toPath({
            organization_uiid: organizationUiId,
            timestamp: timestamp.epoch_milli
          })}
        >
          {uiDateTime(timestamp.epoch_milli)}
        </Link>
      </TableCell>
      <TableCell>{archive_filename}</TableCell>
      <TableCell>
        {imported_projects &&
          imported_projects.length > 0 &&
          imported_projects.some(p => !!p.jira_name) &&
          `${imported_projects.map(
            (project, index) => `${index > 0 ? " " : ""}${project.jira_name}`
          )}`}
      </TableCell>
    </TableRow>
  );
}

export default UploadListItem;
