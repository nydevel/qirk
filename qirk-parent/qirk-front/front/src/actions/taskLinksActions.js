import constants from "../utils/constants";

export const setSelectedLinkedTaskDispatch = task => async dispatch => {
  dispatch(setSelectedLinkedTask(task));
};

export const removeSelectedLinkedTaskDispatch = task => async dispatch => {
  dispatch(removeSelectedLinkedTask(task));
};

export const disarmAllDeleteLinkedTaskButtonsDispatch = () => dispatch => {
  dispatch(disarmAllDeleteLinkedTaskButtons());
};

export const disarmAllDeleteLinkedTaskButtonsAndArmOneDispatch = linkedTask => dispatch => {
  dispatch(disarmAllDeleteLinkedTaskButtonsAndArmOne(linkedTask));
};

export const setSelectedLinkedTaskListDispatch = taskList => dispatch => {
  dispatch(setSelectedLinkedTaskList(taskList || []));
};

const disarmAllDeleteLinkedTaskButtonsAndArmOne = linkedTask => ({
  payload: linkedTask,
  type: constants.DISARM_ALL_DELETE_LINKED_TASK_BUTTONS_AND_ARM_ONE
});

const disarmAllDeleteLinkedTaskButtons = () => ({
  type: constants.DISARM_ALL_DELETE_LINKED_TASK_BUTTONS
});

const setSelectedLinkedTask = task => ({
  payload: task,
  type: constants.SET_SELECTED_LINKED_TASK
});

const removeSelectedLinkedTask = task => ({
  payload: task.id,
  type: constants.REMOVE_SELECTED_LINKED_TASK
});

const setSelectedLinkedTaskList = taskList => ({
  payload: taskList,
  type: constants.SET_SELECTED_LINKED_TASK_LIST
});
