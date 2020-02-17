import constants from "../utils/constants";
import endpoints from "../utils/endpoints";
import axios from "../utils/axios";
import { responseIsStatusOk, uiTaskSummary } from "../utils/variousUtils";

const setRoadListFetchStatus = (status: any) => ({
  type: constants.ROADMAP_SET_ROAD_LIST_FETCH_STATUS,
  payload: status
});
const setRoadList = (list: any) => ({
  type: constants.ROADMAP_SET_ROAD_LIST,
  payload: list
});
const addRoadItem = (item: any) => ({
  type: constants.ROADMAP_ADD_ROAD_ITEM,
  payload: item
});
const updateRoadItem = (item: any) => ({
  type: constants.ROADMAP_UPDATE_ROAD_ITEM,
  payload: item
});
const deleteRoadItem = (id: any) => ({
  type: constants.ROADMAP_DELETE_ROAD_ITEM,
  payload: id
});
const setRoadCreating = (creating: any) => ({
  type: constants.ROADMAP_SET_CREATING,
  payload: creating
});
const _setRoadOrder = (order: any) => ({
  type: constants.ROADMAP_SET_ORDER,
  payload: order
});
const addCard = (roadId: any, card: any) => ({
  type: constants.ROADMAP_ADD_CARD_TO_ROAD,
  payload: { roadId, card }
});
const _updateCard = (card: any) => ({
  type: constants.ROADMAP_UPDATE_CARD,
  payload: card
});
const _deleteCard = (roadId: any, cardId: any) => ({
  type: constants.ROADMAP_DELETE_CARD,
  payload: { roadId, cardId }
});
const _setCardsById = (cardsById: any) => ({
  type: constants.ROADMAP_SET_CARDS_BY_ID,
  payload: cardsById
});
const _addCardsById = (cardsById: any) => ({
  type: constants.ROADMAP_ADD_CARDS_BY_ID,
  payload: cardsById
});
const _setRoadCardsOrder = (roadId: any, cardsOrder: any) => ({
  payload: { roadId, cardsOrder },
  type: constants.ROADMAP_SET_ROAD_CARDS_ORDER
});
const _setCardlessTasksOrder = (order: any) => ({
  payload: order,
  type: constants.ROADMAP_SET_CARDLESS_TASKS_ORDER
});
const _addTasksByIdFromArray = (arrOfTasks: any) => ({
  payload: arrOfTasks,
  type: constants.ROADMAP_ADD_TASKS_BY_ID_FROM_ARRAY
});
const _setLoadingCardlessTasks = (isLoading: any) => ({
  payload: isLoading,
  type: constants.ROADMAP_SET_LOADING_CARDLESS_TASKS
});
const _setCardTasksOrder = (cardId: any, tasksOrder: any) => ({
  payload: { tasksOrder, cardId },
  type: constants.ROADMAP_SET_CARD_TASKS_ORDER
});
const _updateTask = (task: any) => ({
  type: constants.ROADMAP_UPDATE_TASK,
  payload: task
});

export const updateTaskLocally = (task: any) => (dispatch: any) => {
  dispatch(_updateTask(task));
};

export const setCardTasksOrder = (cardId: any, tasksOrder: any) => (
  dispatch: any
) => {
  dispatch(_setCardTasksOrder(cardId, tasksOrder));
};

export const getCardlessTasks = (project_ui_id: any) => async (
  dispatch: any
) => {
  try {
    dispatch(_setLoadingCardlessTasks(true));
    const response = await axios.get(endpoints.GET_TASK_LIST_CARDLESS, {
      params: { project_ui_id }
    });
    if (responseIsStatusOk(response)) {
      const cardlessTasksArr =
        (response && response.data && response.data.data) || [];
      dispatch(
        _addTasksByIdFromArray(
          cardlessTasksArr.map((t: any) => addUiSummaryFieldToTask(t))
        )
      );
      dispatch(_setCardlessTasksOrder(cardlessTasksArr.map((t: any) => t.id)));
    }
  } finally {
    dispatch(_setLoadingCardlessTasks(false));
  }
};

export const setCardlessTasksOrder = (order: any) => (dispatch: any) => {
  dispatch(_setCardlessTasksOrder(order));
};

export const postRoad = (road: any) => async (dispatch: any) => {
  try {
    dispatch(setRoadCreating(true));

    const response = await axios.post(endpoints.POST_ROAD, { ...road });

    if (responseIsStatusOk(response)) {
      const road =
        response &&
        response.data &&
        response.data.data &&
        response.data.data.length > 0 &&
        response.data.data[0];

      dispatch(addRoadItem(road));
    }
  } catch (e) {
    console.error(e);
  } finally {
    dispatch(setRoadCreating(false));
  }
};

export const updateRoadCardsOrder = (
  roadId: any,
  newOrder: any,
  putData: any
) => async (dispatch: any) => {
  try {
    dispatch(_setRoadCardsOrder(roadId, newOrder));
    await axios.put(endpoints.PUT_TASK_CARD_MOVE, { ...putData });
  } catch (e) {
    console.error(e);
  }
};

export const setRoadCardsOrder = (roadId: any, newOrder: any) => (
  dispatch: any
) => {
  dispatch(_setRoadCardsOrder(roadId, newOrder));
};

export const updateRoadsOrder = (newOrder: any, putData: any) => async (
  dispatch: any
) => {
  try {
    dispatch(_setRoadOrder(newOrder));
    await axios.put(endpoints.PUT_ROAD_MOVE, { ...putData });
  } catch (e) {
    console.error(e);
  }
};

export const postCard = (card: any) => async (dispatch: any) => {
  const roadId = card.road;

  try {
    const response = await axios.post(endpoints.POST_TASK_CARD, { ...card });

    if (responseIsStatusOk(response)) {
      const returnedCard =
        response &&
        response.data &&
        response.data.data &&
        response.data.data.length > 0 &&
        response.data.data[0];

      dispatch(addCard(roadId, { ...returnedCard, tasksOrder: [] }));
    }
  } catch (e) {
    console.error(e);
  }
};

export const deleteCard = (roadId: any, cardId: any) => async (
  dispatch: any,
  getState: any
) => {
  try {
    dispatch(_updateCard({ id: cardId, deleting: true }));

    const response = await axios.delete(endpoints.DELETE_TASK_CARD, {
      data: { id: cardId }
    });

    if (responseIsStatusOk(response)) {
      const tasksOrder = getState().roadmap.cardsById[cardId].tasksOrder;
      const cardlessTasksOrder = getState().roadmap.cardlessTasksOrder;

      dispatch(_setCardlessTasksOrder(cardlessTasksOrder.concat(tasksOrder)));
      dispatch(_deleteCard(roadId, cardId));
    }
  } catch (e) {
    console.error(e);
  } finally {
    dispatch(_updateCard({ id: cardId, deleting: false }));
  }
};

export const updateCard = (card: any) => async (dispatch: any) => {
  try {
    dispatch(_updateCard({ id: card.id, updating: true }));

    const response = await axios.put(endpoints.PUT_TASK_CARD, { ...card });

    if (responseIsStatusOk(response)) {
      const returnedCard =
        response &&
        response.data &&
        response.data.data &&
        response.data.data.length > 0 &&
        response.data.data[0];

      dispatch(_updateCard(returnedCard));
    }
  } catch (e) {
    console.error(e);
  } finally {
    dispatch(_updateCard({ id: card.id, updating: false }));
  }
};

export const putRoad = (road: any) => async (dispatch: any) => {
  if (!road) {
    return console.error("check putRoad() parameters");
  }

  const { id } = road;

  try {
    dispatch(updateRoadItem({ id, updating: true }));

    const response = await axios.put(endpoints.PUT_ROAD, { ...road });

    if (responseIsStatusOk(response)) {
      const road =
        response &&
        response.data &&
        response.data.data &&
        response.data.data.length > 0 &&
        response.data.data[0];

      dispatch(updateRoadItem(road));
    }
  } catch (e) {
    console.error(e);
  } finally {
    dispatch(updateRoadItem({ id, updating: false }));
  }
};

export const deleteRoad = (roadId: any) => async (dispatch: any) => {
  try {
    dispatch(updateRoadItem({ id: roadId, deleting: true }));

    const response = await axios.delete(endpoints.DELETE_ROAD, {
      data: { id: roadId }
    });

    if (responseIsStatusOk(response)) {
      dispatch(deleteRoadItem(roadId));
    }
  } catch (e) {
    console.error(e);
  } finally {
    dispatch(updateRoadItem({ id: roadId, deleting: false }));
  }
};

const _setSearchCardlessFor = (searchString: any) => ({
  type: constants.ROADMAP_SET_SEARCH_CARDLESS_FOR,
  payload: searchString
});

export const setSearchCardlessFor = (searchString: any) => (dispatch: any) => {
  dispatch(_setSearchCardlessFor(searchString));
};

const _setFilteredCardlessTasksOrder = (order: any) => ({
  type: constants.ROADMAP_SET_FILTERED_CARDLESS_TASKS_ORDER,
  payload: order
});

export const setFilteredCardlessTasksOrder = (order: any) => (
  dispatch: any
) => {
  dispatch(_setFilteredCardlessTasksOrder(order));
};

const addUiSummaryFieldToTask = (task: any) => ({
  ...task,
  uiSummary: uiTaskSummary(task.number, task.summary)
});

export const getRoadList = (project_ui_id: any) => async (dispatch: any) => {
  try {
    dispatch(setRoadListFetchStatus(constants.WAITING));
    dispatch(setRoadList([]));
    dispatch(_setCardsById({}));

    const response = await axios.get(endpoints.GET_ROAD_LIST, {
      params: { project_ui_id }
    });

    if (responseIsStatusOk(response)) {
      const list = response.data && response.data.data;

      dispatch(
        setRoadList(
          list.map((r: any) => ({
            ...r,
            cardsOrder: (r.cards || []).map((c: any) => c.id)
          }))
        )
      );

      const cardsById: any = {};
      let allTasks: any = [];

      list.forEach((road: any) => {
        const { cards = [] } = road;

        cards.forEach((c: any) => {
          let { tasks = [] } = c;

          cardsById[c.id] = {
            ...c,
            tasksOrder: tasks.map((t: any) => t.id)
          };

          allTasks = allTasks.concat(
            tasks.map((task: any) => {
              const nTask = { ...task };

              if (!nTask.task_priority && nTask.priority) {
                nTask.task_priority = nTask.priority;
                delete nTask.priority;
              }

              if (!nTask.task_status && nTask.status) {
                nTask.task_status = nTask.status;
                delete nTask.status;
              }

              return nTask;
            })
          );
        });
      });

      dispatch(
        _addTasksByIdFromArray(
          allTasks.map((t: any) => addUiSummaryFieldToTask(t))
        )
      );
      dispatch(_addCardsById(cardsById));
      dispatch(setRoadListFetchStatus(constants.SUCCESS));
    }
  } catch (e) {
    console.error(e);
    dispatch(setRoadListFetchStatus(constants.FAILED));
  }
};
