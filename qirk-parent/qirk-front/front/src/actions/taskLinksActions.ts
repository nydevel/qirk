import constants from "../utils/constants";

export const setSelectedLinkedTaskDispatch = (task: any) => async (
  dispatch: any
) => {
  dispatch(setSelectedLinkedTask(task));
};

export const removeSelectedLinkedTaskDispatch = (task: any) => async (
  dispatch: any
) => {
  dispatch(removeSelectedLinkedTask(task));
};

export const disarmAllDeleteLinkedTaskButtonsDispatch = () => (
  dispatch: any
) => {
  dispatch(disarmAllDeleteLinkedTaskButtons());
};

export const disarmAllDeleteLinkedTaskButtonsAndArmOneDispatch = (
  linkedTask: any
) => (dispatch: any) => {
  dispatch(disarmAllDeleteLinkedTaskButtonsAndArmOne(linkedTask));
};

export const setSelectedLinkedTaskListDispatch = (taskList: any) => (
  dispatch: any
) => {
  dispatch(setSelectedLinkedTaskList(taskList || []));
};

const disarmAllDeleteLinkedTaskButtonsAndArmOne = (linkedTask: any) => ({
  payload: linkedTask,
  type: constants.DISARM_ALL_DELETE_LINKED_TASK_BUTTONS_AND_ARM_ONE
});

const disarmAllDeleteLinkedTaskButtons = () => ({
  type: constants.DISARM_ALL_DELETE_LINKED_TASK_BUTTONS
});

const setSelectedLinkedTask = (task: any) => ({
  payload: task,
  type: constants.SET_SELECTED_LINKED_TASK
});

const removeSelectedLinkedTask = (task: any) => ({
  payload: task.id,
  type: constants.REMOVE_SELECTED_LINKED_TASK
});

const setSelectedLinkedTaskList = (taskList: any) => ({
  payload: taskList,
  type: constants.SET_SELECTED_LINKED_TASK_LIST
});
