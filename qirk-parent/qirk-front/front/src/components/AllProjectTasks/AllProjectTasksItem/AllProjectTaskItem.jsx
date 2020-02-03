import React from "react";
import "./AllProjectTaskItem.sass";
import { t } from "i18next";
import constants from "../../../utils/constants";
import { uiUserFromOrgMember } from "../../../utils/variousUtils";
import { uiDate } from "../../../utils/timeUtils";
import { connect } from "react-redux";
import classNames from "classnames";

const mapStateToProps = state => ({
  cacheUsers: state.cacheUsers.usersList
});

function AllProjectTaskItem({ itemData, cacheUsers, ...props }) {
  if (
    !itemData ||
    Object.keys(itemData).length === 0 ||
    (itemData.assignee && !(itemData.assignee in cacheUsers)) ||
    (itemData.reporter && !(itemData.reporter in cacheUsers))
  ) {
    return null;
  }
  const tasksPriority =
    itemData.priority === constants.TASK_PRIORITIES.CRITICAL
      ? constants.TASK_PRIORITIES.CRITICAL
      : itemData.priority === constants.TASK_PRIORITIES.MAJOR
      ? constants.TASK_PRIORITIES.MAJOR
      : constants.TASK_PRIORITIES.MINOR;
  const taskUpdated = itemData.dateOfCreate !== itemData.dateOfUpdate;
  const formatDateString = string => uiDate(string);

  const renderLeftPart = () => {
    const props = {};

    if (tasksPriority === constants.TASK_PRIORITIES.CRITICAL) {
      props.className = "left-part-of-item bcRed";
    } else if (tasksPriority === constants.TASK_PRIORITIES.MAJOR) {
      props.className = "left-part-of-item bcGreen";
    } else if (tasksPriority === constants.TASK_PRIORITIES.MINOR) {
      props.className = "left-part-of-item bcBlue";
    }

    return <aside {...props}></aside>;
  };

  const renderRightPart = () => {
    const props = {};

    if (itemData.status === constants.TASK_STATUSES.OPEN) {
      props.className = "right-part-of-item stat-open";
    } else if (itemData.status === constants.TASK_STATUSES.IN_DEVELOPMENT) {
      props.className = "right-part-of-item stat-indev";
    } else if (itemData.status === constants.TASK_STATUSES.WAITING_FOR_QA) {
      props.className = "right-part-of-item stat-waitqa";
    } else if (itemData.status === constants.TASK_STATUSES.IN_QA_REVIEW) {
      props.className = "right-part-of-item stat-inqarev";
    } else if (
      itemData.status === constants.TASK_STATUSES.REJECTED ||
      itemData.status === constants.TASK_STATUSES.CLOSED
    ) {
      props.className = "right-part-of-item stat-closed";
    }

    return <aside {...props}>{t(`TaskStatuses.${itemData.status}`)}</aside>;
  };

  return (
    <div className="all-project-tasks-content__item" {...props}>
      {renderLeftPart()}
      <div className="all-project-tasks-content__item__task-id">
        # {itemData.id}
      </div>
      <div className="main-item-part">
        <div className="main-item-part__header">
          <div className="main-item-part__header__to-by">
            {itemData.reporter && (
              <div className="main-item-part__header__reporter">
                <span>{t("author")}: </span>
                {uiUserFromOrgMember(cacheUsers[itemData.reporter])}
              </div>
            )}
            {itemData.assignee && (
              <div className="main-item-part__header__assignee">
                <span>{t("to")}: </span>
                {uiUserFromOrgMember(cacheUsers[itemData.assignee])}
              </div>
            )}
          </div>
          {taskUpdated && (
            <div className="main-item-part__header__dateofupdate">
              <span>{t("taskUpdated")}: </span>
              <span>{formatDateString(itemData.dateOfUpdate)}</span>
            </div>
          )}
        </div>
        <div className="main-item-part__summary">
          <span>{itemData.summary}</span>
        </div>
        <div className="main-item-part__footer">
          <div className="main-item-part__footer__priority-and-type">
            <span
              className={classNames({
                "main-item-part__footer__priority-and-type__priority": true,
                "critical-priority":
                  tasksPriority === constants.TASK_PRIORITIES.CRITICAL,
                "major-priority":
                  tasksPriority === constants.TASK_PRIORITIES.MAJOR,
                "minor-priority":
                  tasksPriority === constants.TASK_PRIORITIES.MINOR
              })}
            >
              {t(`TaskPriorities.${itemData.priority}`)}{" "}
            </span>
            <span className="main-item-part__footer__priority-and-type__type">
              {t(`TaskTypes.${itemData.type}`)}
            </span>
          </div>
          <div className="main-item-part__footer__dateofcreate">
            <span>{t("taskCreated")}: </span>
            <span>{formatDateString(itemData.dateOfCreate)}</span>
          </div>
        </div>
      </div>
      {renderRightPart()}
    </div>
  );
}

export default connect(mapStateToProps)(AllProjectTaskItem);
