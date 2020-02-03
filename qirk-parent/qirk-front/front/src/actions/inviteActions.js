import { toast } from "react-toastify";
import { t } from "i18next";
import axios from "./../utils/axios";
import endpoints from "../utils/endpoints";
import constants from "../utils/constants";
import {
  responseIsStatusOk,
  errorIsAlreadyExists
} from "../utils/variousUtils";
import { setInviteToProjectOptionInviteByProjectId } from "./userSearchActions";

export const inviteUserToProject = (
  userId,
  projectId,
  text = ""
) => async dispatch => {
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
      toast.success(t("invite_sent"));
    } else {
      dispatch(setInviteUserToProjectRequestStatus(constants.FAILED));
      toast.error(t("Errors.Error"));
    }
  } catch (e) {
    if (errorIsAlreadyExists(e)) {
      toast.error(t("this_user_is_already_invited_to_this_project"));
    } else {
      toast.error(t("Errors.Error"));
    }
    dispatch(setInviteUserToProjectRequestStatus(constants.FAILED));
  }
};

const setInviteUserToProjectRequestStatus = status => ({
  payload: status,
  type: constants.SET_INVITE_USER_TO_PROJECT_REQUEST_STATUS
});
