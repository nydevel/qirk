import React, { useState, useEffect, useRef } from "react";
import { withRouter } from "react-router-dom";
import { toast } from "react-toastify";
import { connect } from "react-redux";
import { t } from "i18next";
import Loading from "../Loading/Loading";
import axios from "../../utils/axios";
import endpoints from "../../utils/endpoints";
import {
  responseIsStatusOk,
  uiUserFromUser,
  regexMatch,
  responseIsEmailSent,
  errorIsAlreadyExists
} from "../../utils/variousUtils";
import constants from "../../utils/constants";
import { addProjectMembersDispatch } from "../../actions/projectActions";
import Button1 from "../Button/Button";
import Checkbox from "../Checkbox/Checkbox";
import {
  IconButton,
  Icon,
  Chip,
  Paper,
  ListItem,
  List
} from "@material-ui/core";
import TextInput from "../TextInput/TextInput";

const AddProjectMemberForm = ({
  addProjectMembersDispatch,
  projectId,
  onInvitePosted,
  user,
  match: {
    params: { project_uiid }
  }
}) => {
  const wrapperRef = useRef(null);

  const [hideOptions, setHideOptions] = useState(false);
  const [requestInProgress, setRequestInProgress] = useState(false);
  const [allowWrite, setAllowWrite] = useState(false);
  const [isManager, setIsManager] = useState(false);
  const [searchReqInProgress, setSearchReqInProgress] = useState(false);
  const [searchReqTimeout, setSearchReqTimeout] = useState(null);
  const [selectedMember, setSelectedMember] = useState(null);
  const [options, setOptions] = useState([]);
  const [searchStr, setSearchStr] = useState("");

  const handleClickOutside = e => {
    if (
      wrapperRef &&
      wrapperRef.current &&
      wrapperRef.current.contains(e.target)
    ) {
      setHideOptions(false);
    } else {
      setHideOptions(true);
    }
  };

  const processForm = async selectedMember => {
    if (!selectedMember) {
      return false;
    }

    if (requestInProgress) {
      return toast.info(t("this_request_is_already_in_progress"));
    }

    if (selectedMember.user && selectedMember.user.is_member) {
      try {
        setRequestInProgress(true);

        const response = await axios.post(endpoints.POST_PROJECT_MEMBER, {
          organization_member:
            selectedMember.user &&
            selectedMember.user.organization_member &&
            selectedMember.user.organization_member.member_id,
          write_allowed: allowWrite,
          manager: isManager,
          project: projectId
        });

        if (responseIsStatusOk(response)) {
          addProjectMembersDispatch(response.data.data);
          setOptions(options.filter(opt => opt.value !== selectedMember.value));
          toast.success(t("project_member_with_specified_permissions_added"));
        }
      } catch (e) {
        console.error(e);
        toast.error(t("Errors.Error"));
      } finally {
        setAllowWrite(false);
        setIsManager(false);
        setRequestInProgress(false);
        setSelectedMember(null);
        setSearchStr("");
      }
    } else if (
      selectedMember.user &&
      !selectedMember.user.is_member &&
      !selectedMember.is_email
    ) {
      try {
        setRequestInProgress(true);
        const response = await axios.post(
          endpoints.POST_GRANTED_PERMISSIONS_PROJECT_INVITE,
          {
            user: selectedMember.user.id,
            write_allowed: allowWrite,
            manager: isManager,
            project: projectId,
            text: ""
          }
        );
        if (responseIsStatusOk(response)) {
          const invite =
            response &&
            response.data &&
            response.data.data &&
            response.data.data.length > 0 &&
            response.data.data[0];

          invite &&
            invite.id &&
            onInvitePosted({
              ...invite,
              sender: { ...user },
              user: { ...selectedMember.user }
            });

          toast.success(t("invite_sent"));
        }
      } catch (e) {
        console.error(e);
        if (errorIsAlreadyExists(e)) {
          toast.error(t("this_user_is_already_invited_to_this_project"));
        } else {
          toast.error(t("Errors.Error"));
        }
      } finally {
        setAllowWrite(false);
        setIsManager(false);
        setRequestInProgress(false);
        setSelectedMember(null);
        setOptions(options.filter(opt => opt.value !== selectedMember.value));
      }
    } else if (selectedMember.is_email) {
      try {
        setRequestInProgress(true);
        const response = await axios.post(
          endpoints.POST_GRANTED_PERMISSIONS_PROJECT_INVITE_CREATE_BY_EMAIL,
          {
            email: searchStr,
            project: projectId,
            write_allowed: allowWrite,
            manager: isManager
          }
        );
        if (responseIsStatusOk(response)) {
          if (responseIsEmailSent(response)) {
            const { email_sent, ...inviteData } =
              response &&
              response.data &&
              response.data.data &&
              response.data.data.length > 0 &&
              response.data.data[0];

            if (inviteData && inviteData.id) {
              onInvitePosted({
                ...inviteData,
                email: searchStr,
                sender: { ...user }
              });
            }

            toast.success(t("invite_sent_via_email"));
            setAllowWrite(false);
            setIsManager(false);
            setSelectedMember(null);
            setSearchStr("");
            setOptions([]);
          } else {
            toast.error(t("email_not_delivered_error_message"));
          }
        }
      } catch (e) {
        console.error(e);
        toast.error(t("Errors.Error"));
      } finally {
        setRequestInProgress(false);
      }
    }
  };

  const getSearchResults = async () => {
    try {
      setSearchReqInProgress(true);

      const response = await axios.get(endpoints.GET_USER_SEARCH_FOR_PROJECT, {
        params: { project_ui_id: project_uiid, prefix: searchStr }
      });

      if (responseIsStatusOk(response)) {
        const results = response.data.data;

        const options = results.map(user => ({
          label: uiUserFromUser(user),
          value: user.id,
          user: user
        }));

        if (options.length > 0) {
          setOptions(options);
        } else if (regexMatch(searchStr.toLowerCase(), constants.REGEX_EMAIL)) {
          setOptions([
            {
              label: `${t("invite_via_email")}: ${searchStr}`,
              value: searchStr,
              is_email: true
            }
          ]);
        } else {
          setOptions(options);
        }
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setSearchReqInProgress(false);
    }
  };

  useEffect(() => {
    if (searchReqTimeout) {
      clearTimeout(searchReqTimeout);
      setSearchReqTimeout(null);
    }

    if (searchStr) {
      setSearchReqTimeout(
        setTimeout(() => {
          getSearchResults();
          setSearchReqTimeout(null);
        }, 350)
      );
    } else {
      setOptions([]);
    }
  }, [searchStr]);

  useEffect(() => {
    if (isManager) {
      setAllowWrite(true);
    }
  }, [isManager]);

  useEffect(() => {
    if (!allowWrite) {
      setIsManager(false);
    }
  }, [allowWrite]);

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <div style={{ position: "relative" }} ref={wrapperRef}>
      {selectedMember === null && (
        <div style={{ display: "flex" }}>
          <div style={{ flex: 1 }}>
            <TextInput
              autoComplete="off"
              value={searchStr}
              onChange={e => setSearchStr(e.target.value)}
              label={t("Add member")}
              helperText={t("start_typing_username_or_email")}
              id="add_project_member_search_user_or_email_input"
              onFocus={() => {
                setHideOptions(false);
              }}
              onKeyPress={e => {
                if (
                  e.key === "Enter" &&
                  options &&
                  options.length === 1 &&
                  !searchReqInProgress
                ) {
                  setSelectedMember(options[0]);
                }
              }}
            />
          </div>
          {searchStr && (
            <div style={{ padding: "4px 0 0px 8px" }}>
              <IconButton
                onClick={() => {
                  setSearchStr("");
                  setOptions([]);
                }}
                aria-label={t("clear")}
              >
                <Icon>clear</Icon>
              </IconButton>
            </div>
          )}
        </div>
      )}

      {selectedMember !== null && selectedMember.label && (
        <div style={{ display: "flex" }}>
          <div style={{ flex: 1 }}>
            <TextInput
              value={selectedMember.label}
              onChange={() => {
                setSelectedMember(null);
              }}
              label={t("Add member")}
              id="selected_user_input"
            />
          </div>
          <div style={{ padding: "4px 0 0px 8px" }}>
            <IconButton
              onClick={() => {
                setSelectedMember(null);
              }}
              aria-label={t("clear")}
            >
              <Icon>clear</Icon>
            </IconButton>
          </div>
        </div>
      )}

      {searchReqInProgress && (
        <div style={{ paddingBottom: 30 }}>
          <Loading />
        </div>
      )}

      {!searchReqInProgress &&
        options.length === 0 &&
        searchStr !== "" &&
        searchReqTimeout === null &&
        selectedMember === null && (
          <div style={{ paddingBottom: 30 }}>{t("no_results_found")}</div>
        )}

      {options &&
        !searchReqInProgress &&
        options.length > 0 &&
        selectedMember === null && (
          <Paper
            style={{
              position: "absolute",
              top: 80,
              width: "calc(100% - 57px)",
              zIndex: 1,
              display: hideOptions ? "none" : "block",
              maxHeight: 500,
              overflowY: "auto"
            }}
          >
            <List>
              {options.map(opt => (
                <ListItem
                  button
                  key={opt.value}
                  onClick={() => setSelectedMember(opt)}
                >
                  {opt.label}
                  {opt.user && opt.user.is_member && (
                    <>
                      {` `}
                      <Chip id={opt.user.id} label={t("colleague")} />
                    </>
                  )}
                </ListItem>
              ))}
            </List>
          </Paper>
        )}
      {selectedMember !== null && (
        <div style={{ paddingBottom: 30 }}>
          <form
            onSubmit={e => {
              e.preventDefault();
              processForm(selectedMember);
            }}
          >
            <fieldset>
              <Checkbox
                disabled={requestInProgress}
                checked={allowWrite}
                onChange={e => setAllowWrite(e.target.checked)}
                label={t("writing_allowed")}
              />

              <Checkbox
                disabled={requestInProgress}
                checked={isManager}
                onChange={e => setIsManager(e.target.checked)}
                label={t("project_manager")}
              />

              <div>
                {selectedMember.user && selectedMember.user.is_member && (
                  <Button1 disabled={requestInProgress}>
                    {t("make_project_member")}
                  </Button1>
                )}

                {selectedMember.is_email && (
                  <Button1 disabled={requestInProgress}>
                    {t("send_invite_via_email")}
                  </Button1>
                )}

                {!(selectedMember.user && selectedMember.user.is_member) &&
                  !selectedMember.is_email && (
                    <Button1 disabled={requestInProgress}>
                      {t("send_invite")}
                    </Button1>
                  )}
              </div>
            </fieldset>
          </form>
        </div>
      )}
    </div>
  );
};

export default withRouter(
  connect(
    state => ({
      projectId: state.project.info && state.project.info.id,
      user: state.user
    }),
    { addProjectMembersDispatch }
  )(AddProjectMemberForm)
);
