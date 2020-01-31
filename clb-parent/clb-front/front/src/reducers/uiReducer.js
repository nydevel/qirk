import constants from "../utils/constants";
import { takeItemFromLocalStorageSafe } from "../utils/variousUtils";

const INITIAL_STATE = {
  dashboardCollapsed: takeItemFromLocalStorageSafe(
    "dashboard_collapsed",
    false
  ),
  linkedTasksListDeleting: []
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case constants.CHANGE_DASHBOARD_SIZE:
      return state.dashboardCollapsed === false
        ? { ...state, dashboardCollapsed: true }
        : { ...state, dashboardCollapsed: false };
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
