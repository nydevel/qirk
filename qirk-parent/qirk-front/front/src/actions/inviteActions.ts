import axios from "./../utils/axios";
import endpoints from "../utils/endpoints";
import constants from "../utils/constants";
import {
  responseIsStatusOk,
  errorIsAlreadyExists
} from "../utils/variousUtils";
import { setInviteToProjectOptionInviteByProjectId } from "./userSearchActions";
import snackbarActions from "./snackbarActions";

export const inviteUserToProject = (
  userId: any,
  projectId: any,
  text = ""
) => async (dispatch: any) => {
  try {
    dispatch(setInviteUserToProjectRequestStatus(constants.WAITING));
    const response = await axios.post(endpoints.POST_PROJECT_INVITE, {
      project: parseInt(projectId),
      user: parseInt(userId),
      text
    });
    if (
      responseIsStatusOk(response) &&
      response.data.data &&
      response.data.data.length > 0 &&
      response.data.data[0].status &&
      response.data.data[0].id
    ) {
      dispatch(setInviteUserToProjectRequestStatus(constants.SUCCESS));
      dispatch(
        setInviteToProjectOptionInviteByProjectId(projectId, {
          id: response.data.data[0].id,
          status: response.data.data[0].status
        })
      );
      dispatch(snackbarActions.success("invite_sent"));
    } else {
      dispatch(setInviteUserToProjectRequestStatus(constants.FAILED));

      dispatch(snackbarActions.error("Errors.Error"));
    }
  } catch (e) {
    if (errorIsAlreadyExists(e)) {
      dispatch(snackbarActions.error("this_user_is_already_invited_to_this_project"));
    } else {
      dispatch(snackbarActions.error("Errors.Error"));

    }
    dispatch(setInviteUserToProjectRequestStatus(constants.FAILED));
  }
};

const setInviteUserToProjectRequestStatus = (status: any) => ({
  payload: status,
  type: constants.SET_INVITE_USER_TO_PROJECT_REQUEST_STATUS
});
