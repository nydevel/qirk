import axios from "../utils/axios";
import endpoints from "../utils/endpoints";
import { toast } from "react-toastify";
import { t } from "i18next";
import actions from "../utils/constants";
import constants from "../utils/constants";
import { langToOption, responseIsStatusOk } from "../utils/variousUtils";

export const setRequestStatus = requestStatus => {
  return {
    type: actions.SET_COMMON_REQUEST_STATUS,
    requestStatus
  };
};

export const fetchLanguages = () => async dispatch => {
  try {
    dispatch(setFetchLanguagesStatus(constants.WAITING));
    const response = await axios.get(endpoints.LANGUAGES_LIST);
    if (
      responseIsStatusOk(response) &&
      response.data.data &&
      response.data.data.length > 0
    ) {
      dispatch(
        setLanguages(response.data.data.map(lang => langToOption(lang)))
      );
      dispatch(setFetchLanguagesStatus(constants.SUCCESS));
    } else {
      dispatch(setFetchLanguagesStatus(constants.FAILED));
    }
  } catch {
    dispatch(setFetchLanguagesStatus(constants.FAILED));
    toast.error(t("Errors.LanguagesList"));
  }
};

export const setFavoriteIdOrder = (order, putData) => async dispatch => {
  dispatch(setFavoriteIdOrderRaw(order));
  try {
    await axios.put(endpoints.PUT_MOVE_FAVORITES, putData);
  } catch {
    toast.error(t("Errors.Error"));
  }
};

export const fetchFavorites = () => async dispatch => {
  try {
    const response = await axios.get(endpoints.GET_FAVORITES);
    if (responseIsStatusOk(response)) {
      dispatch(setFavorites(response.data.data));
    }
  } catch (error) {
    // nothing ..?
  }
};

export const removeFavorite = favId => async (dispatch, getState) => {
  try {
    dispatch(setRequestStatus(actions.WAITING));

    const response = await axios.delete(endpoints.DELETE_FAVORITE, {
      data: { id: favId }
    });

    if (responseIsStatusOk(response)) {
      dispatch(removeFavoriteRaw(favId));
    }
  } catch {
    dispatch(setRequestStatus(actions.FAILED));
  } finally {
    dispatch(setRequestStatus(actions.NOT_REQUESTED));
  }
};

export const addProjectToFavorites = projectId => async dispatch => {
  try {
    dispatch(setRequestStatus(actions.WAITING));

    const response = await axios.post(endpoints.POST_ADD_TO_FAVORITES, {
      project: projectId
    });

    if (responseIsStatusOk(response)) {
      const addedFav = response.data.data[0];
      dispatch(addFavorite(addedFav));
    }
  } catch {
    dispatch(setRequestStatus(actions.FAILED));
  } finally {
    dispatch(setRequestStatus(actions.NOT_REQUESTED));
  }
};

export const setFavorites = listOfFavorites => {
  return {
    type: actions.SET_FAVORITE_LIST,
    payload: listOfFavorites
  };
};

export const setFavoriteIdOrderRaw = idOrder => {
  return {
    type: actions.SET_FAVORITE_ID_ORDER,
    payload: idOrder
  };
};

export const removeFavoriteRaw = favorite => {
  return {
    type: actions.REMOVE_FAVORITE_FROM_LIST,
    payload: favorite
  };
};

export const addFavorite = favorite => {
  return {
    type: actions.ADD_FAVORITE_TO_LIST,
    payload: favorite
  };
};

export const setLanguages = languages => {
  return {
    type: actions.SET_LANGUAGES,
    languages
  };
};

const setFetchLanguagesStatus = status => ({
  payload: status,
  type: actions.SET_FETCH_LANGUAGES_STATUS
});
