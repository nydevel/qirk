import constants from "../utils/constants";

const INITIAL_STATE = {
  cardlessTasksOrder: [],
  filteredCardlessTasksOrder: [],
  searchCardlessFor: "",
  loadingCardlessTasks: false,
  roadsById: {},
  cardsById: {},
  tasksById: {},
  roadsOrder: [],
  roadsByIdFetchStatus: constants.NOT_REQUESTED,
  creating: false
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case constants.ROADMAP_SET_SEARCH_CARDLESS_FOR:
      return { ...state, searchCardlessFor: action.payload };

    case constants.ROADMAP_SET_FILTERED_CARDLESS_TASKS_ORDER:
      return { ...state, filteredCardlessTasksOrder: action.payload };

    case constants.ROADMAP_UPDATE_TASK:
      return {
        ...state,
        tasksById: {
          ...state.tasksById,
          [action.payload.id]: {
            ...state.tasksById[action.payload.id],
            ...action.payload
          }
        }
      };

    case constants.ROADMAP_SET_CARD_TASKS_ORDER:
      return {
        ...state,
        cardsById: {
          ...state.cardsById,
          [action.payload.cardId]: {
            ...state.cardsById[action.payload.cardId],
            tasksOrder: action.payload.tasksOrder
          }
        }
      };

    case constants.ROADMAP_SET_CARDLESS_TASKS_ORDER:
      return { ...state, cardlessTasksOrder: action.payload };

    case constants.ROADMAP_ADD_TASKS_BY_ID_FROM_ARRAY:
      return {
        ...state,
        tasksById: {
          ...state.tasksById,
          ...action.payload.reduce((acc, curr) => {
            acc[curr.id] = curr;
            return acc;
          }, {})
        }
      };

    case constants.ROADMAP_SET_LOADING_CARDLESS_TASKS:
      return { ...state, loadingCardlessTasks: action.payload };

    case constants.ROADMAP_SET_ROAD_CARDS_ORDER:
      return {
        ...state,
        roadsById: {
          ...state.roadsById,
          [action.payload.roadId]: {
            ...state.roadsById[action.payload.roadId],
            cardsOrder: action.payload.cardsOrder
          }
        }
      };

    case constants.ROADMAP_ADD_CARDS_BY_ID:
      return {
        ...state,
        cardsById: { ...state.cardsById, ...action.payload }
      };

    case constants.ROADMAP_SET_CARDS_BY_ID:
      return {
        ...state,
        cardsById: { ...action.payload }
      };

    case constants.ROADMAP_DELETE_CARD:
      return {
        ...state,
        roadsById: {
          ...state.roadsById,
          [action.payload.roadId]: {
            ...state.roadsById[action.payload.roadId],
            cardsOrder: state.roadsById[
              action.payload.roadId
            ].cardsOrder.filter(c => c !== action.payload.cardId)
          }
        },
        cardsById: { ...state.cardsById, [action.payload.cardId]: {} }
      };

    case constants.ROADMAP_UPDATE_CARD:
      return {
        ...state,
        cardsById: {
          ...state.cardsById,
          [action.payload.id]: {
            ...state.cardsById[action.payload.id],
            ...action.payload
          }
        }
      };

    case constants.ROADMAP_ADD_CARD_TO_ROAD:
      return {
        ...state,
        cardsById: {
          ...state.cardsById,
          [action.payload.card.id]: action.payload.card
        },
        roadsById: {
          ...state.roadsById,
          [action.payload.roadId]: {
            ...state.roadsById[action.payload.roadId],
            cardsOrder: [
              action.payload.card.id,
              ...(state.roadsById[action.payload.roadId].cardsOrder || [])
            ]
          }
        }
      };

    case constants.ROADMAP_SET_ORDER:
      return { ...state, roadsOrder: action.payload };

    case constants.ROADMAP_SET_ROAD_LIST:
      return {
        ...state,
        roadsById:
          action.payload && action.payload.length > 0
            ? action.payload.reduce((acc, curr) => {
                acc[curr.id] = curr;
                return acc;
              }, {})
            : {},
        roadsOrder: action.payload.map(r => r.id)
      };

    case constants.ROADMAP_ADD_ROAD_ITEM:
      return {
        ...state,
        roadsById: {
          ...state.roadsById,
          [action.payload.id]: { ...action.payload }
        },
        roadsOrder: [...state.roadsOrder, action.payload.id]
      };

    case constants.ROADMAP_UPDATE_ROAD_ITEM:
      return {
        ...state,
        roadsById: {
          ...state.roadsById,
          [action.payload.id]: {
            ...state.roadsById[action.payload.id],
            ...action.payload
          }
        }
      };

    case constants.ROADMAP_DELETE_ROAD_ITEM:
      return {
        ...state,
        roadsOrder: state.roadsOrder.filter(r => r !== action.payload),
        roadsById: { ...state.roadsById, [action.payload]: {} }
      };

    case constants.ROADMAP_SET_ROAD_LIST_FETCH_STATUS:
      return {
        ...state,
        roadsByIdFetchStatus: action.payload
      };

    case constants.ROADMAP_SET_CREATING:
      return {
        ...state,
        creating: action.payload
      };

    default:
      return state;
  }
};
