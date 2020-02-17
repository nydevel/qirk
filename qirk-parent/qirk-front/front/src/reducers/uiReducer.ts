import constants from "../utils/constants";
import { takeItemFromLocalStorageSafe } from "../utils/variousUtils";
import { Action } from "../utils/types/Action";
import { Snackbar } from "../utils/types/Snackbar";
import { SnackbarType } from "../utils/enums/SnackbarType";

export type UiReducerState = {
  snackbar: Snackbar;
  dashboardCollapsed: Boolean;
  linkedTasksListDeleting: number[];
};

const INITIAL_STATE: UiReducerState = {
  snackbar: {
    message: null,
    type: SnackbarType.INFO
  },
  dashboardCollapsed: takeItemFromLocalStorageSafe(
    "dashboard_collapsed",
    false
  ),
  linkedTasksListDeleting: []
};

export default (
  state: UiReducerState = INITIAL_STATE,
  action: Action
): UiReducerState => {
  switch (action.type) {
    case constants.SET_SNACKBAR:
      return { ...state, snackbar: action.payload };

    case constants.CHANGE_DASHBOARD_SIZE:
      return { ...state, dashboardCollapsed: !state.dashboardCollapsed };

    case constants.SET_LINKED_TASKS_LIST_DELETING_ITEM:
      return {
        ...state,
        linkedTasksListDeleting: [
          ...state.linkedTasksListDeleting,
          action.payload
        ]
      };
    case constants.REMOVE_LINKED_TASKS_LIST_DELETING_ITEM:
      return {
        ...state,
        linkedTasksListDeleting: state.linkedTasksListDeleting.filter(
          item => item !== action.payload
        )
      };
    default:
      return state;
  }
};
