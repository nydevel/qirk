import React, { useEffect, useCallback, useState, useRef } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { useDropzone } from "react-dropzone";
import { toast } from "react-toastify";
import { t } from "i18next";
import { v4 } from "uuid";
import classNames from "classnames";
import {
  fetchTaskAttachments,
  addAttachmentsDispatch,
  updateAttachmentByUuidDispatch,
  updateAttachmentDispatch,
  deleteAttachmentDispatch,
  dropAttachmentsListDispatch,
  disarmAllDeleteAttachmentButtonsDispatch,
  disarmAllDeleteAttachmentButtonsAndArmOneDispatch
} from "./../../actions/attachmentActions";
import Loading from "../Loading/Loading";
import axios from "./../../utils/axios";
import endpoints from "./../../utils/endpoints";
import ic_spinner from "./../../assets/icons/ic_spinner_dots.svg";
import constants from "../../utils/constants";
import "./Attachments.sass";
import {
  responseIsStatusOk,
  errorIsFileTooLarge,
  errorIsYandexCloudUploadFailed,
  saveItemToLocalStorageSafe,
  takeItemFromLocalStorageSafe
} from "../../utils/variousUtils";
import { Icon, IconButton, Button } from "@material-ui/core";
import useOutsideClickListener from "../../utils/hooks/useOutsideClickListener";
import { setClearAttachmentsDispatch } from "../../actions/uiActions";

const UPLOAD_ATTEMPTS_LIMIT = 5;

function Attachments({
  canManage,
  taskId,
  projectId,
  isLoading,
  hiddenLoading = false,
  canUpload,
  attachments,
  fetchTaskAttachments,
  addAttachmentsDispatch,
  updateAttachmentByUuidDispatch,
  updateAttachmentDispatch,
  deleteAttachmentDispatch,
  dropAttachmentsListDispatch,
  disarmAllDeleteAttachmentButtonsDispatch,
  disarmAllDeleteAttachmentButtonsAndArmOneDispatch,
  setClearAttachmentsDispatch,
  hasAttachmants,
  clearAttachments,
  isTaskCreate = false
}) {
  const deleteBtnRef = useRef();
  const [xHoveredOver, setXHoveredOver] = useState(null);
  const [_attachments, setAttachments] = useState(() => {
    if (!isTaskCreate) {
      return attachments;
    }
    const currentDateInSeconds = Date.now() / 1000;
    return takeItemFromLocalStorageSafe("attachments", []).filter(
      item =>
        item.created_at &&
        process.env.REACT_APP_ATTACHMENT_ACTIVE_TIME >
          currentDateInSeconds - item.created_at
    );
  });

  useOutsideClickListener(
    deleteBtnRef,
    isTaskCreate
      ? () => {
          const attachmentsFromLocalStorage = takeItemFromLocalStorageSafe(
            "attachments",
            []
          );
          setAttachments(
            attachmentsFromLocalStorage.map(att => ({
              ...att,
              deleteBtnArmed: false
            }))
          );
        }
      : disarmAllDeleteAttachmentButtonsDispatch,
    () => {
      return;
    }
  );

  useEffect(() => {
    if (taskId) {
      dropAttachmentsListDispatch();
      saveItemToLocalStorageSafe("last_task_id", taskId);
    }
  }, [taskId]);

  useEffect(() => {
    if (taskId) {
      fetchTaskAttachments(taskId);
    }
  }, [taskId]);

  useEffect(() => {
    if (projectId) {
      saveItemToLocalStorageSafe("project_id", projectId);
    }
  }, [projectId]);

  useEffect(() => {
    if (isTaskCreate) {
      if (clearAttachments) {
        setAttachments([]);
        setClearAttachmentsDispatch(false);
      }
      saveItemToLocalStorageSafe("attachments", _attachments);
    }
  }, [_attachments, clearAttachments]);

  const handleUploadedFile = async (file, uuid, attempt = 1) => {
    if (file.size > process.env.REACT_APP_MAX_ATTACHMANT_SIZE_BYTES) {
      deleteAttachmentDispatch({ uuid });
      return toast.error(`${file.name}: ${t("file_too_large_error_message")}`);
    }

    const formData = new FormData();

    formData.append("uuid", uuid);
    isTaskCreate
      ? formData.append(
          "project_id",
          takeItemFromLocalStorageSafe("project_id")
        )
      : formData.append(
          "task_id",
          takeItemFromLocalStorageSafe("last_task_id")
        );
    formData.append("file", file);

    try {
      console.log("[tryng to upload file] > file.name", file.name);
      const response = await axios.post(endpoints.FILE_UPLOAD, formData);
      if (
        responseIsStatusOk(response) &&
        response.data.meta &&
        response.data.meta.uuid &&
        response.data.data &&
        response.data.data.length > 0
      ) {
        if (isTaskCreate) {
          const currentAttachment = {
            uuid: response.data.data[0].uuid,
            filename: file.name,
            externalUuid: response.data.meta.uuid,
            created_at: Date.now() / 1000
          };
          const attachmentsFromLocalStorage = takeItemFromLocalStorageSafe(
            "attachments",
            []
          );
          setAttachments(
            attachmentsFromLocalStorage.map(item =>
              item.uuid === currentAttachment.externalUuid
                ? currentAttachment
                : item
            )
          );
        } else {
          updateAttachmentByUuidDispatch(
            response.data.meta.uuid,
            response.data.data[0]
          );
        }
        console.log(
          "[file uploaded successfully] > response.data",
          response.data
        );
      }
    } catch (e) {
      console.error("[file upload failed] > e", e); // TODO Denis remove when reduntant

      if (e && e.response) {
        console.error("[file upload failed] > e.response", e.response); // TODO Denis remove when reduntant
        if (isTaskCreate) {
          const attachmentsFromLocalStorage = takeItemFromLocalStorageSafe(
            "attachments",
            []
          );
          setAttachments(
            attachmentsFromLocalStorage.filter(item => item.uuid !== uuid)
          );
        }
      }

      if (errorIsFileTooLarge(e)) {
        toast.error(`${file.name}: ${t("file_too_large_error_message")}`);
        if (isTaskCreate) {
          const attachmentsFromLocalStorage = takeItemFromLocalStorageSafe(
            "attachments",
            []
          );
          setAttachments(
            attachmentsFromLocalStorage.filter(item => item.uuid !== uuid)
          );
        } else {
          deleteAttachmentDispatch({ uuid });
        }
        // } else if (errorIsDropboxAuthorizationFailed(e)) {
        //   toast.error(t("dropbox_authorization_failed"));
        //   deleteAttachmentDispatch({ uuid });
      } else if (
        errorIsYandexCloudUploadFailed(e) &&
        attempt <= UPLOAD_ATTEMPTS_LIMIT
      ) {
        console.error("YANDEX_CLOUD_UPLOAD_FAILED > attempt ", attempt);
        await handleUploadedFile(file, uuid, attempt + 1);
      } else {
        if (isTaskCreate) {
          const attachmentsFromLocalStorage = takeItemFromLocalStorageSafe(
            "attachments",
            []
          );
          setAttachments(
            attachmentsFromLocalStorage.filter(item => item.uuid !== uuid)
          );
        } else {
          deleteAttachmentDispatch({ uuid });
        }
        toast.error(t("Errors.AttachmentUpload"));
      }
    }
  };

  const deleteAttahment = async att => {
    updateAttachmentDispatch({ ...att, deleteInProgress: true });
    try {
      const response = await axios.delete(endpoints.ATTACHMENT, {
        data: { id: att.id }
      });
      if (responseIsStatusOk(response)) {
        toast.success(t("Success"));
        deleteAttachmentDispatch({ ...att });
      } else {
        toast.error(t("Errors.Error"));
      }
    } catch {
      toast.error(t("Errors.Error"));
    }
  };

  const onDrop = useCallback(files => {
    if (files.length > 5) {
      return toast.error(t("no_more_than_5_files"));
    }

    const filesWithUuid = files.map(file => ({
      uuid: v4(),
      file: file,
      filename: file.name
    }));

    const uploadingAttachments = filesWithUuid.map(fileWithUuid => ({
      uploadInProgress: true,
      uuid: fileWithUuid.uuid,
      filename: fileWithUuid.filename
    }));

    if (isTaskCreate) {
      const attachmentsFromLocalStorage = takeItemFromLocalStorageSafe(
        "attachments",
        []
      );
      setAttachments([...attachmentsFromLocalStorage, ...uploadingAttachments]);
    } else {
      addAttachmentsDispatch(uploadingAttachments);
    }

    filesWithUuid.forEach(async fileWithUuid => {
      await handleUploadedFile(fileWithUuid.file, fileWithUuid.uuid);
    });
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop });

  return (
    <div className="attachments-wrapper">
      {(hasAttachmants || canUpload || canManage) && (
        <h3>{t("Attachments")}</h3>
      )}
      {canUpload && (
        <form className="file-drop-form">
          <div
            className={classNames({
              dropzone: true,
              "file-hovering": isDragActive
            })}
            {...getRootProps()}
          >
            <input name="file" {...getInputProps()} />
            <p>{t("drag_n_drop_some_files_here_or_click_to_select_files")}</p>
          </div>
        </form>
      )}
      <div className="attachments-list">
        {isLoading && !hiddenLoading && (
          <div style={{ paddingTop: 15 }}>
            <Loading />
          </div>
        )}
        {!isLoading && _attachments && _attachments.length > 0 && (
          <div ref={deleteBtnRef}>
            {_attachments.map((att, index) => (
              <div
                className="attachment-item"
                key={att.id || att.uiid || index * -1}
              >
                <div style={{ display: "flex" }} className="file-link-wrapper">
                  <a
                    className={classNames({
                      "x-hovered": isTaskCreate
                        ? xHoveredOver === att.uuid
                        : xHoveredOver === att.id ||
                          att.deleteInProgress == true,
                      uploading: att.uploadInProgress
                    })}
                    href={
                      isTaskCreate
                        ? null
                        : att.url &&
                          process.env.REACT_APP_SERVER_URL &&
                          process.env.REACT_APP_API_PREFIX
                        ? `${process.env.REACT_APP_SERVER_URL}${process.env.REACT_APP_API_PREFIX}/${att.url}`
                        : `not-found`
                    }
                    style={{ margin: "auto 0" }}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    <span>
                      {att && att.filename && att.filename.substring(0, 50)}
                    </span>
                    {(att.uploadInProgress === true ||
                      att.deleteInProgress === true) && (
                      <img
                        className="spinner"
                        alt={t("Loading")}
                        src={ic_spinner}
                      />
                    )}
                  </a>
                </div>
                <div
                  onMouseOver={() => {
                    setXHoveredOver(att.id || att.uuid);
                  }}
                  onMouseOut={() => {
                    setXHoveredOver(null);
                  }}
                  className="delete-btn-wrapper"
                >
                  {!att.uploadInProgress && !att.deleteBtnArmed && (
                    <IconButton
                      disabled={att.deleteInProgress === true}
                      onClick={() => {
                        if (isTaskCreate) {
                          const attachmentsFromLocalStorage = takeItemFromLocalStorageSafe(
                            "attachments",
                            []
                          );
                          setAttachments(
                            attachmentsFromLocalStorage.map(item => {
                              const isThisTheOne =
                                att.uuid && item.uuid && item.uuid === att.uuid;
                              return { ...item, deleteBtnArmed: isThisTheOne };
                            })
                          );
                        } else {
                          disarmAllDeleteAttachmentButtonsAndArmOneDispatch(
                            att
                          );
                        }
                      }}
                      aria-label={t("clear")}
                    >
                      <Icon>delete</Icon>
                    </IconButton>
                  )}
                  {!att.uploadInProgress && att.deleteBtnArmed && (
                    <Button
                      style={{ color: "red", padding: "12px 8px" }}
                      className="confirm"
                      onClick={() => {
                        if (isTaskCreate) {
                          const attachmentsFromLocalStorage = takeItemFromLocalStorageSafe(
                            "attachments",
                            []
                          );
                          setAttachments(
                            attachmentsFromLocalStorage.filter(
                              item => item.uuid !== att.uuid
                            )
                          );
                        } else {
                          deleteAttahment(att);
                        }
                      }}
                    >
                      {t("delete_file")}
                    </Button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default withRouter(
  connect(
    state => ({
      attachments: state.attachments.list,
      hasAttachmants:
        state.attachments.list && state.attachments.list.length > 0,
      isLoading: state.attachments.fetchRequestStatus === constants.WAITING,
      canManage: state.project.info && state.project.info.can_manage,
      clearAttachments: state.helpForSelectedTask.clearAttachments
    }),
    {
      fetchTaskAttachments,
      addAttachmentsDispatch,
      updateAttachmentByUuidDispatch,
      updateAttachmentDispatch,
      deleteAttachmentDispatch,
      dropAttachmentsListDispatch,
      disarmAllDeleteAttachmentButtonsDispatch,
      disarmAllDeleteAttachmentButtonsAndArmOneDispatch,
      setClearAttachmentsDispatch
    }
  )(Attachments)
);
