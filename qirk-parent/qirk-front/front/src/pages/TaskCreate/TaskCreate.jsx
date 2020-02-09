import React, { useState, useEffect } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { t } from "i18next";
import classNames from "classnames";
import ReactTextareaAutocomplete from "@webscopeio/react-textarea-autocomplete";
import "@webscopeio/react-textarea-autocomplete/style.css";
import Loading from "../../components/Loading/Loading";
import { createProjectTask } from "../../actions/projectActions";
import {
  setClearAttachmentsDispatch,
  setClearLinkedTasksListDispatch
} from "../../actions/uiActions";
import Button from "../../components/Button/Button";
import useRequestResultToast from "../../utils/hooks/useRequestResultToast";
import axios from "../../utils/axios";
import actions from "../../utils/constants";
import constants from "../../utils/constants";
import endpoints from "../../utils/endpoints";
import {
  uiUserFromUser,
  provideTaskStatusesOptions,
  responseIsStatusOk,
  cutFirstString,
  parseHashtags,
  saveItemToLocalStorageSafe,
  takeItemFromLocalStorageSafe,
  setTimeOutToWrite,
  removeItemFromLocalStorageSafe
} from "../../utils/variousUtils";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import WithFetch from "../../components/WithFetch/WithFetch";
import "./TaskCreate.sass";
import TypableSelectInput from "../../components/TypableSelectInput/TypableSelectInput";
import { toast } from "react-toastify";
import { Chip, IconButton, Icon } from "@material-ui/core";
import paths from "../../routes/paths";
import TagRadioButton from "../../components/TagRadioButton/TagRadioButton";
import GoBackButton from "../../components/GoBackButton/GoBackButton";
import TaskSearchSelectFilter from "../../components/TaskSearchSelectFilter/TaskSearchSelectFilter";
import TaskLinks from "../../components/TaskLinks/TaskLinks";
import ReactTooltip from "react-tooltip";
import { renderToStaticMarkup } from "react-dom/server";
import Attachments from "../../components/Attachments/Attachments";
import queryString from "query-string";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";

const mapStateToProps = state => ({
  userId: state.user.id,
  projectId: state.project.id,
  projectInfo: state.project.info,
  requestStatus: state.project.requestStatus,
  canCreateTask: state.project.info && state.project.info.write_allowed
});

function TaskCreate({
  userId,
  projectId,
  createProjectTask,
  setClearAttachmentsDispatch,
  setClearLinkedTasksListDispatch,
  requestStatus,
  canCreateTask,
  match: { params }
}) {
  const { project_uiid } = params;
  const taskStatuses = provideTaskStatusesOptions();

  const [isEmptyDescriptionError, setIsEmptyDescriptionError] = useState(false);
  const [doBounce, setDoBounce] = useState(false);
  const [description, setDescription] = useState(
    takeItemFromLocalStorageSafe("task_description", "")
  );
  const [hashtags, setHashtags] = useState([]);
  const [taskPriority, setTaskPriority] = useState(
    takeItemFromLocalStorageSafe(
      "task_priority",
      constants.TASK_PRIORITIES.MAJOR
    )
  );
  const [parseHashtagsTimeout, setParseHashtagsTimeout] = useState(null);

  const [assignee, setAssignee] = useState(
    takeItemFromLocalStorageSafe("task_assignee", "")
  );
  const [taskType, setTaskType] = useState(
    takeItemFromLocalStorageSafe("task_type", constants.TASK_TYPES.TASK)
  );
  const [taskStatus, setTaskStatus] = useState(
    takeItemFromLocalStorageSafe("task_status", taskStatuses[0])
  );

  const [assigneeList, setassigneeList] = useState("");
  const [presentHashtags, setPresentHashtags] = useState([]);

  const filterParams = takeItemFromLocalStorageSafe("filter_params", ""); //fulters from localStorage in URL to TaskSearch

  const [open, setOpen] = useState(false);
  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };

  useRequestResultToast(requestStatus);

  useEffect(() => {
    if (description) {
      setIsEmptyDescriptionError(false);
    }
  }, [description]);

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
  }, [description, setDescription, setHashtags]);

  useEffect(() => {
    fetchAssigneeList();
  }, []);

  const fetchAssigneeList = async () => {
    try {
      const response = await axios.get(
        endpoints.GET_ORGANIZATION_MEMBER_SEARCH,
        {
          params: {
            prefix: "",
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
        setassigneeList(
          response.data.data.map(m => ({
            value: m.id,
            label: uiUserFromUser(m.user)
          }))
        );
      }
    } catch (e) {
      console.error(e);
    }
  };

  const onTaskSubmit = async e => {
    e.preventDefault();

    const blankDescription = !description.trim();

    if (blankDescription) {
      setIsEmptyDescriptionError(true);
      return toast.error(t("required_fields_contain_only_spaces"));
    }

    await createProjectTask(
      {
        hashtag_ids: presentHashtags
          .filter(pht => hashtags.some(textTag => textTag === pht.name))
          .map(pht => pht.id),
        hashtag_names: hashtags,
        description: description.trim(),
        summary: cutFirstString(description),
        assignee: (assignee && assignee.value) || null,
        assign_to_me: false,
        task_type: taskType,
        task_status: taskStatus.value,
        task_priority: taskPriority,
        project: { ui_id: params && params.project_uiid },
        linked_tasks: takeItemFromLocalStorageSafe(
          "selected_task_links_list",
          []
        ).map(item => item.value)
      },
      takeItemFromLocalStorageSafe("attachments", []),
      doBounce,
      () => {
        setDescription("");
        removeItemFromLocalStorageSafe(
          "task_description",
          "task_assignee",
          "task_status",
          "task_type",
          "task_priority",
          "selected_task_links_list",
          "attachments"
        );
        setClearAttachmentsDispatch(true);
        setClearLinkedTasksListDispatch(true);
      }
    );
  };

  const isWaiting = requestStatus === actions.WAITING;

  return (
    <PrivatePage>
      <WithFetch fetchList={[constants.FETCH_PROJECT]}>
        <div>
          <GoBackButton
            pathToBack={`${paths.ProjectSingle.toPath({
              project_uiid: project_uiid
            })}?${queryString.stringify(filterParams, {
              arrayFormat: "comma"
            })}`}
          />
        </div>
        <fieldset disabled={isWaiting || !canCreateTask}>
          <div className="headline-and-reset-form-block">
            <h1 style={{ margin: 0 }}>{t("Task create")}</h1>
            <ReactTooltip
              id="resetFormForCreateTask"
              place="bottom"
              type="dark"
              effect="solid"
            />
            <span
              data-tip={renderToStaticMarkup(<>{t("reset_form")}</>)}
              data-for="resetFormForCreateTask"
              style={{ cursor: "pointer" }}
            >
              <IconButton onClick={handleClickOpen}>
                <Icon
                  style={{
                    transform: "scale(-1,1)"
                  }}
                >
                  refresh
                </Icon>
              </IconButton>
            </span>
            <Dialog
              open={open}
              onClose={handleClose}
              aria-labelledby="form-dialog-title"
            >
              <DialogTitle id="form-dialog-title">
                {t("reset_form")}
              </DialogTitle>
              <DialogContent>{t("reset_form_confirmation")}</DialogContent>
              <DialogActions>
                <Button raised={false} outlined={false} onClick={handleClose}>
                  {t("cancel")}
                </Button>
                <Button
                  raised={false}
                  outlined={false}
                  onClick={() => {
                    setDescription("");
                    setTaskPriority(constants.TASK_PRIORITIES.MAJOR);
                    setAssignee("");
                    setTaskType(constants.TASK_TYPES.TASK);
                    setTaskStatus(taskStatuses[0]);
                    setClearAttachmentsDispatch(true);
                    setClearLinkedTasksListDispatch(true);
                    handleClose();
                  }}
                >
                  {t("reset")}
                </Button>
              </DialogActions>
            </Dialog>
          </div>
          <form onSubmit={onTaskSubmit}>
            <div style={{ display: "flex" }}>
              <div style={{ flex: 1, paddingRight: 40, paddingTop: 17 }}>
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
                  onChange={e => {
                    setDescription(e.target.value);
                    setTimeOutToWrite(function() {
                      saveItemToLocalStorageSafe(
                        "task_description",
                        e.target.value
                      );
                    }, 500);
                  }}
                />
              </div>
              <div style={{ maxWidth: 300 }}>
                <div style={{ padding: "15px 0", width: "300px" }}>
                  <div style={{ paddingBottom: 8, paddingLeft: 10 }}>
                    {`${t("TaskTypes.self")}:`}
                  </div>
                  <div>
                    <TagRadioButton
                      clickable={canCreateTask}
                      selected={taskType === constants.TASK_TYPES.BUG}
                      onClick={() => {
                        setTaskType(constants.TASK_TYPES.BUG);
                        saveItemToLocalStorageSafe(
                          "task_type",
                          constants.TASK_TYPES.BUG
                        );
                      }}
                      style={{
                        color: constants.TASKS_COLORS.TASK_TYPES_COLORS.BUG
                      }}
                    >
                      bug
                    </TagRadioButton>
                    <TagRadioButton
                      clickable={canCreateTask}
                      selected={
                        taskType === constants.TASK_TYPES.TASK ||
                        taskType === null
                      }
                      onClick={() => {
                        setTaskType(constants.TASK_TYPES.TASK);
                        saveItemToLocalStorageSafe(
                          "task_type",
                          constants.TASK_TYPES.TASK
                        );
                      }}
                      style={{
                        color: constants.TASKS_COLORS.TASK_TYPES_COLORS.TASK
                      }}
                    >
                      task
                    </TagRadioButton>
                    <TagRadioButton
                      clickable={canCreateTask}
                      selected={taskType === constants.TASK_TYPES.NEW_FEATURE}
                      onClick={() => {
                        setTaskType(constants.TASK_TYPES.NEW_FEATURE);
                        saveItemToLocalStorageSafe(
                          "task_type",
                          constants.TASK_TYPES.NEW_FEATURE
                        );
                      }}
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
                    style={{ paddingBottom: 8, paddingLeft: 10, paddingTop: 5 }}
                  >
                    {`${t("TaskPriorities.self")}:`}
                  </div>
                  <div>
                    <TagRadioButton
                      clickable={canCreateTask}
                      selected={
                        taskPriority === constants.TASK_PRIORITIES.CRITICAL
                      }
                      onClick={() => {
                        setTaskPriority(constants.TASK_PRIORITIES.CRITICAL);
                        saveItemToLocalStorageSafe(
                          "task_priority",
                          constants.TASK_PRIORITIES.CRITICAL
                        );
                      }}
                      style={{
                        color:
                          constants.TASKS_COLORS.TASK_PRIORITIES_COLORS.CRITICAL
                      }}
                    >
                      critical
                    </TagRadioButton>
                    <TagRadioButton
                      clickable={canCreateTask}
                      selected={
                        taskPriority === constants.TASK_PRIORITIES.MAJOR ||
                        taskPriority === null
                      }
                      onClick={() => {
                        setTaskPriority(constants.TASK_PRIORITIES.MAJOR);
                        saveItemToLocalStorageSafe(
                          "task_priority",
                          constants.TASK_PRIORITIES.MAJOR
                        );
                      }}
                      style={{
                        color:
                          constants.TASKS_COLORS.TASK_PRIORITIES_COLORS.MAJOR
                      }}
                    >
                      major
                    </TagRadioButton>
                    <TagRadioButton
                      clickable={canCreateTask}
                      selected={
                        taskPriority === constants.TASK_PRIORITIES.MINOR
                      }
                      onClick={() => {
                        setTaskPriority(constants.TASK_PRIORITIES.MINOR);
                        saveItemToLocalStorageSafe(
                          "task_priority",
                          constants.TASK_PRIORITIES.MINOR
                        );
                      }}
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
                    onChange={e => {
                      setTaskStatus({
                        value: e.target.value,
                        label: t(`TaskStatuses.${e.target.value}`)
                      });
                      saveItemToLocalStorageSafe("task_status", {
                        value: e.target.value,
                        label: t(`TaskStatuses.${e.target.value}`)
                      });
                    }}
                  />
                </div>

                <div style={{ paddingBottom: 20 }}>
                  <TypableSelectInput
                    style={{ width: "100%" }}
                    isClearable
                    onChange={e => {
                      setAssignee(e);
                      saveItemToLocalStorageSafe("task_assignee", e);
                    }}
                    value={assignee ? assignee : ""}
                    placeholder={t("Assignee")}
                    options={assigneeList || []}
                  />
                </div>

                <div>
                  <Button
                    type="submit"
                    onClick={() => {
                      setDoBounce(true);
                      if (!description) {
                        setIsEmptyDescriptionError(true);
                      }
                    }}
                    style={{ marginTop: 15, width: "100%" }}
                    disabled={isWaiting}
                  >
                    {t("Button.Create")}
                  </Button>
                </div>
                <div>
                  <Button
                    type="submit"
                    onClick={() => {
                      setDoBounce(false);
                      if (!description) {
                        setIsEmptyDescriptionError(true);
                      }
                    }}
                    style={{ marginTop: 15, width: "100%" }}
                    disabled={isWaiting}
                  >
                    {t("Button.CreateAndStay")}
                  </Button>
                </div>
              </div>
            </div>
            <Attachments
              projectId={projectId}
              canUpload={canCreateTask}
              isTaskCreate={true}
            />
            <TaskLinks isTaskCreate={true} />
          </form>
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
      </WithFetch>
    </PrivatePage>
  );
}

export default withRouter(
  connect(
    mapStateToProps,
    {
      createProjectTask,
      setClearAttachmentsDispatch,
      setClearLinkedTasksListDispatch
    }
  )(TaskCreate)
);
