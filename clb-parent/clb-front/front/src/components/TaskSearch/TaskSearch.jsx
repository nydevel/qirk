import React, { useState, useEffect } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { t } from "i18next";
import { toast } from "react-toastify";
import queryString from "query-string";
import {
  responseIsStatusOk,
  uiUserFromUser,
  provideTaskSortByOptions,
  provideTaskSortOrderOptions,
  saveItemToLocalStorageSafe
} from "../../utils/variousUtils";
import {
  resetTaskSearchDispatch,
  setLastOrganizationUiIdDispatch,
  setLastProjectUiIdDispatch,
  setTaskSearchCountOfAllTasksDispatch,
  setTaskSearchFilterSearchAfterDispatch,
  setTaskSearchResultDispatch,
  addTaskSearchResultDispatch,
  setTaskSearchLoadingMissingTasksDispatch
} from "../../actions/taskSearchActions";
import { addCacheUsersDispatch } from "../../actions/cacheUsersActions";
import Button from "../Button/Button";
import endpoints from "../../utils/endpoints";
import axios from "../../utils/axios";
import paths from "../../routes/paths";
import Loading from "../Loading/Loading";
import { Paper } from "@material-ui/core";
import "./TaskSearch.sass";
import TaskSearchSelectFilter from "../TaskSearchSelectFilter/TaskSearchSelectFilter";
import constants from "../../utils/constants";
import TaskSearchTextFilter from "../TaskSearchTextFilter/TaskSearchTextFilter";
import TaskSearchFetchSelectFilter from "../TaskSearchFetchSelectFilter/TaskSearchFetchSelectFilter";
import TagRadioButton from "../TagRadioButton/TagRadioButton";
import Icon from "@material-ui/core/Icon";
import IconButton from "@material-ui/core/IconButton";
import Menu from "@material-ui/core/Menu";
import AllProjectTasks from "../AllProjectTasks/AllProjectTasks";
import useKeyPress from "../../utils/hooks/useKeyPress";
import WithTooltip from "../WithTooltip/WithTooltip";

const connectActions = {
  setLastOrganizationUiIdDispatch,
  setLastProjectUiIdDispatch,
  resetTaskSearchDispatch,
  addCacheUsersDispatch,
  setTaskSearchCountOfAllTasksDispatch,
  setTaskSearchFilterSearchAfterDispatch,
  setTaskSearchResultDispatch,
  addTaskSearchResultDispatch,
  setTaskSearchLoadingMissingTasksDispatch
};

const mapStateToProps = state => ({
  userId: state.user.id,
  cacheUsers: state.cacheUsers.usersList,
  lastOrganizationUiId: state.taskSearch.lastOrganizationUiId,
  lastProjectUiId: state.taskSearch.lastProjectUiId,
  organizationUiid: state.organization.info && state.organization.info.ui_id,
  organizationId: state.organization.info && state.organization.info.id,
  projectUiid: state.project.uiid,
  numberOfFoundTasks: state.taskSearch.countOfAllTasks,
  searchAfter: state.taskSearch.searchAfter,
  tasks: state.taskSearch.result,
  loadingMissingTasks: state.taskSearch.loadingMissingTasks
});

const SEARCH_DELAY_MS = 350;

const UNASSIGNED_ORG_MEMBER_ID = -2;

let requestsDates = {}; //в редьюсере и в стейте компонента заходит со старым значением, поэтому вынесено сюда
let lastResultUpdate = 0; // аналогично первому

function TaskSearch({
  userId,
  cacheUsers,
  lastOrganizationUiId,
  lastProjectUiId,
  setLastOrganizationUiIdDispatch,
  setLastProjectUiIdDispatch,
  organizationUiid,
  organizationId,
  projectUiid,
  resetTaskSearchDispatch,
  addCacheUsersDispatch,
  setTaskSearchLoadingMissingTasksDispatch,
  setTaskSearchCountOfAllTasksDispatch,
  setTaskSearchFilterSearchAfterDispatch,
  setTaskSearchResultDispatch,
  addTaskSearchResultDispatch,
  numberOfFoundTasks,
  searchAfter,
  tasks,
  loadingMissingTasks,
  location,
  history,
  match: {
    params: { organization_uiid }
  },
  match: { params }
}) {
  const parsed = queryString.parse(location.search);

  const [text, setText] = useState(parsed.text || "");
  const [assignee, setAssignee] = useState(
    (parsed.assignee_label &&
      parsed.assignee_value && {
        label: parsed.assignee_label,
        value: parsed.assignee_value
      }) ||
      ""
  );
  const [reporter, setReporter] = useState(
    (parsed.reporter_label &&
      parsed.reporter_value && {
        label: parsed.reporter_label,
        value: parsed.reporter_value
      }) ||
      ""
  );
  const [hashtag, setHashtag] = useState(
    (parsed.hashtag_label &&
      parsed.hashtag_value && {
        label: parsed.hashtag_label,
        value: parsed.hashtag_value
      }) ||
      ""
  );
  const [type, setType] = useState(parsed.type || "");
  const [priority, setPriority] = useState(parsed.priority || "");
  const [status, setStatus] = useState(
    (parsed.status && parsed.status.split(",")) || [
      constants.TASK_STATUSES.OPEN,
      constants.TASK_STATUSES.IN_DEVELOPMENT,
      constants.TASK_STATUSES.WAITING_FOR_QA,
      constants.TASK_STATUSES.IN_QA_REVIEW
    ]
  );
  const [searchOrder, setSearchOrder] = useState(
    parsed.searchOrder || constants.DESC
  );
  const [searchCol, setSearchCol] = useState(
    parsed.searchCol || constants.TASK_SORT_BY.UPDATED_AT
  );

  const [usersList, setUsersList] = useState([]);
  const [searchTimeout, setSearchTimeout] = useState(null);
  const [requestInProgress, setRequestInProgress] = useState(false);

  const sortByOptions = provideTaskSortByOptions();
  const sortOrderOptions = provideTaskSortOrderOptions();
  const [sortBy, setSortBy] = useState(
    searchCol === constants.TASK_SORT_BY.UPDATED_AT
      ? sortByOptions[0]
      : sortByOptions[1]
  ); //label+value for select
  const [orderBy, setOrderBy] = useState(
    searchOrder === constants.DESC ? sortOrderOptions[0] : sortOrderOptions[1]
  ); //label+value for select
  const shiftPressed = useKeyPress("Shift");
  const ctrlPressed = useKeyPress("Control");
  const [
    lastClickedElementForStatuses,
    setLastClickedElementForStatuses
  ] = useState(null);
  const tasksStatusesInRightOrder = [
    constants.TASK_STATUSES.OPEN,
    constants.TASK_STATUSES.IN_DEVELOPMENT,
    constants.TASK_STATUSES.WAITING_FOR_QA,
    constants.TASK_STATUSES.IN_QA_REVIEW,
    constants.TASK_STATUSES.REJECTED,
    constants.TASK_STATUSES.CLOSED
  ]; //change with TagRadioButton Status in right order

  const [anchorEl, setAnchorEl] = useState(null);
  function handleClick(event) {
    setAnchorEl(event.currentTarget);
  }
  function handleClose() {
    setAnchorEl(null);
  }

  useEffect(() => {
    requestsDates = {};
  }, []);

  const loadOrgMemberOptions = async (
    searchString,
    appendUnassignedOption = false
  ) => {
    try {
      const response = await axios.get(
        endpoints.GET_ORGANIZATION_MEMBER_SEARCH,
        {
          params: {
            prefix: searchString,
            organization_ui_id: organization_uiid,
            me_first: true,
            show_first: userId
          }
        }
      );

      if (
        responseIsStatusOk(response) &&
        response &&
        response.data &&
        response.data.data
      ) {
        const foundMembers = response.data.data;

        const memberOptions = foundMembers.map(m => ({
          value: m.id,
          label: uiUserFromUser(m.user)
        }));

        if (
          appendUnassignedOption &&
          "UNASSIGNED".startsWith(searchString.toUpperCase())
        ) {
          memberOptions.push({
            value: UNASSIGNED_ORG_MEMBER_ID,
            label: t("unassigned")
          });
        }

        return memberOptions;
      }
    } catch (e) {
      console.error(e);
      return [];
    }
  };

  const loadHashtagOptions = async (searchString, includeUsed = false) => {
    try {
      const response = await axios.get(endpoints.GET_TASK_HASHTAG_SEARCH, {
        params: {
          prefix: searchString,
          project_ui_id: params.project_uiid,
          include_used: includeUsed
        }
      });
      if (
        responseIsStatusOk(response) &&
        response &&
        response.data &&
        response.data.data
      ) {
        const foundHashtags = response.data.data;

        const hashtagsOptions = foundHashtags.map(h => ({
          value: h.id,
          label: h.name
        }));

        return hashtagsOptions;
      }
    } catch (e) {
      console.error(e);
      return [];
    }
  };

  //cacheUsers
  const fetchUsersList = async (organizationId, usersList) => {
    if (organizationId && usersList.length > 0) {
      try {
        if (!searchAfter) {
          setRequestInProgress(true);
        }
        const response = await axios.get(
          endpoints.GET_ORGANIZATION_MEMBER_LIST_BY_IDS,
          {
            params: {
              organization_id: organizationId,
              ids: usersList.join(",")
            }
          }
        );

        if (
          responseIsStatusOk(response) &&
          response.data.data &&
          response.data.data.length > 0
        ) {
          const fetchedUsersList = response.data.data;
          const cacheUsersList = fetchedUsersList.reduce((acc, cur) => {
            acc[Number(cur.id)] = cur;
            return acc;
          }, {});
          addCacheUsersDispatch(cacheUsersList);
        }
      } catch (e) {
        console.error(e);
        toast.error(t("Errors.Error"));
      } finally {
        setRequestInProgress(false);
      }
    }
  };

  const searchTask = async (_searchAfter = searchAfter, id = 0) => {
    try {
      //prevent spinner and refresh tasks-list
      if (!_searchAfter) {
        setRequestInProgress(true);
      }
      const response = await axios.get(endpoints.GET_TASK_SEARCH, {
        params: {
          project_ui_id: params.project_uiid,
          reported_by_me: false,
          reporter_id: reporter && reporter.value,
          assigned_to_me: false,
          assignee_id: assignee && assignee.value,
          type,
          text,
          priority,
          status: status.join(","),
          hashtag: hashtag && hashtag.label,
          sort_by: searchCol,
          ordering: searchOrder,
          search_after: _searchAfter
        }
      });

      if (responseIsStatusOk(response)) {
        if (id === 0 || requestsDates[`${id}`] > lastResultUpdate) {
          if (id !== 0) {
            lastResultUpdate = requestsDates[`${id}`];
          }
          _searchAfter
            ? addTaskSearchResultDispatch(response.data.data)
            : setTaskSearchResultDispatch(response.data.data);
          setTaskSearchCountOfAllTasksDispatch(response.data.meta.total);
          setUsersList([
            ...new Set(
              response.data.data
                .map(e => e.assignee && e.assignee.id)
                .concat(
                  response.data.data.map(e => e.reporter && e.reporter.id)
                )
                .filter(Boolean)
                .filter(u => !(u in cacheUsers))
            )
          ]);
        }
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setRequestInProgress(false);
      setTaskSearchFilterSearchAfterDispatch(null);
      setTaskSearchLoadingMissingTasksDispatch(false);
    }
  };

  const handleClickTasksStatuses = currentStatus => {
    if (shiftPressed) {
      if (lastClickedElementForStatuses) {
        if (
          tasksStatusesInRightOrder.indexOf(currentStatus) >
          tasksStatusesInRightOrder.indexOf(lastClickedElementForStatuses)
        ) {
          setStatus(
            tasksStatusesInRightOrder.slice(
              tasksStatusesInRightOrder.indexOf(lastClickedElementForStatuses),
              tasksStatusesInRightOrder.indexOf(currentStatus) + 1
            )
          );
          setLastClickedElementForStatuses(currentStatus);
        } else {
          setStatus(
            tasksStatusesInRightOrder.slice(
              tasksStatusesInRightOrder.indexOf(currentStatus),
              tasksStatusesInRightOrder.indexOf(lastClickedElementForStatuses) +
                1
            )
          );
          setLastClickedElementForStatuses(currentStatus);
        }
      } else {
        setStatus(
          tasksStatusesInRightOrder.slice(
            0,
            tasksStatusesInRightOrder.indexOf(currentStatus) + 1
          )
        );
        setLastClickedElementForStatuses(currentStatus);
      }
    } else if (ctrlPressed) {
      setLastClickedElementForStatuses(currentStatus);
      if (!status.includes(currentStatus)) {
        setStatus([...status, currentStatus]);
      } else setStatus(status.filter(item => item !== currentStatus));
    } else {
      setLastClickedElementForStatuses(currentStatus);
      setStatus([currentStatus]);
    }
  };

  useEffect(() => {
    if (
      params.organization_uiid &&
      params.project_uiid &&
      (lastOrganizationUiId !== params.organization_uiid ||
        lastProjectUiId !== params.project_uiid)
    ) {
      resetTaskSearchDispatch();
      setLastOrganizationUiIdDispatch(params.organization_uiid);
      setLastProjectUiIdDispatch(params.project_uiid);
    }
  }, [
    params.organization_uiid,
    params.project_uiid,
    lastOrganizationUiId,
    lastProjectUiId
  ]);

  useEffect(() => {
    //ignore spinner if searchAfter != null
    if (organizationId) {
      fetchUsersList(organizationId, usersList);
    }
  }, [usersList]);

  useEffect(() => {
    const e = document.querySelector(".drawer-app-content");
    if (e) {
      e.scrollTop = 0;
    }
    setTaskSearchLoadingMissingTasksDispatch(false);
    setRequestInProgress(true);
    setTaskSearchFilterSearchAfterDispatch(null);
    setTaskSearchResultDispatch([]);
    setTaskSearchCountOfAllTasksDispatch(0);

    const filterParams = {
      text,
      assignee_value: (assignee && assignee.value) || "",
      assignee_label: (assignee && assignee.label) || "",
      reporter_value: (reporter && reporter.value) || "",
      reporter_label: (reporter && reporter.label) || "",
      hashtag_value: (hashtag && hashtag.value) || "",
      hashtag_label: (hashtag && hashtag.label) || "",
      type,
      priority,
      status,
      searchOrder,
      searchCol
    };

    history.push(
      `${paths.ProjectSingle.toPath({
        organization_uiid: params && params.organization_uiid,
        project_uiid: params && params.project_uiid
      })}?${queryString.stringify(filterParams, { arrayFormat: "comma" })}`
    );

    const id = `id${(~~(Math.random() * 1e8)).toString(16)}`;
    requestsDates[`${id}`] = Date.now();

    if (searchTimeout) {
      clearTimeout(searchTimeout);
    }

    setSearchTimeout(
      setTimeout(async () => {
        await searchTask(null, id);
        setSearchTimeout(null);
      }, SEARCH_DELAY_MS)
    );
    saveItemToLocalStorageSafe("filter_params", filterParams);
  }, [
    params.project_uiid,
    reporter,
    assignee,
    type,
    text,
    priority,
    status,
    hashtag,
    searchCol,
    searchOrder
  ]);

  //отдельный юзэффект нужен для того, чтобы при setSearchAfter(null) не было перезагрузки тасков
  useEffect(() => {
    if (searchAfter && !loadingMissingTasks) {
      setTaskSearchLoadingMissingTasksDispatch(true);
      if (searchTimeout) {
        clearTimeout(searchTimeout);
      }

      setSearchTimeout(
        setTimeout(async () => {
          await searchTask();
          setSearchTimeout(null);
        }, SEARCH_DELAY_MS)
      );
    }
  }, [searchAfter]);

  const renderFilters = (isMobileFilters = false) => {
    return (
      <div
        /* filters */ className={
          isMobileFilters
            ? "filter-for-mobile-menu__content"
            : "project-filters-for-PC"
        }
        style={isMobileFilters ? {} : { paddingLeft: 32 }}
      >
        <Paper
          style={
            isMobileFilters
              ? { padding: "13px 8px" }
              : { padding: 8, position: "sticky", top: 32 }
          }
        >
          <div
            className={
              isMobileFilters ? "filter-for-mobile-menu__content__title" : ""
            }
            style={{ display: "flex" }}
          >
            <div
              style={
                isMobileFilters
                  ? { display: "flex" }
                  : { display: "flex", flex: 1 }
              }
            >
              <span style={{ margin: "auto 6px" }}>{t("filters")}</span>
            </div>
            <Button
              flat={true}
              value={assignee}
              onClick={() => {
                setAssignee("");
                setReporter("");
                setText("");
                setHashtag("");
                setStatus([
                  constants.TASK_STATUSES.OPEN,
                  constants.TASK_STATUSES.IN_DEVELOPMENT,
                  constants.TASK_STATUSES.WAITING_FOR_QA,
                  constants.TASK_STATUSES.IN_QA_REVIEW
                ]);
                setType("");
                setPriority("");
              }}
              style={isMobileFilters ? { marginLeft: 10 } : {}}
            >
              {t("reset")}
            </Button>
            {isMobileFilters && (
              <Button flat={true} onClick={handleClose}>
                <Icon>close</Icon>
              </Button>
            )}
          </div>
          <div>
            <TaskSearchTextFilter
              id={
                isMobileFilters
                  ? "task-summary-search-filter-for-mobile"
                  : "task-summary-search-filter"
              }
              fullWidth
              className={
                isMobileFilters ? "filter-for-mobile-menu__content__item" : ""
              }
              value={text}
              label={t("text_search")}
              onChange={e => setText(e.target.value)}
            />
          </div>
          <div style={{ paddingTop: 22 }}>
            <TaskSearchFetchSelectFilter
              placeholder={t("to")}
              fullWidth
              className={
                isMobileFilters ? "filter-for-mobile-menu__content__item" : ""
              }
              onChange={setAssignee}
              value={assignee || ""}
              loadOptions={str => loadOrgMemberOptions(str, true)}
            />
          </div>
          <div style={{ paddingTop: 22 }}>
            <TaskSearchFetchSelectFilter
              placeholder={t("by")}
              className={
                isMobileFilters ? "filter-for-mobile-menu__content__item" : ""
              }
              fullWidth
              onChange={setReporter}
              value={reporter || ""}
              loadOptions={str => loadOrgMemberOptions(str, false)}
            />
          </div>
          <div style={{ paddingTop: 22 }}>
            <TaskSearchFetchSelectFilter
              id={
                isMobileFilters
                  ? "task-hashtag-search-filter-for-mobile"
                  : "task-hashtag-search-filter"
              }
              className={
                isMobileFilters ? "filter-for-mobile-menu__content__item" : ""
              }
              fullWidth
              value={hashtag || ""}
              placeholder={t("hashtag")}
              onChange={setHashtag}
              loadOptions={str => loadHashtagOptions(str, false)}
            />
          </div>
          <div style={{ paddingTop: 10 }}>
            <div style={{ paddingBottom: 10 }}>{`${t("TaskTypes.self")}:`}</div>
            <div style={{ textTransform: "lowercase" }}>
              <TagRadioButton
                selected={type === ""}
                onClick={() => setType("")}
                style={{
                  color: "grey"
                }}
              >
                {t("Any")}
              </TagRadioButton>
              <TagRadioButton
                selected={type === constants.TASK_TYPES.BUG}
                onClick={() => setType(constants.TASK_TYPES.BUG)}
                style={{
                  color: constants.TASKS_COLORS.TASK_TYPES_COLORS.BUG
                }}
              >
                {t("TaskTypes.BUG")}
              </TagRadioButton>
              <TagRadioButton
                selected={type === constants.TASK_TYPES.TASK || type === null}
                onClick={() => setType(constants.TASK_TYPES.TASK)}
                style={{
                  color: constants.TASKS_COLORS.TASK_TYPES_COLORS.TASK
                }}
              >
                {t("TaskTypes.TASK")}
              </TagRadioButton>
              <TagRadioButton
                selected={type === constants.TASK_TYPES.NEW_FEATURE}
                onClick={() => setType(constants.TASK_TYPES.NEW_FEATURE)}
                style={{
                  color: constants.TASKS_COLORS.TASK_TYPES_COLORS.NEW_FEATURE
                }}
              >
                {t("TaskTypes.NEW_FEATURE")}
              </TagRadioButton>
            </div>
          </div>
          <div style={{ paddingTop: 10 }}>
            <div
              style={{
                paddingBottom: 10
              }}
            >
              {`${t("TaskPriorities.self")}:`}
            </div>
            <div style={{ textTransform: "lowercase" }}>
              <TagRadioButton
                selected={priority === ""}
                onClick={() => setPriority("")}
                style={{
                  color: "grey"
                }}
              >
                {t("Any")}
              </TagRadioButton>
              <TagRadioButton
                selected={priority === constants.TASK_PRIORITIES.CRITICAL}
                onClick={() => setPriority(constants.TASK_PRIORITIES.CRITICAL)}
                style={{
                  color: constants.TASKS_COLORS.TASK_PRIORITIES_COLORS.CRITICAL
                }}
              >
                {t("TaskPriorities.CRITICAL")}
              </TagRadioButton>
              <TagRadioButton
                selected={
                  priority === constants.TASK_PRIORITIES.MAJOR ||
                  priority === null
                }
                onClick={() => setPriority(constants.TASK_PRIORITIES.MAJOR)}
                style={{
                  color: constants.TASKS_COLORS.TASK_PRIORITIES_COLORS.MAJOR
                }}
              >
                {t("TaskPriorities.MAJOR")}
              </TagRadioButton>
              <TagRadioButton
                selected={priority === constants.TASK_PRIORITIES.MINOR}
                onClick={() => setPriority(constants.TASK_PRIORITIES.MINOR)}
                style={{
                  color: constants.TASKS_COLORS.TASK_PRIORITIES_COLORS.MINOR
                }}
              >
                {t("TaskPriorities.MINOR")}
              </TagRadioButton>
            </div>
          </div>
          <div
            onMouseDown={e => e.preventDefault()}
            style={{ paddingTop: 10, maxWidth: 235 }}
          >
            <div style={{ display: "flex", alignItems: "baseline" }}>
              <WithTooltip
                dataTip={t("status_help")}
                id="status-help"
                style={{
                  marginBottom: 10
                }}
              >{`${t("TaskStatuses.self")}:`}</WithTooltip>
            </div>
            <div
              style={{
                textTransform: "lowercase",
                display: "flex",
                flexWrap: "wrap",
                justifyContent: "space-between"
              }}
            >
              <TagRadioButton
                selected={status.includes(constants.TASK_STATUSES.OPEN)}
                onClick={() => {
                  handleClickTasksStatuses(constants.TASK_STATUSES.OPEN);
                }}
                style={{
                  color: constants.TASKS_COLORS.TASK_STATUSES_COLORS.OPEN,
                  marginBottom: 1
                }}
              >
                {t("TaskStatuses.OPEN")}
              </TagRadioButton>
              <TagRadioButton
                selected={status.includes(
                  constants.TASK_STATUSES.IN_DEVELOPMENT
                )}
                onClick={() => {
                  handleClickTasksStatuses(
                    constants.TASK_STATUSES.IN_DEVELOPMENT
                  );
                }}
                style={{
                  color:
                    constants.TASKS_COLORS.TASK_STATUSES_COLORS.IN_DEVELOPMENT,
                  marginBottom: 1
                }}
              >
                {t("TaskStatuses.IN_DEVELOPMENT")}
              </TagRadioButton>
              <TagRadioButton
                selected={status.includes(
                  constants.TASK_STATUSES.WAITING_FOR_QA
                )}
                onClick={() => {
                  handleClickTasksStatuses(
                    constants.TASK_STATUSES.WAITING_FOR_QA
                  );
                }}
                style={{
                  color:
                    constants.TASKS_COLORS.TASK_STATUSES_COLORS.WAITING_FOR_QA,
                  marginBottom: 1
                }}
              >
                {t("TaskStatuses.WAITING_FOR_QA")}
              </TagRadioButton>
              <TagRadioButton
                selected={status.includes(constants.TASK_STATUSES.IN_QA_REVIEW)}
                onClick={() => {
                  handleClickTasksStatuses(
                    constants.TASK_STATUSES.IN_QA_REVIEW
                  );
                }}
                style={{
                  color:
                    constants.TASKS_COLORS.TASK_STATUSES_COLORS.IN_QA_REVIEW,
                  marginBottom: 1
                }}
              >
                {t("TaskStatuses.IN_QA_REVIEW")}
              </TagRadioButton>
              <TagRadioButton
                selected={status.includes(constants.TASK_STATUSES.REJECTED)}
                onClick={() => {
                  handleClickTasksStatuses(constants.TASK_STATUSES.REJECTED);
                }}
                style={{
                  color: constants.TASKS_COLORS.TASK_STATUSES_COLORS.REJECTED,
                  marginBottom: 1
                }}
              >
                {t("TaskStatuses.REJECTED")}
              </TagRadioButton>
              <TagRadioButton
                selected={status.includes(constants.TASK_STATUSES.CLOSED)}
                onClick={() => {
                  handleClickTasksStatuses(constants.TASK_STATUSES.CLOSED);
                }}
                style={{
                  color: constants.TASKS_COLORS.TASK_STATUSES_COLORS.CLOSED,
                  marginBottom: 1
                }}
              >
                {t("TaskStatuses.CLOSED")}
              </TagRadioButton>
            </div>
          </div>
        </Paper>
      </div>
    );
  };

  return (
    <div className="project-task-search">
      <div className="project-task-search__column-wrapper">
        <div /* table & filters */ style={{ flex: 1 }}>
          <div /* table wrapper */ className="project-task-search__tasks">
            <div
              style={{
                display: "flex",
                alignItems: "center",
                paddingBottom: 15
              }}
            >
              <div style={{ flex: "1" }}>
                {tasks &&
                  tasks.length > 0 &&
                  numberOfFoundTasks !== 0 &&
                  !requestInProgress &&
                  (cacheUsers && Object.keys(cacheUsers).length !== 0 && (
                    <div className="count-of-founded-tasks">
                      <span>{`${t("Found")} ${numberOfFoundTasks} ${t(
                        "tasks"
                      )}`}</span>
                    </div>
                  ))}
                {!requestInProgress &&
                  (!tasks || (tasks && tasks.length === 0)) && (
                    <div>
                      <span>{t("no_data_placeholder")}</span>
                    </div>
                  )}
              </div>
              <div className="project-task-search__sorting">
                <div className="project-task-search__sorting__refresh">
                  <IconButton
                    style={{ marginRight: 15 }}
                    onClick={async () => {
                      await searchTask();
                    }}
                  >
                    <Icon>refresh</Icon>
                  </IconButton>
                </div>
                <div className="project-task-search__sorting__by">
                  <TaskSearchSelectFilter
                    value={sortBy && sortBy.value}
                    label={t("TaskSortBy.self")}
                    options={sortByOptions}
                    onChange={e => {
                      setSortBy({
                        value: e.target.value,
                        label: t(`TaskSortBy.${e.target.value}`)
                      });
                      setSearchCol(e.target.value);
                    }}
                  />
                </div>
                <div className="project-task-search__sorting__order">
                  <TaskSearchSelectFilter
                    value={orderBy && orderBy.value}
                    label={t("TaskSortOrder.self")}
                    options={sortOrderOptions}
                    onChange={e => {
                      setOrderBy({
                        value: e.target.value,
                        label: t(`TaskSortOrder.${e.target.value}`)
                      });
                      setSearchOrder(e.target.value);
                    }}
                  />
                </div>
              </div>
              <div
                /* filters Mobile */ className="project-filters-for-mobile-icon-button"
              >
                <IconButton
                  aria-label="more"
                  aria-controls="project-filters-for-mobile"
                  aria-haspopup="true"
                  onClick={handleClick}
                  style={{ marginLeft: 15 }}
                >
                  <Icon>filter_list</Icon>
                </IconButton>
              </div>
            </div>
            <Menu
              id="project-filters-for-mobile"
              anchorEl={anchorEl}
              getContentAnchorEl={null}
              anchorOrigin={{
                vertical: "top",
                horizontal: "right"
              }}
              transformOrigin={{
                vertical: "top",
                horizontal: "right"
              }}
              open={Boolean(anchorEl)}
              onClose={handleClose}
              MenuListProps={{ disablePadding: true }}
            >
              {renderFilters(true)}
            </Menu>
            {(requestInProgress || !cacheUsers) && (
              <Loading
                disableShrink
                style={{
                  display: "flex",
                  width: "100%",
                  height: "calc(100vh - 400px)",
                  justifyContent: "center",
                  alignItems: "center"
                }}
              />
            )}
            {!requestInProgress && cacheUsers && (
              <AllProjectTasks
                style={{ width: "100%", height: "100%" }}
                isLoading={requestInProgress}
                data={tasks.map(item => ({
                  link: paths.TaskContent.toPath({
                    organization_uiid: organizationUiid,
                    project_uiid: projectUiid,
                    task_number: item.number
                  }),
                  itemData: {
                    id: item.number,
                    summary: item.summary,
                    reporter: (item.reporter && item.reporter.id) || null,
                    assignee: (item.assignee && item.assignee.id) || null,
                    dateOfCreate: item.created_at,
                    dateOfUpdate: item.updated_at,
                    priority: item.task_priority.name_code,
                    type: item.task_type.name_code,
                    status: item.task_status.name_code
                  }
                }))}
              />
            )}
            {loadingMissingTasks && !requestInProgress && (
              <Loading
                style={{
                  display: "flex",
                  justifyContent: "center",
                  paddingTop: 31
                }}
              />
            )}
          </div>
        </div>
        {renderFilters()}
      </div>
    </div>
  );
}

export default withRouter(
  connect(
    mapStateToProps,
    connectActions
  )(TaskSearch)
);
