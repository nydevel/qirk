import constants from "./../utils/constants";
import axios from "./../utils/axios";
import endpoints from "./../utils/endpoints";
import { responseIsStatusOk } from "../utils/variousUtils";
import { Task } from "../utils/types/Task";
import { Dispatch } from "react";
import { Action } from "../utils/types/Action";
import { Attachment } from "../utils/types/Attachment";
import { RequestStatus } from "../utils/enums/RequestStatus";

export const fetchTaskAttachments = (taskId: Task["id"]) => async (
  dispatch: Dispatch<Action>
) => {
  try {
    dispatch(setAttachmentsFetchRequestStatus(RequestStatus.WAITING));
    dispatch(dropAttachmentsList());

    const response = await axios.get(endpoints.TASK_ATTACHMENTS, {
      params: { task_id: taskId }
    });

    if (responseIsStatusOk(response) && response.data.data) {
      dispatch(setAttachmentsList(response.data.data));
      dispatch(setAttachmentsFetchRequestStatus(RequestStatus.SUCCESS));
    } else {
      dispatch(setAttachmentsFetchRequestStatus(RequestStatus.FAILED));
    }
  } catch {
    dispatch(setAttachmentsFetchRequestStatus(RequestStatus.FAILED));
  } finally {
    dispatch(setAttachmentsFetchRequestStatus(RequestStatus.NOT_REQUESTED));
  }
};

export const setAttachmentsListDispatch = (attachmentsList: Attachment[]) => (
  dispatch: Dispatch<Action>
) => {
  dispatch(setAttachmentsList(attachmentsList));
};

export const dropAttachmentsListDispatch = () => (
  dispatch: Dispatch<Action>
) => {
  dispatch(dropAttachmentsList());
};

export const addAttachmentsDispatch = (attachmentsList: Attachment[]) => (
  dispatch: Dispatch<Action>
) => {
  dispatch(addAttachments(attachmentsList));
};

export const updateAttachmentByUuidDispatch = (
  uuid: Attachment["uuid"],
  attachment: Attachment
) => (dispatch: Dispatch<Action>) => {
  dispatch(updateAttachmentByUuid(uuid, attachment));
};

export const updateAttachmentDispatch = (attachment: Attachment) => (
  dispatch: Dispatch<Action>
) => {
  dispatch(updateAttachment(attachment));
};

export const deleteAttachmentDispatch = (attachment: Attachment) => (
  dispatch: Dispatch<Action>
) => {
  dispatch(deleteAttachment(attachment));
};

export const disarmAllDeleteAttachmentButtonsDispatch = () => (
  dispatch: Dispatch<Action>
) => {
  dispatch(disarmAllDeleteAttachmentButtons());
};

export const disarmAllDeleteAttachmentButtonsAndArmOneDispatch = (
  attachment: Attachment
) => (dispatch: Dispatch<Action>) => {
  dispatch(disarmAllDeleteAttachmentButtonsAndArmOne(attachment));
};

const disarmAllDeleteAttachmentButtonsAndArmOne = (attachment: Attachment) => ({
  payload: attachment,
  type: constants.DISARM_ALL_DELETE_ATTACHMENT_BUTTONS_AND_ARM_ONE
});

const disarmAllDeleteAttachmentButtons = () => ({
  type: constants.DISARM_ALL_DELETE_ATTACHMENT_BUTTONS
});

const setAttachmentsList = (attachmentsList: Attachment[]) => ({
  payload: attachmentsList,
  type: constants.SET_ATTACHMENTS_LIST
});

const dropAttachmentsList = () => ({
  type: constants.DROP_ATTACHMENTS_LIST
});

const addAttachments = (attachmentsList: Attachment[]) => ({
  payload: attachmentsList,
  type: constants.ADD_ATTACHMENTS
});

const updateAttachmentByUuid = (
  uuid: Attachment["uuid"],
  attachment: Attachment
) => ({
  payload: {
    attachment,
    uuid
  },
  type: constants.UPDATE_ATTACHMENT_BY_UUID
});

const updateAttachment = (attachment: Attachment) => ({
  payload: attachment,
  type: constants.UPDATE_ATTACHMENT
});

const deleteAttachment = (attachment: Attachment) => ({
  payload: attachment,
  type: constants.DELETE_ATTACHMENT
});

const setAttachmentsFetchRequestStatus = (status: RequestStatus) => ({
  payload: status,
  type: constants.SET_ATTACHMENTS_FETCH_REQUEST_STATUS
});
