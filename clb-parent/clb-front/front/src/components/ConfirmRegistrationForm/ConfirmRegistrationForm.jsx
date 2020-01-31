import React, { useState, useEffect } from "react";
import qs from "query-string";
import { withRouter } from "react-router-dom";
import { t } from "i18next";
import { toast } from "react-toastify";
import axios from "../../utils/axios";
import endpoints from "../../utils/endpoints";
import paths from "../../routes/paths";
import {
  responseIsStatusOk,
  regexMatch,
  errorIsEntityNotFound
} from "../../utils/variousUtils";
import TextInput from "../../components/TextInput/TextInput";
import Button from "../../components/Button/Button";
import constants from "../../utils/constants";

const USERNAME_CHECK_TIMEOUT_MS = 350;

const RegisterConfirmationForm = ({ location: { search }, history }) => {
  const { code, invite_key } = qs.parse(search);

  const invitedToProjectMode = !!invite_key;

  const [
    checkingIfAlreadyRegistered,
    setCheckingIfAlreadyRegistered
  ] = useState(false); // token if accepting email invite
  const [fullName, setFullName] = useState("");
  const [username, setUsername] = useState("");
  const [usernameError, setUsernameError] = useState(null);
  const [fullNameError, setFullNameError] = useState(null);
  const [usernameIsUnique, setUsernameIsUnique] = useState(true);
  const [checkingUsernameUniqueness, setCheckingUsernameUniqueness] = useState(
    false
  );
  const [usernameCheckTimeout, setUsernameCheckTimeout] = useState(null);

  const [confirming, setConfirming] = useState(false);

  const handleChangedUsernameInput = e => {
    const username = e.target.value;

    setUsername(username);

    if (regexMatch(username, constants.REGEX_USERNAME)) {
      setUsernameError(null);

      if (usernameCheckTimeout) {
        clearTimeout(usernameCheckTimeout);
      }

      setUsernameCheckTimeout(
        setTimeout(() => {
          checkUsernameUniqueness(username);
          setUsernameCheckTimeout(null);
        }, USERNAME_CHECK_TIMEOUT_MS)
      );
    } else {
      setUsernameError(t("must_be_alphanumeric"));
    }
  };

  const checkUsernameUniqueness = async username => {
    try {
      setCheckingUsernameUniqueness(true);

      const response = await axios.get(endpoints.GET_USER_CHECK_USERNAME, {
        params: { username }
      });
      if (responseIsStatusOk(response)) {
        if (
          response &&
          response.data &&
          response.data.data &&
          response.data.data.length > 0 &&
          response.data.data[0]
        ) {
          if (response.data.data[0].exists) {
            setUsernameIsUnique(false);
            setUsernameError(t("this_username_is_taken"));
          } else {
            setUsernameIsUnique(true);
            setUsernameError("");
          }
        }
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setCheckingUsernameUniqueness(false);
    }
  };

  const confirm = async () => {
    try {
      if (confirming) {
        return toast.error(t("this_request_is_already_in_progress"));
      }

      setConfirming(true);

      let response;

      if (invitedToProjectMode) {
        response = await axios.post(
          endpoints.POST_PROJECT_INVITE_TOKEN_ACCEPT,
          {
            token: invite_key,
            username: username.trim() || null,
            full_name: fullName.trim() || null
          }
        );
      } else {
        response = await axios.post(endpoints.POST_USER_ACTIVATE_NO_PASSWORD, {
          token: code,
          username: username.trim(),
          full_name: fullName.trim()
        });
      }

      if (responseIsStatusOk(response)) {
        if (invitedToProjectMode && !username.trim() && !fullName.trim()) {
          toast.success(t("invite_accepted"));
        } else {
          toast.success(t("registration_completed"));
        }
        history.push(paths.Login.toPath());
      } else {
        toast.error(t("Errors.Error"));
      }
    } catch (e) {
      console.error(e);
      if (errorIsEntityNotFound(e)) {
        toast.error(t("looks_like_url_is_outdated"));
      } else {
        toast.error(t("Errors.Error"));
      }
    } finally {
      setConfirming(false);
    }
  };

  const checkToken = async () => {
    if (!invite_key) {
      return toast.error(t("Bad Url"));
    }

    try {
      setCheckingIfAlreadyRegistered(true);

      const response = await axios.get(
        endpoints.GET_PROJECT_INVITE_CHECK_TOKEN,
        { params: { token: invite_key } }
      );

      if (
        responseIsStatusOk(response) &&
        response.data.data &&
        response.data.data.length > 0
      ) {
        if (response.data.data[0].registered) {
          confirm();
        }
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setCheckingIfAlreadyRegistered(false);
    }
  };

  useEffect(() => {
    if (invitedToProjectMode) {
      checkToken();
    }
  }, [invitedToProjectMode]);

  return (
    <form
      onSubmit={e => {
        e.preventDefault();
        confirm();
      }}
    >
      <fieldset disabled={confirming}>
        <TextInput
          id="username"
          required
          pattern={constants.REGEX_USERNAME}
          errorText={usernameError}
          autocomplete="username"
          value={username}
          maxLength="25"
          onChange={handleChangedUsernameInput}
          label={t("username")}
        />
        <TextInput
          id="name"
          required
          errorText={fullNameError}
          autocomplete="name"
          value={fullName}
          maxLength="255"
          onChange={e => {
            setFullName(e.target.value);
            if (e.target.value.trim()) {
              setFullNameError(null);
            } else {
              setFullNameError(t("must_be_not_blank"));
            }
          }}
          label={t("full_name")}
        />
        <Button
          style={{ width: "100%" }}
          disabled={
            confirming ||
            usernameError ||
            fullNameError ||
            checkingUsernameUniqueness ||
            checkingIfAlreadyRegistered ||
            usernameCheckTimeout ||
            !usernameIsUnique
          }
        >
          {invitedToProjectMode
            ? t("accept_invite")
            : t("complete_registration")}
        </Button>
      </fieldset>
    </form>
  );
};

const connectedRegisterConfirmationForm = withRouter(RegisterConfirmationForm);

export default connectedRegisterConfirmationForm;
