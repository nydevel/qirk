import React from "react";
import { Link } from "react-router-dom";
import paths from "../../routes/paths";
import {
  uiUserFromNotification,
  uiUserFromUser
} from "../../utils/variousUtils";
import "./NotificationItem.sass";
import { uiDateTime } from "../../utils/timeUtils";
import { setNotificationsNeedToSendCheckAllRequestToSocketDispatch } from "./../../actions/notificationActions";
import { connect } from "react-redux";
import { useTranslation } from "react-i18next";

const NotificationItem = ({
  notification,
  handlerClick,
  setNotificationsNeedToSendCheckAllRequestToSocketDispatch
}) => {const{t}=useTranslation()
  const {
    iso8601,
    notification_type,
    task_number,
    old_type,
    new_type,
    old_status,
    new_status,
    old_priority,
    new_priority,
    old_assignee,
    new_assignee,
    new_assignee_username,
    new_assignee_full_name,
    organization_ui_id,
    project_ui_id,
    task_summary,
    message
  } = notification;

  return (
    <div
      onClick={() => {
        setNotificationsNeedToSendCheckAllRequestToSocketDispatch(true);
        handlerClick();
      }}
      style={{
        color: "rgba(0, 0, 0, 0.87)",
        padding: 8,
        borderBottom: "1px solid rgba(0, 0, 0, 0.2)"
      }}
      className="notification-item"
    >
      <Link
        style={{ color: "rgba(0, 0, 0, 0.87)" }}
        to={paths.TaskContent.toPath({
          task_number,
          organization_uiid: organization_ui_id,
          project_uiid: project_ui_id
        })}
      >
        <div>
          <span style={{ fontWeight: "bold" }}>Task #{task_number} </span>
          <span>
            {task_summary} was {t(`NotificationTypes.${notification_type}`)} by{" "}
          </span>
          <span>{uiUserFromNotification(notification)}</span>
          <div
            style={{ paddingTop: notification_type === "TASK_CREATED" ? 0 : 8 }}
          >
            {notification_type === "TASK_UPDATED" && old_type !== new_type && (
              <div>
                <span style={{ textDecoration: "line-through" }}>
                  {t("TaskTypes." + old_type)}
                </span>{" "}
                <span> -> {t("TaskTypes." + new_type)}</span>
              </div>
            )}
            {notification_type === "TASK_UPDATED" && old_status !== new_status && (
              <div>
                <span style={{ textDecoration: "line-through" }}>
                  {t("TaskStatuses." + old_status)}
                </span>{" "}
                <span>-> {t("TaskStatuses." + new_status)}</span>
              </div>
            )}
            {notification_type === "TASK_UPDATED" &&
              old_priority !== new_priority && (
                <div>
                  <span style={{ textDecoration: "line-through" }}>
                    {t("TaskPriorities." + old_priority)}
                  </span>{" "}
                  <span>-> {t("TaskPriorities." + new_priority)}</span>
                </div>
              )}
            {notification_type === "TASK_UPDATED" &&
              old_assignee !== new_assignee &&
              new_assignee !== null && (
                <div>
                  <span>
                    New assignee:{" "}
                    {uiUserFromUser({
                      full_name: new_assignee_full_name,
                      username: new_assignee_username
                    })}
                  </span>
                </div>
              )}
            {notification_type === "TASK_UPDATED" &&
              old_assignee !== new_assignee &&
              new_assignee === null && <div>Assignee not set</div>}
            {notification_type === "TASK_COMMENTED" && <div>{message}</div>}
          </div>
        </div>
        <div style={{ fontSize: 12, paddingTop: 8 }}>{uiDateTime(iso8601)}</div>
      </Link>
    </div>
  );
};

export default connect(
  null,
  {
    setNotificationsNeedToSendCheckAllRequestToSocketDispatch
  }
)(NotificationItem);
