import React, { useState } from "react";
import { renderToStaticMarkup } from "react-dom/server";
import { Link } from "react-router-dom";
import { t } from "i18next";
import MoreVertIcon from "@material-ui/icons/MoreVert";
import ReactTooltip from "react-tooltip";
import {
  TableRow,
  TableCell,
  IconButton,
  Menu,
  MenuItem
} from "@material-ui/core";
import { uiUserFromUser } from "../../utils/variousUtils";
import paths from "../../routes/paths";
import { uiDateTime } from "../../utils/timeUtils";

function ProjectMemberInviteListItem({
  invite: inv,
  cancelInvite,
  projectUiId: project_uiid
}) {
  const [anchorEl, setAnchorEl] = useState(null);

  const handleClose = () => {
    setAnchorEl(null);
  };

  if (!inv) {
    return null;
  }

  return (
    <TableRow key={inv.id}>
      <TableCell component="th" scope="row">
        {inv.email ? inv.email : uiUserFromUser(inv.user)}
      </TableCell>
      <TableCell>
        <ReactTooltip
          id={`tooltip_for_invite_id_${inv.id}`}
          place="bottom"
          type="dark"
          effect="solid"
        />
        <span
          data-tip={renderToStaticMarkup(
            <>
              {`${t("invited_by")} ${uiUserFromUser(inv.sender)} 
              ${t("at")} ${uiDateTime(inv.created_at)} `}
            </>
          )}
          data-for={`tooltip_for_invite_id_${inv.id}`}
          style={{ cursor: "pointer", borderBottom: "1px dashed #525252" }}
        >
          {t("waiting")}
        </span>
      </TableCell>
      <TableCell align="center">
        {inv.write_allowed && (
          <span role="img" aria-label={t("yes")}>
            ✔️
          </span>
        )}
        {!inv.write_allowed && (
          <span role="img" aria-label={t("no")}>
            ❌️
          </span>
        )}
      </TableCell>
      <TableCell align="center">
        {inv.manager && (
          <span role="img" aria-label={t("yes")}>
            ✔️
          </span>
        )}
        {!inv.manager && (
          <span role="img" aria-label={t("no")}>
            ❌️
          </span>
        )}
      </TableCell>
      <TableCell align="right">
        <IconButton
          aria-label="more"
          aria-controls="long-menu"
          aria-haspopup="true"
          onClick={e => {
            setAnchorEl(e.currentTarget);
          }}
        >
          <MoreVertIcon />
        </IconButton>
        <Menu
          id={`invite_actions_${inv.id}`}
          anchorEl={anchorEl}
          keepMounted
          open={Boolean(anchorEl)}
          onClose={handleClose}
        >
          <Link
            to={paths.ProjectPredefinedPermissionsInviteEdit.toPath({
              invite_id: inv.id,
              project_uiid: project_uiid || "a"
            })}
            onClick={e => {
              handleClose(e);
            }}
          >
            <MenuItem>{t("edit")}</MenuItem>
          </Link>

          <MenuItem
            onClick={e => {
              handleClose(e);
              cancelInvite(inv.id);
            }}
          >
            {t("cancel")}
          </MenuItem>
        </Menu>
      </TableCell>
    </TableRow>
  );
}

export default ProjectMemberInviteListItem;
