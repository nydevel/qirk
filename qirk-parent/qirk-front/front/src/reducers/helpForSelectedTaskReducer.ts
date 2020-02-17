import constants from "../utils/constants";
import { Action } from "../utils/types/Action";

export type HelpForSelectedTaskReducerState = {
  linkedList: any[]; // todo
  clearAttachments: boolean;
  clearLinkedTasksList: boolean;
};

const INITIAL_STATE: HelpForSelectedTaskReducerState = {
  linkedList: [],
  clearAttachments: false,
  clearLinkedTasksList: false
};

export default (
  state: HelpForSelectedTaskReducerState = INITIAL_STATE,
  action: Action
): HelpForSelectedTaskReducerState => {
  switch (action.type) {
    case constants.SET_SELECTED_LINKED_TASK_LIST:
      return {
        ...state,
        linkedList: action.payload
      };
    case constants.SET_SELECTED_LINKED_TASK:
      return {
        ...state,
        linkedList: [...state.linkedList, action.payload]
      };
    case constants.REMOVE_SELECTED_LINKED_TASK:
      return {
        ...state,
        linkedList: state.linkedList.filter(item => item.id !== action.payload)
      };
    case constants.DISARM_ALL_DELETE_LINKED_TASK_BUTTONS:
      return {
        ...state,
        linkedList: state.linkedList.map(task => ({
          ...task,
          deleteBtnArmed: false
        }))
      };
    case constants.DISARM_ALL_DELETE_LINKED_TASK_BUTTONS_AND_ARM_ONE:
      return {
        ...state,
        linkedList: state.linkedList.map(task => {
          const isThisTheOne =
            action.payload.id && task.id && task.id === action.payload.id;
          return { ...task, deleteBtnArmed: isThisTheOne };
        })
      };
    case constants.SET_CLEAR_ATTACHMENTS:
      return {
        ...state,
        clearAttachments: action.payload
      };
    case constants.SET_CLEAR_LINKED_TASKS_LIST:
      return {
        ...state,
        clearLinkedTasksList: action.payload
      };
    default:
      return state;
  }
};
