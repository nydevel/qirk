import constants from "../utils/constants";

const INITIAL_STATE = {
  list: [],
  fetchRequestStatus: constants.NOT_REQUESTED
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case constants.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };

    case constants.DISARM_ALL_DELETE_ATTACHMENT_BUTTONS:
      return {
        ...state,
        list: state.list.map(att => ({ ...att, deleteBtnArmed: false }))
      };

    case constants.DISARM_ALL_DELETE_ATTACHMENT_BUTTONS_AND_ARM_ONE:
      return {
        ...state,
        list: state.list.map(att => {
          const isThisTheOne =
            (action.payload.id && att.id && att.id === action.payload.id) ||
            (action.payload.uuid &&
              att.uuid &&
              att.uuid === action.payload.uuid);

          return { ...att, deleteBtnArmed: isThisTheOne };
        })
      };

    case constants.SET_ATTACHMENTS_FETCH_REQUEST_STATUS:
      return { ...state, fetchRequestStatus: action.payload };

    case constants.SET_ATTACHMENTS_LIST:
      return { ...state, list: action.payload };

    case constants.DROP_ATTACHMENTS_LIST:
      return { ...state, list: [] };

    case constants.ADD_ATTACHMENTS:
      return { ...state, list: [...state.list, ...action.payload] };

    case constants.UPDATE_ATTACHMENT_BY_UUID:
      if (action.payload && action.payload.uuid && action.payload.attachment) {
        return {
          ...state,
          list: state.list.map(item =>
            item.uuid === action.payload.uuid ? action.payload.attachment : item
          )
        };
      }
      return { ...state };

    case constants.UPDATE_ATTACHMENT:
      if (action && action.payload && action.payload.id) {
        return {
          ...state,
          list: state.list.map(item =>
            item.id === action.payload.id ? action.payload : item
          )
        };
      } else if (action && action.payload && action.payload.uuid) {
        return {
          ...state,
          list: state.list.map(item =>
            item.uuid === action.payload.uuid ? action.payload : item
          )
        };
      }
      return { ...state };

    case constants.DELETE_ATTACHMENT:
      if (action && action.payload && action.payload.id) {
        return {
          ...state,
          list: state.list.filter(item => item.id !== action.payload.id)
        };
      } else if (action && action.payload && action.payload.uuid) {
        return {
          ...state,
          list: state.list.filter(item => item.uuid !== action.payload.uuid)
        };
      } else {
        return { ...state };
      }

    default:
      return state;
  }
};
