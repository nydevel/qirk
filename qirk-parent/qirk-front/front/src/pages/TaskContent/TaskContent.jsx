import React, { useState, useEffect } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import { toast } from "react-toastify";
import queryString from "query-string";
import classNames from "classnames";
import ReactTextareaAutocomplete from "@webscopeio/react-textarea-autocomplete";
import "@webscopeio/react-textarea-autocomplete/style.css";
import axios from "../../utils/axios";
import {
  provideTaskStatusesOptions,
  responseIsStatusOk,
  cutFirstString,
  parseHashtags,
  uiUserFromUser,
  takeItemFromLocalStorageSafe
} from "../../utils/variousUtils";
import endpoints from "../../utils/endpoints";
import Button from "../../components/Button/Button";
import {
  editProjectTask,
  setProjectSelectedTaskInitialStateDispatch
} from "../../actions/projectActions";
import actions from "../../utils/constants";
import paths from "../../routes/paths";
import Chat from "../../components/Chat/Chat";
import constants from "../../utils/constants";
import Attachments from "../../components/Attachments/Attachments";
import "./TaskContent.sass";
import Page from "../../components/Page/Page";
import WithFetch from "../../components/WithFetch/WithFetch";
import Loading from "../../components/Loading/Loading";
import { Chip, IconButton, Icon } from "@material-ui/core";
import TagRadioButton from "../../components/TagRadioButton/TagRadioButton";
import GoBackButton from "../../components/GoBackButton/GoBackButton";
import { uiDateTime } from "../../utils/timeUtils";
import TaskSearchFetchSelectFilter from "../../components/TaskSearchFetchSelectFilter/TaskSearchFetchSelectFilter";
import TaskSearchSelectFilter from "../../components/TaskSearchSelectFilter/TaskSearchSelectFilter";
import TaskLinks from "../../components/TaskLinks/TaskLinks";
import { setSelectedLinkedTaskListDispatch } from "../../actions/taskLinksActions";
import { useTranslation } from "react-i18next";

const mapStateToProps = state => ({
  userId: state.user.id,
  loadingTaskContentInProgress:
    state.project.selectedTaskFetchStatus === constants.WAITING,
  requestStatus: state.project.requestStatus,
  initialData: state.project.selectedTaskInitialState,
  projectUiid:
    state.project.selectedTaskInitialState &&
    state.project.selectedTaskInitialState.project &&
    state.project.selectedTaskInitialState.project.ui_id,
  writeAllowed: state.project.info && state.project.info.write_allowed,
  canCreateTask: state.project.info && state.project.info.write_allowed,
  loadingAttachmentsInProgress:
    state.attachments.fetchRequestStatus === constants.WAITING,
  isSignedIn: state.auth.isSignedIn,
  linkedTaskList: state.helpForSelectedTask.linkedList
});

function TaskContent({
  userId,

  isSignedIn,
  loadingAttachmentsInProgress,
  loadingTaskContentInProgress,
  editProjectTask,
  requestStatus,
  initialData,
  location,
  writeAllowed,
  setProjectSelectedTaskInitialStateDispatch,
  setSelectedLinkedTaskListDispatch,
  linkedTaskList,
  match: { params }
}) {
  const { t } = useTranslation();
  const { project_uiid } = params;
  const taskStatuses = provideTaskStatusesOptions();
  const parsed = queryString.parse(location.search);

  useEffect(() => {
    if (parsed.get_file_error_code === constants.NOT_FOUND) {
      toast.error(t("file_not_found"));
    }
  }, [parsed && parsed.get_file_error_code]);

  const [isEmptyDescriptionError, setIsEmptyDescriptionError] = useState(false);
  const [reporter, setReporter] = useState("");
  const [taskId, setTaskId] = useState(null);
  const [description, setDescription] = useState("");
  const [hashtags, setHashtags] = useState([]);
  const [taskPriority, setTaskPriority] = useState(null);
  const [taskType, setTaskType] = useState(constants.TASK_TYPES.TASK);
  const [parseHashtagsTimeout, setParseHashtagsTimeout] = useState(null);
  const [assignee, setAssignee] = useState(undefined);
  const [taskStatus, setTaskStatus] = useState("");
  const [isEditingDescription, setIsEditingDescription] = useState(false);

  const [presentHashtags, setPresentHashtags] = useState([]);
  const [subscribeReqestInProgress, setSubscribeReqestInProgress] = useState(
    false
  );
  const [disabledUpdateButton, setDisabledUpdateButton] = useState(true);

  const filterParams = takeItemFromLocalStorageSafe("filter_params", ""); //fulters from localStorage in URL to TaskSearch

  useEffect(() => {
    if (description) {
      setIsEmptyDescriptionError(false);
    }
  }, [description]);

  useEffect(() => {
    if (parseHashtagsTimeout) {
      clearTimeout(parseHashtagsTimeout);
      setParseHashtagsTimeout(null);
    }

    if (description) {
      setParseHashtagsTimeout(
        setTimeout(() => {
          setHashtags(parseHashtags(description));
          setParseHashtagsTimeout(null);
        }, 300)
      );
    } else {
      setHashtags([]);
    }
  }, [description]);

  useEffect(() => {
    if (initialData) {
      setIsEditingDescription(false);
      setTaskId(initialData.id);
      setPresentHashtags(initialData.hashtags);
      setDescription(initialData.description_md);

      setTaskStatus(
        initialData.task_status &&
          initialData.task_status.name_code && {
            value: initialData.task_status.name_code,
            label: t(`TaskStatuses.${initialData.task_status.name_code}`)
          }
      );

      setTaskType(initialData.task_type && initialData.task_type.name_code);

      setTaskPriority(
        initialData.task_priority && initialData.task_priority.name_code
      );

      setSelectedLinkedTaskListDispatch(initialData.linked_tasks);
    }
  }, [initialData]);

  useEffect(() => {
    if (presentHashtags && presentHashtags.length > 0) {
      const filteredPresentHashtags = [];
      presentHashtags.forEach(tagObj => {
        if (!filteredPresentHashtags.some(t => t.id === tagObj.id)) {
          filteredPresentHashtags.push(tagObj);
        }
      });

      if (filteredPresentHashtags.length !== presentHashtags.length) {
        setPresentHashtags(filteredPresentHashtags);
      }
    }
  }, [presentHashtags]);

  useEffect(() => {
    const hasChanged = () =>
      initialData.description_md !== description ||
      (initialData.task_priority &&
        initialData.task_priority.name_code !== taskPriority) ||
      (initialData.task_type && initialData.task_type.name_code !== taskType) ||
      (initialData.task_status &&
        initialData.task_status.name_code &&
        initialData.task_status.name_code !== taskStatus.value) ||
      ((initialData.assignee && initialData.assignee.id) || undefined) !==
        ((assignee && assignee.value) || undefined);

    if (initialData) {
      if (hasChanged()) {
        setDisabledUpdateButton(false);
      } else {
        setDisabledUpdateButton(true);
      }
    }
  }, [initialData, description, taskPriority, taskType, taskStatus, assignee]);

  const editTask = async e => {
    e.preventDefault();

    const blankDescription = !description.trim();

    if (blankDescription) {
      setIsEditingDescription(true);
      return toast.error(t("required_fields_contain_only_spaces"));
    }

    await editProjectTask({
      hashtag_ids: presentHashtags
        .filter(pht => hashtags.some(textTag => textTag === pht.name))
        .map(pht => pht.id),
      hashtag_names: hashtags,
      description: description.trim(),
      summary: cutFirstString(description),
      id: taskId,
      record_version: initialData.record_version,
      task_type: taskType || constants.TASK_TYPES.TASK,
      task_priority: taskPriority || constants.TASK_PRIORITIES.MAJOR,
      task_status: taskStatus.value,
      assignee: assignee && assignee.value ? assignee.value : null
    });
  };

  const canEditTask = writeAllowed;

  const isWaiting = requestStatus === actions.WAITING;

  const loadOrgMemberOptions = async searchString => {
    try {
      const response = await axios.get(
        endpoints.GET_ORGANIZATION_MEMBER_SEARCH,
        {
          params: {
            prefix: searchString,
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

        return foundMembers.map(m => ({
          value: m.id,
          label: uiUserFromUser(m.user)
        }));
      }
    } catch (e) {
      console.error(e);
      return [];
    }
  };

  return (
    <Page>
      <WithFetch fetchList={[constants.FETCH_TASK, constants.FETCH_PROJECT]}>
        {loadingTaskContentInProgress && <Loading />}
        {loadingTaskContentInProgress === false && initialData && (
          <div style={{ paddingBottom: 10 }}>
            <div className="task-edit-header">
              <div>
                <GoBackButton
                  pathToBack={`${paths.ProjectSingle.toPath({
                    project_uiid: project_uiid
                  })}?${queryString.stringify(filterParams, {
                    arrayFormat: "comma"
                  })}`}
                />
              </div>
              <div className="task-edit-header_reporter">
                <div className="descriptor">
                  <span>
                    {t("Reported")} {t("by_lc")}{" "}
                  </span>
                  {reporter}
                  <span> {t("at_lc")} </span>
                  <span>{uiDateTime(initialData.created_at)}</span>
                </div>
              </div>
            </div>
            <fieldset style={{ marginTop: 10 }}>
              <div style={{ display: "flex" }}>
                <div style={{ flex: 1, paddingRight: 40, paddingTop: 17 }}>
                  {!isEditingDescription && initialData && (
                    <div style={{ display: "flex" }}>
                      <div
                        style={{ flex: 1, whiteSpace: "pre-wrap" }}
                        className="task-html-container"
                        dangerouslySetInnerHTML={{
                          __html: initialData.description_html
                        }}
                      />

                      <div style={{ paddingLeft: 20 }}>
                        <IconButton
                          onClick={() => {
                            setIsEditingDescription(true);
                          }}
                          aria-label={t("settings")}
                        >
                          <Icon>edit</Icon>
                        </IconButton>
                      </div>
                    </div>
                  )}
                  {isEditingDescription && (
                    <ReactTextareaAutocomplete
                      className={classNames({
                        "error-emply-field": isEmptyDescriptionError
                      })}
                      loadingComponent={() => <Loading />}
                      minChar={1}
                      maxLength={10000}
                      style={{ fontSize: 16, color: "#313131" }}
                      trigger={{
                        "#": {
                          dataProvider: async token => {
                            try {
                              const response = await axios.get(
                                endpoints.GET_TASK_HASHTAG_SEARCH,
                                {
                                  params: {
                                    prefix: token,
                                    include_used: false,
                                    project_ui_id: params.project_uiid
                                  }
                                }
                              );
                              if (responseIsStatusOk(response)) {
                                setPresentHashtags([
                                  ...presentHashtags,
                                  ...response.data.data
                                ]);
                                return [
                                  ...response.data.data.map(t => t.name)
                                ].filter(t => !hashtags.some(h => h === t));
                              }
                            } catch (e) {
                              console.error(e);
                              return [];
                            }
                          },
                          component: ({ entity: entity }) => (
                            <div>{`#${entity}`}</div>
                          ),
                          output: (item, trigger) => trigger + item
                        }
                      }}
                      placeholder={t("Task description")}
                      value={description}
                      required
                      id="task_description"
                      onChange={e => setDescription(e.target.value)}
                    />
                  )}
                </div>

                <div style={{ maxWidth: 300 }}>
                  <div style={{ padding: "15px 0" }}>
                    <div style={{ paddingBottom: 8, paddingLeft: 10 }}>
                      {`${t("TaskTypes.self")}:`}
                    </div>
                    <div>
                      <TagRadioButton
                        clickable={canEditTask && !isWaiting}
                        selected={taskType === constants.TASK_TYPES.BUG}
                        onClick={() => setTaskType(constants.TASK_TYPES.BUG)}
                        style={{
                          color: constants.TASKS_COLORS.TASK_TYPES_COLORS.BUG
                        }}
                      >
                        bug
                      </TagRadioButton>
                      <TagRadioButton
                        clickable={canEditTask && !isWaiting}
                        selected={
                          taskType === constants.TASK_TYPES.TASK ||
                          taskType === null
                        }
                        onClick={() => setTaskType(constants.TASK_TYPES.TASK)}
                        style={{
                          color: constants.TASKS_COLORS.TASK_TYPES_COLORS.TASK
                        }}
                      >
                        task
                      </TagRadioButton>
                      <TagRadioButton
                        clickable={canEditTask && !isWaiting}
                        selected={taskType === constants.TASK_TYPES.NEW_FEATURE}
                        onClick={() =>
                          setTaskType(constants.TASK_TYPES.NEW_FEATURE)
                        }
                        style={{
                          color:
                            constants.TASKS_COLORS.TASK_TYPES_COLORS.NEW_FEATURE
                        }}
                      >
                        feature
                      </TagRadioButton>
                    </div>
                  </div>

                  <div style={{ padding: "0 0 20px" }}>
                    <div
                      style={{
                        paddingBottom: 8,
                        paddingLeft: 10,
                        paddingTop: 5
                      }}
                    >
                      {`${t("TaskPriorities.self")}:`}
                    </div>
                    <div>
                      <TagRadioButton
                        clickable={canEditTask && !isWaiting}
                        selected={
                          taskPriority === constants.TASK_PRIORITIES.CRITICAL
                        }
                        onClick={() =>
                          setTaskPriority(constants.TASK_PRIORITIES.CRITICAL)
                        }
                        style={{
                          color:
                            constants.TASKS_COLORS.TASK_PRIORITIES_COLORS
                              .CRITICAL
                        }}
                      >
                        critical
                      </TagRadioButton>
                      <TagRadioButton
                        clickable={canEditTask && !isWaiting}
                        selected={
                          taskPriority === constants.TASK_PRIORITIES.MAJOR ||
                          taskPriority === null
                        }
                        onClick={() =>
                          setTaskPriority(constants.TASK_PRIORITIES.MAJOR)
                        }
                        style={{
                          color:
                            constants.TASKS_COLORS.TASK_PRIORITIES_COLORS.MAJOR
                        }}
                      >
                        major
                      </TagRadioButton>
                      <TagRadioButton
                        clickable={canEditTask && !isWaiting}
                        selected={
                          taskPriority === constants.TASK_PRIORITIES.MINOR
                        }
                        onClick={() =>
                          setTaskPriority(constants.TASK_PRIORITIES.MINOR)
                        }
                        style={{
                          color:
                            constants.TASKS_COLORS.TASK_PRIORITIES_COLORS.MINOR
                        }}
                      >
                        minor
                      </TagRadioButton>
                    </div>
                  </div>

                  <div style={{ paddingBottom: 20 }}>
                    <TaskSearchSelectFilter
                      value={taskStatus && taskStatus.value}
                      label={t("TaskStatuses.self")}
                      formControlStyle={{ width: "100%" }}
                      options={taskStatuses}
                      onChange={e =>
                        setTaskStatus({
                          value: e.target.value,
                          label: t(`TaskStatuses.${e.target.value}`)
                        })
                      }
                    />
                  </div>

                  <div style={{ paddingBottom: 20 }}>
                    <TaskSearchFetchSelectFilter
                      placeholder={t("Assignee")}
                      label={t("Assignee")}
                      onChange={e => setAssignee(e)}
                      value={assignee}
                      loadOptions={loadOrgMemberOptions}
                    />
                  </div>

                  <div className="buttons" style={{ paddingTop: 20 }}>
                    {canEditTask && (
                      <div style={{ marginRight: 25 }}>
                        <Button
                          onClick={e => {
                            editTask(e);
                            if (!description) {
                              setIsEmptyDescriptionError(true);
                            }
                          }}
                          disabled={isWaiting || disabledUpdateButton}
                        >
                          {t("Button.Update")}
                        </Button>
                      </div>
                    )}
                    {initialData && !loadingTaskContentInProgress && 1 === 2 && (
                      /* TODO Denis */ <div>
                        {initialData.subscribers_count}{" "}
                        {initialData.subscribers_count === 1
                          ? t("subscriber_lc")
                          : t("subscribers_lc")}
                      </div>
                    )}
                    <div className="un-sub-scribe_block">
                      {isSignedIn &&
                        initialData &&
                        initialData.subscribed &&
                        !loadingTaskContentInProgress && (
                          <Button
                            style={{ backgroundColor: "#06ab74", flexGrow: 1 }}
                            disabled={subscribeReqestInProgress}
                            onClick={async () => {
                              try {
                                if (subscribeReqestInProgress) {
                                  return toast.info(
                                    t("this_request_is_already_in_progress")
                                  );
                                }

                                setSubscribeReqestInProgress(true);

                                const response = await axios.delete(
                                  endpoints.DELETE_TASK_SUBSCRIBER,
                                  { data: { task_id: initialData.id } }
                                );

                                if (responseIsStatusOk(response)) {
                                  toast.success(t("you_unsubscribed"));

                                  setProjectSelectedTaskInitialStateDispatch({
                                    ...initialData,
                                    subscribed: false,
                                    subscribers_count:
                                      initialData.subscribers_count - 1
                                  });
                                }
                              } catch (e) {
                                toast.error(t("Errors.Error"));
                                console.error(e);
                              } finally {
                                setSubscribeReqestInProgress(false);
                              }
                            }}
                          >
                            {t("unsubscribe")}
                          </Button>
                        )}
                      {isSignedIn &&
                        initialData &&
                        !initialData.subscribed &&
                        !loadingTaskContentInProgress && (
                          <Button
                            style={{ backgroundColor: "#06ab74", flexGrow: 1 }}
                            unelevated={true}
                            disabled={subscribeReqestInProgress}
                            onClick={async () => {
                              try {
                                if (subscribeReqestInProgress) {
                                  return toast.info(
                                    t("this_request_is_already_in_progress")
                                  );
                                }

                                setSubscribeReqestInProgress(true);

                                const response = await axios.post(
                                  endpoints.POST_TASK_SUBSCRIBER,
                                  { task_id: initialData.id }
                                );

                                if (responseIsStatusOk(response)) {
                                  toast.success(t("you_subscribed"));

                                  setProjectSelectedTaskInitialStateDispatch({
                                    ...initialData,
                                    subscribed: true,
                                    subscribers_count:
                                      initialData.subscribers_count + 1
                                  });
                                }
                              } catch (e) {
                                toast.error(t("Errors.Error"));
                                console.error(e);
                              } finally {
                                setSubscribeReqestInProgress(false);
                              }
                            }}
                          >
                            {t("subscribe")}
                          </Button>
                        )}
                    </div>
                  </div>
                  <div className="last-update-date">
                    <span>{t("updated_at")}</span>{" "}
                    <span>{uiDateTime(initialData.updated_at)}</span>
                  </div>
                </div>
              </div>
            </fieldset>
            {hashtags && hashtags.length > 0 && (
              <div style={{ paddingTop: 30 }}>
                {hashtags.map((hashtag, index) => (
                  <Chip
                    style={{ marginRight: 8, marginBottom: 8 }}
                    key={index}
                    label={"#" + hashtag}
                  />
                ))}
              </div>
            )}
          </div>
        )}
        {!loadingTaskContentInProgress && (
          <Attachments
            hiddenLoading={loadingTaskContentInProgress}
            canUpload={canEditTask}
            taskId={taskId}
          />
        )}
        {!loadingTaskContentInProgress &&
          initialData &&
          initialData.linked_tasks &&
          linkedTaskList && <TaskLinks taskId={taskId} data={linkedTaskList} />}
        <Chat
          hideLoadingSpinner={
            loadingTaskContentInProgress || loadingAttachmentsInProgress
          }
          chatType={constants.TASK}
          chatId={taskId}
        />
      </WithFetch>
    </Page>
  );
}

export default withRouter(
  connect(mapStateToProps, {
    editProjectTask,
    setProjectSelectedTaskInitialStateDispatch,
    setSelectedLinkedTaskListDispatch
  })(TaskContent)
);
