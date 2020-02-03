import React, { useState, useEffect, useRef } from "react";
import { withRouter, Link } from "react-router-dom";
import { Paper } from "@material-ui/core";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import { t } from "i18next";
import { uiTaskSummary, uiUserFromOrgMember } from "../../utils/variousUtils";
import paths from "../../routes/paths";
import {
  removeSelectedLinkedTaskDispatch,
  setSelectedLinkedTaskDispatch,
  disarmAllDeleteLinkedTaskButtonsDispatch,
  disarmAllDeleteLinkedTaskButtonsAndArmOneDispatch
} from "../../actions/taskLinksActions";
import {
  setLinkedTasksListDeletingItemDispatch,
  removeLinkedTasksListDeletingItemDispatch
} from "../../actions/uiActions";
import { connect } from "react-redux";
import endpoints from "../../utils/endpoints";
import axios from "../../utils/axios";
import { toast } from "react-toastify";
import {
  saveItemToLocalStorageSafe,
  takeItemFromLocalStorageSafe,
  errorIsAlreadyExists,
  responseIsStatusOk
} from "../../utils/variousUtils";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import TypableSelectInput from "../../components/TypableSelectInput/TypableSelectInput";
import Button from "../../components/Button/Button";
import Loading from "../Loading/Loading";
import classNames from "classnames";
import "./TaskLinks.sass";
import { Icon, IconButton } from "@material-ui/core";
import useOutsideClickListener from "../../utils/hooks/useOutsideClickListener";
import { setClearLinkedTasksListDispatch } from "../../actions/uiActions";

function TaskLinks({
  data,
  taskId = null,
  projectId,
  clearLinkedTasksList,
  removeSelectedLinkedTaskDispatch,
  setSelectedLinkedTaskDispatch,
  linkedTasksListDeleting,
  setLinkedTasksListDeletingItemDispatch,
  removeLinkedTasksListDeletingItemDispatch,
  disarmAllDeleteLinkedTaskButtonsDispatch,
  disarmAllDeleteLinkedTaskButtonsAndArmOneDispatch,
  setClearLinkedTasksListDispatch,
  isTaskCreate = false,
  match: { params }
}) {
  const deleteBtnRef = useRef();
  const dialogRef = useRef();
  const [xHoveredOver, setXHoveredOver] = useState(null);

  const [open, setOpen] = useState(false);
  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };

  //linked-task
  /*selectedTaskLinksList---------->data*/
  const [linkedTask, setLinkedTask] = useState(""); //выбраный сейчас линкованный таск
  const [linkedTasksList, setLinkedTasksList] = useState([]); //доступные линкованые таски
  const [selectedTaskLinksList, setSelectedTaskLinksList] = isTaskCreate
    ? useState(takeItemFromLocalStorageSafe("selected_task_links_list", []))
    : [data, null]; //выбранные линковые таски
  const [postTaskInProgress, setPostTaskInProgress] = useState(false);

  //пост линков
  const postTaskLink = isTaskCreate
    ? () => {
        setPostTaskInProgress(true);
        try {
          setSelectedTaskLinksList([...selectedTaskLinksList, linkedTask]);
          setLinkedTasksList(
            linkedTasksList.filter(item => item.value !== linkedTask.value)
          );
          setOpen(false);
          setLinkedTask("");
        } catch (e) {
          console.error(e);
          toast.error(t("Errors.Error"));
        } finally {
          setPostTaskInProgress(false);
        }
      }
    : async () => {
        setPostTaskInProgress(true);
        try {
          const response = await axios.post(endpoints.POST_TASK_LINK, {
            task1: taskId,
            task2: linkedTask.id
          });
          if (responseIsStatusOk(response)) {
            toast.success(t("task_linked"));
            setSelectedLinkedTaskDispatch(linkedTask);
            setOpen(false);
            setLinkedTasksList(
              linkedTasksList.filter(item => item.value !== linkedTask.value)
            );
            setLinkedTask("");
            fetchListLinks();
          }
        } catch (e) {
          console.error(e);
          if (errorIsAlreadyExists(e)) {
            toast.error(t("these_tasks_are_already_connected"));
          } else {
            toast.error(t("Errors.Error"));
          }
        } finally {
          setPostTaskInProgress(false);
        }
      };

  //получить список доступных линкованных тасков
  const fetchListLinks = isTaskCreate
    ? async () => {
        try {
          const response = await axios.get(endpoints.GET_LIST_LINK_OPTIONS, {
            params: { project_id: projectId }
          });
          if (
            responseIsStatusOk(response) &&
            response &&
            response.data &&
            response.data.data
          ) {
            setLinkedTasksList(
              response.data.data
                .map(e => ({
                  label: uiTaskSummary(e.number, e.summary),
                  value: e.id,
                  assignee: e.assignee,
                  task_status: e.task_status,
                  summary: e.summary,
                  number: e.number,
                  id: e.id
                }))
                .filter(item => {
                  return !selectedTaskLinksList.some(
                    i => i.value === item.value
                  );
                })
            );
          }
        } catch (e) {
          console.error(e);
        }
      }
    : async () => {
        try {
          const response = await axios.get(endpoints.GET_LIST_LINK_OPTIONS, {
            params: { task_id: taskId }
          });
          if (
            responseIsStatusOk(response) &&
            response &&
            response.data &&
            response.data.data
          ) {
            setLinkedTasksList(
              response.data.data
                .map(e => ({
                  label: uiTaskSummary(e.number, e.summary),
                  value: e.id,
                  assignee: e.assignee,
                  task_status: e.task_status,
                  summary: e.summary,
                  number: e.number,
                  id: e.id
                }))
                .filter(e => e.value !== taskId)
            );
          }
        } catch (e) {
          console.error(e);
        }
      };

  //в зависимости откуда берётся линкед такски, при удалении работают разные функции
  const deleteTaskLink = isTaskCreate
    ? item => {
        setLinkedTasksListDeletingItemDispatch(item);
        try {
          setSelectedTaskLinksList(
            selectedTaskLinksList.filter(e => e.value !== item.value)
          );
          setLinkedTasksList([
            ...linkedTasksList,
            { ...item, deleteBtnArmed: false }
          ]);
          toast.success(t("link_deleted"));
          removeLinkedTasksListDeletingItemDispatch(item);
          setXHoveredOver(null);
        } catch (e) {
          console.error(e);
          toast.error(t("Errors.Error"));
        }
      }
    : async item => {
        setLinkedTasksListDeletingItemDispatch(item);
        try {
          const response = await axios.delete(endpoints.REMOVE_TASK_LINK, {
            data: { task1: taskId, task2: item.id }
          });
          if (responseIsStatusOk(response)) {
            removeSelectedLinkedTaskDispatch(item);
            toast.success(t("link_deleted"));
            removeLinkedTasksListDeletingItemDispatch(item);
            fetchListLinks();
            setXHoveredOver(null);
          }
        } catch (e) {
          console.error(e);
          toast.error(t("Errors.Error"));
        }
      };

  useEffect(() => {
    if (isTaskCreate) {
      if (clearLinkedTasksList) {
        setLinkedTasksList([...linkedTasksList, ...selectedTaskLinksList]);
        setSelectedTaskLinksList([]);
        setClearLinkedTasksListDispatch(false);
      }
      saveItemToLocalStorageSafe(
        "selected_task_links_list",
        selectedTaskLinksList
      );
    }
  }, [selectedTaskLinksList, clearLinkedTasksList]); //при обновлении выбранных тасков сохранять их в локал сторадж

  //при получении
  useEffect(() => {
    if (isTaskCreate) {
      projectId && fetchListLinks();
      saveItemToLocalStorageSafe("project_id", projectId);
    } else {
      taskId && fetchListLinks();
    }
  }, [projectId, taskId]);

  useOutsideClickListener(
    deleteBtnRef,
    isTaskCreate
      ? (
          isDialog = dialogRef &&
            dialogRef.current &&
            dialogRef.current.contains(event.target)
        ) => {
          !isDialog &&
            setSelectedTaskLinksList(
              selectedTaskLinksList.map(task => ({
                ...task,
                deleteBtnArmed: false
              }))
            );
        }
      : (
          isDialog = dialogRef &&
            dialogRef.current &&
            dialogRef.current.contains(event.target)
        ) => {
          !isDialog && disarmAllDeleteLinkedTaskButtonsDispatch();
        },
    () => {
      return;
    }
  );

  return (
    <div>
      <div
        className="task-links"
        style={{
          display: "flex",
          alignItems: "center"
        }}
      >
        <div>
          <h3>{t("Linked_tasks")}</h3>
        </div>
        <div>
          <IconButton onClick={handleClickOpen}>
            <Icon>add_box</Icon>
          </IconButton>
          <Dialog
            ref={dialogRef}
            fullWidth
            open={open}
            onClose={handleClose}
            aria-labelledby="form-dialog-title"
          >
            <DialogTitle id="form-dialog-title">
              {t("new_linked_task")}
            </DialogTitle>
            <DialogContent style={{ minHeight: 280 }}>
              <TypableSelectInput
                autoFocus
                fullWidth
                maxHeightOfMenuList={220}
                isClearable
                menuPlacement={"bottom"}
                onChange={setLinkedTask}
                value={linkedTask || ""}
                placeholder={t("task")}
                options={linkedTasksList || []}
              />
            </DialogContent>
            <DialogActions>
              <Button raised={false} outlined={false} onClick={handleClose}>
                {t("cancel")}
              </Button>
              <Button
                raised={false}
                outlined={false}
                disabled={!linkedTask || postTaskInProgress}
                onClick={postTaskLink}
              >
                {t("add")}
                {postTaskInProgress && (
                  <Loading
                    style={{
                      position: "absolute",
                      top: "50%",
                      left: "50%",
                      marginTop: -12,
                      marginLeft: -12
                    }}
                    size={25}
                  />
                )}
              </Button>
            </DialogActions>
          </Dialog>
        </div>
      </div>
      {selectedTaskLinksList.length > 0 && (
        <Paper>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>{t("linked_task")}</TableCell>
                <TableCell>{t("Assignee")}</TableCell>
                <TableCell>{t("status")}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody ref={deleteBtnRef}>
              {selectedTaskLinksList.map((item, index) => (
                <TableRow key={index}>
                  <TableCell>
                    <Link
                      className={classNames({
                        "x-hovered-linked-task": xHoveredOver === item.id
                      })}
                      to={paths.TaskContent.toPath({
                        organization_uiid:
                          params.organization_uiid || "organization_uiid",
                        project_uiid: params.project_uiid || "project_uiid",
                        task_number: item.number || "task_number"
                      })}
                    >
                      {uiTaskSummary(item.number, item.summary)}
                    </Link>
                  </TableCell>
                  <TableCell
                    className={classNames({
                      "x-hovered-linked-task": xHoveredOver === item.id
                    })}
                  >
                    {(item.assignee && uiUserFromOrgMember(item.assignee)) ||
                      (item.assignee === null && (
                        <span>{t("unassigned")}</span>
                      ))}
                  </TableCell>
                  <TableCell
                    className={classNames({
                      "x-hovered-linked-task": xHoveredOver === item.id
                    })}
                  >
                    <div
                      style={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        height: "100%"
                      }}
                    >
                      {item.task_status && item.task_status.name_code && (
                        <span>
                          {t(
                            `TaskStatuses.${item.task_status &&
                              item.task_status.name_code}`
                          )}
                        </span>
                      )}
                      <div
                        onMouseOver={() => {
                          setXHoveredOver(item.id);
                        }}
                        onMouseOut={() => {
                          setXHoveredOver(null);
                        }}
                        className="delete-btn-wrapper"
                      >
                        {linkedTasksListDeleting &&
                          linkedTasksListDeleting.includes(item.id) && (
                            <Loading
                              style={{
                                padding: 12,
                                height: "100%"
                              }}
                              size={24}
                            />
                          )}
                        {linkedTasksListDeleting &&
                          !linkedTasksListDeleting.includes(item.id) &&
                          !item.deleteBtnArmed && (
                            <IconButton
                              onClick={() => {
                                if (isTaskCreate) {
                                  setSelectedTaskLinksList(
                                    selectedTaskLinksList.map(e => {
                                      const isThisTheOne =
                                        item.id && e.id && e.id === item.id;
                                      return {
                                        ...e,
                                        deleteBtnArmed: isThisTheOne
                                      };
                                    })
                                  );
                                } else {
                                  disarmAllDeleteLinkedTaskButtonsAndArmOneDispatch(
                                    item
                                  );
                                }
                              }}
                            >
                              <Icon>delete</Icon>
                            </IconButton>
                          )}
                        {item.deleteBtnArmed &&
                          linkedTasksListDeleting &&
                          !linkedTasksListDeleting.includes(item.id) && (
                            <IconButton
                              style={{ color: "red" }}
                              onClick={() => {
                                deleteTaskLink(item);
                              }}
                            >
                              <Icon>close</Icon>
                            </IconButton>
                          )}
                      </div>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      )}
    </div>
  );
}

// export default  withRouter(TaskLinks);
export default withRouter(
  connect(
    state => ({
      projectId: state.project.id,
      linkedTasksListDeleting: state.ui.linkedTasksListDeleting,
      clearLinkedTasksList: state.helpForSelectedTask.clearLinkedTasksList
    }),
    {
      removeSelectedLinkedTaskDispatch,
      setSelectedLinkedTaskDispatch,
      setLinkedTasksListDeletingItemDispatch,
      removeLinkedTasksListDeletingItemDispatch,
      disarmAllDeleteLinkedTaskButtonsDispatch,
      disarmAllDeleteLinkedTaskButtonsAndArmOneDispatch,
      setClearLinkedTasksListDispatch
    }
  )(TaskLinks)
);
