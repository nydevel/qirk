import constants from "../utils/constants";

export const changeDashboardSizeDispatch = () => dispatch => {
  dispatch(changeDashboardSize());
};

export const setLinkedTasksListDeletingItemDispatch = task => dispatch => {
  dispatch(setLinkedTasksListDeletingItem(task));
};

export const removeLinkedTasksListDeletingItemDispatch = task => dispatch => {
  dispatch(removeLinkedTasksListDeletingItem(task));
};

export const setClearAttachmentsDispatch = isClearAttachments => dispatch => {
  dispatch(setClearAttachments(isClearAttachments));
};

export const setClearLinkedTasksListDispatch = isClearLinkedTasksList => dispatch => {
  dispatch(setClearLinkedTasksList(isClearLinkedTasksList));
};

const changeDashboardSize = () => ({
  type: constants.CHANGE_DASHBOARD_SIZE
});

const setLinkedTasksListDeletingItem = task => ({
  payload: task.id,
  type: constants.SET_LINKED_TASKS_LIST_DELETING_ITEM
});

const removeLinkedTasksListDeletingItem = task => ({
  payload: task.id,
  type: constants.REMOVE_LINKED_TASKS_LIST_DELETING_ITEM
});

const setClearAttachments = isClearAttachments => ({
  payload: isClearAttachments,
  type: constants.SET_CLEAR_ATTACHMENTS
});

const setClearLinkedTasksList = isClearLinkedTasksList => ({
  payload: isClearLinkedTasksList,
  type: constants.SET_CLEAR_LINKED_TASKS_LIST
});
