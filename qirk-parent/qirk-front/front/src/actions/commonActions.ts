import axios from "../utils/axios";
import endpoints from "../utils/endpoints";
import actions from "../utils/constants";
import { responseIsStatusOk } from "../utils/variousUtils";
import { error } from "./snackbarActions";

export const setFavoriteIdOrder = (order: any, putData: any) => async (
  dispatch: any
) => {
  dispatch(setFavoriteIdOrderRaw(order));
  try {
    await axios.put(endpoints.PUT_MOVE_FAVORITES, putData);
  } catch {
    dispatch(error("Errors.Error"));
  }
};

export const fetchFavorites = () => async (dispatch: any) => {
  try {
    const response = await axios.get(endpoints.GET_FAVORITES);
    if (responseIsStatusOk(response)) {
      dispatch(setFavorites(response.data.data));
    }
  } catch (е) {
    console.error(е);
  }
};

export const removeFavorite = (favId: any) => async (
  dispatch: any,
  getState: any
) => {
  try {
    const response = await axios.delete(endpoints.DELETE_FAVORITE, {
      data: { id: favId }
    });

    if (responseIsStatusOk(response)) {
      dispatch(removeFavoriteRaw(favId));
    }
  } catch (е) {
    console.error(е);
  }
};

export const addProjectToFavorites = (projectId: any) => async (
  dispatch: any
) => {
  try {
    const response = await axios.post(endpoints.POST_ADD_TO_FAVORITES, {
      project: projectId
    });

    if (responseIsStatusOk(response)) {
      const addedFav = response.data.data[0];
      dispatch(addFavorite(addedFav));
    }
  } catch (е) {
    console.error(е);
  }
};

export const setFavorites = (listOfFavorites: any) => {
  return {
    type: actions.SET_FAVORITE_LIST,
    payload: listOfFavorites
  };
};

export const setFavoriteIdOrderRaw = (idOrder: any) => {
  return {
    type: actions.SET_FAVORITE_ID_ORDER,
    payload: idOrder
  };
};

export const removeFavoriteRaw = (favorite: any) => {
  return {
    type: actions.REMOVE_FAVORITE_FROM_LIST,
    payload: favorite
  };
};

export const addFavorite = (favorite: any) => {
  return {
    type: actions.ADD_FAVORITE_TO_LIST,
    payload: favorite
  };
};
