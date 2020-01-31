import constants from "./../utils/constants";
import axios from "./../utils/axios";
import endpoints from "./../utils/endpoints";
import { responseIsStatusOk } from "../utils/variousUtils";

export const fetchTaskAttachments = taskId => async dispatch => {
  try {
    dispatch(setAttachmentsFetchRequestStatus(constants.WAITING));
    dispatch(dropAttachmentsList());

    const response = await axios.get(
      endpoints.TASK_ATTACHMENTS.replace("{task_id}", taskId)
    );

    if (responseIsStatusOk(response) && response.data.data) {
      dispatch(setAttachmentsList(response.data.data));
      dispatch(setAttachmentsFetchRequestStatus(constants.SUCCESS));
    } else {
      dispatch(setAttachmentsFetchRequestStatus(constants.FAILED));
    }
  } catch {
    dispatch(setAttachmentsFetchRequestStatus(constants.FAILED));
  } finally {
    dispatch(setAttachmentsFetchRequestStatus(constants.NOT_REQUESTED));
  }
};

export const setAttachmentsListDispatch = attachmentsList => dispatch => {
  dispatch(setAttachmentsList(attachmentsList));
};

export const dropAttachmentsListDispatch = () => dispatch => {
  dispatch(dropAttachmentsList());
};

export const addAttachmentsDispatch = attachmentsList => dispatch => {
  dispatch(addAttachments(attachmentsList));
};

export const updateAttachmentByUuidDispatch = (
  uuid,
  attachment
) => dispatch => {
  dispatch(updateAttachmentByUuid(uuid, attachment));
};

export const updateAttachmentDispatch = attachment => dispatch => {
  dispatch(updateAttachment(attachment));
};

export const deleteAttachmentDispatch = attachment => dispatch => {
  dispatch(deleteAttachment(attachment));
};

export const disarmAllDeleteAttachmentButtonsDispatch = () => dispatch => {
  dispatch(disarmAllDeleteAttachmentButtons());
};

export const disarmAllDeleteAttachmentButtonsAndArmOneDispatch = attachment => dispatch => {
  dispatch(disarmAllDeleteAttachmentButtonsAndArmOne(attachment));
};

const disarmAllDeleteAttachmentButtonsAndArmOne = attachment => ({
  payload: attachment,
  type: constants.DISARM_ALL_DELETE_ATTACHMENT_BUTTONS_AND_ARM_ONE
});

const disarmAllDeleteAttachmentButtons = () => ({
  type: constants.DISARM_ALL_DELETE_ATTACHMENT_BUTTONS
});

const setAttachmentsList = attachmentsList => ({
  payload: attachmentsList,
  type: constants.SET_ATTACHMENTS_LIST
});

const dropAttachmentsList = () => ({
  type: constants.DROP_ATTACHMENTS_LIST
});

const addAttachments = attachmentsList => ({
  payload: attachmentsList,
  type: constants.ADD_ATTACHMENTS
});

const updateAttachmentByUuid = (uuid, attachment) => ({
  payload: {
    attachment,
    uuid
  },
  type: constants.UPDATE_ATTACHMENT_BY_UUID
});

const updateAttachment = attachment => ({
  payload: attachment,
  type: constants.UPDATE_ATTACHMENT
});

const deleteAttachment = attachment => ({
  payload: attachment,
  type: constants.DELETE_ATTACHMENT
});

const setAttachmentsFetchRequestStatus = status => ({
  payload: status,
  type: constants.SET_ATTACHMENTS_FETCH_REQUEST_STATUS
});
