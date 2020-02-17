import constants from "../utils/constants";

export const changeDashboardSizeDispatch = () => (dispatch: any) => {
  dispatch(changeDashboardSize());
};

export const setLinkedTasksListDeletingItemDispatch = (task: any) => (
  dispatch: any
) => {
  dispatch(setLinkedTasksListDeletingItem(task));
};

export const removeLinkedTasksListDeletingItemDispatch = (task: any) => (
  dispatch: any
) => {
  dispatch(removeLinkedTasksListDeletingItem(task));
};

export const setClearAttachmentsDispatch = (isClearAttachments: any) => (
  dispatch: any
) => {
  dispatch(setClearAttachments(isClearAttachments));
};

export const setClearLinkedTasksListDispatch = (
  isClearLinkedTasksList: any
) => (dispatch: any) => {
  dispatch(setClearLinkedTasksList(isClearLinkedTasksList));
};

const changeDashboardSize = () => ({
  type: constants.CHANGE_DASHBOARD_SIZE
});

const setLinkedTasksListDeletingItem = (task: any) => ({
  payload: task.id,
  type: constants.SET_LINKED_TASKS_LIST_DELETING_ITEM
});

const removeLinkedTasksListDeletingItem = (task: any) => ({
  payload: task.id,
  type: constants.REMOVE_LINKED_TASKS_LIST_DELETING_ITEM
});

const setClearAttachments = (isClearAttachments: any) => ({
  payload: isClearAttachments,
  type: constants.SET_CLEAR_ATTACHMENTS
});

const setClearLinkedTasksList = (isClearLinkedTasksList: any) => ({
  payload: isClearLinkedTasksList,
  type: constants.SET_CLEAR_LINKED_TASKS_LIST
});
