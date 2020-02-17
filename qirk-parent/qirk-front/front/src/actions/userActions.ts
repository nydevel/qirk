import actions from "../utils/constants";
import constants from "../utils/constants";
import axios from "./../utils/axios";
import { responseIsStatusOk } from "../utils/variousUtils";
import endpoints from "../utils/endpoints";

export const setUser = (user: any) => ({
  type: actions.SET_USER,
  user
});

export const setUserDispatch = (user: any) => (dispatch: any) => {
  dispatch(setUser(user));
};

export const fetchUserProjectManagerProjects = () => async (dispatch: any) => {
  try {
    dispatch(setProjectManagerFetchRequestStatus(constants.WAITING));

    const response = await axios.get(endpoints.GET_PROJECT_LIST_BY_USER, {
      params: {
        managed: true
      }
    });

    if (responseIsStatusOk(response) && response.data.data) {
      dispatch(setProjectManagerProjects(response.data.data));
      dispatch(setProjectManagerFetchRequestStatus(constants.SUCCESS));
    } else {
      dispatch(setProjectManagerFetchRequestStatus(constants.FAILED));
    }
  } catch {
    dispatch(setProjectManagerFetchRequestStatus(constants.FAILED));
  }
};

const setProjectManagerProjects = (user: any) => ({
  payload: user,
  type: constants.SET_USER_PROJECT_MANAGER_PROJECTS
});

const setProjectManagerFetchRequestStatus = (status: any) => ({
  payload: status,
  type: constants.SET_USER_PROJECT_MANAGER_FETCH_REQUEST_STATUS
});
