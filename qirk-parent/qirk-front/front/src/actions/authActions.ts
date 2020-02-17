import { push } from "connected-react-router";
import axios from "../utils/axios";
import endpoints from "../utils/endpoints";
import actions from "../utils/constants";
import { setUser } from "./userActions";
import paths from "../routes/paths";
import constants from "../utils/constants";
import {
  errorIsEntityNotFound,
  responseIsStatusOk,
  errorIsInvalid,
  responseIsEmailSent,
  errorIsInvalidCredentials,
  errorIsAccountDisabled,
  removeItemFromLocalStorageSafe,
  errorIsTooManyLoginAttempts,
  errorIsInvalidRecaptcha,
  errorIsLicenseNotAccepted
} from "../utils/variousUtils";
import snackbarActions from "./snackbarActions";
import { Dispatch } from "react";
import { Action } from "../utils/types/Action";
import { State } from "../utils/types/State";

export const emailSignUp = ({
  email,
  license_accepted = true
}: {
  email: string;
  license_accepted: boolean;
}) => async (dispatch: Dispatch<Action>) => {
  try {
    dispatch(setRegistrationStatusCode("LOADING"));
    dispatch(setRegistrationEmail(email));
    const response = await axios.post(
      endpoints.POST_USER_REGISTER_NO_PASSWORD,
      {
        license_accepted,
        email
      }
    );
    if (responseIsStatusOk(response)) {
      if (responseIsEmailSent(response)) {
        dispatch(setRegistrationStatusCode("PENDING"));
      } else {
        dispatch(setRegistrationStatusCode(constants.EMAIL_NOT_DELIVERED));
      }
    }
  } catch (error) {
    if (errorIsInvalid(error)) {
      dispatch(setRegistrationStatusCode("EMAIL_IN_USE"));
    } else {
      dispatch(setRegistrationStatusCode("FAILED"));
    }
  }
};

export const activateAccount = (hash: string) => async (
  dispatch: Dispatch<Action>
) => {
  try {
    const result = await axios.post(endpoints.USER_ACTIVATE, { token: hash });
    if (responseIsStatusOk(result)) {
      dispatch(push(paths.Login.toPath()));
      dispatch(setActivationStatusCode("SUCCESS"));
    }
  } catch (error) {
    console.error(error);

    if (errorIsEntityNotFound(error)) {
      // todo ignore ??
    } else {
      dispatch(setActivationStatusCode("ERROR"));
    }
  }
};

export const fetchCsrf = () =>
  axios.get(endpoints.GET_CSRF_REFRESH).then(response => {
    if (responseIsStatusOk(response)) {
      return true;
    } else {
      return false;
    }
  });

export const resendRegistrationEmail = () => (
  dispatch: Dispatch<Action>,
  getState: () => State
) => {
  const email = getState().auth.registrationEmail;
  axios.post(endpoints.POST_USER_REGISTER_NO_PASSWORD, { email });
};

export const emailSignIn = (username: string, password: string) => async (
  dispatch: Dispatch<Action>
) => {
  const data = {
    username,
    password,
    remember_me: true
  };

  try {
    dispatch(setLoginStatusCode("LOADING"));
    const response = await axios.post(endpoints.LOGIN, data);
    if (responseIsStatusOk(response)) {
      dispatch(setAuthStatus(true));
    }
  } catch (error) {
    console.error(error);
    dispatch(setAuthStatus(false));
    if (errorIsLicenseNotAccepted(error)) {
      dispatch(setLoginStatusCode("LICENSE_NOT_ACCEPTED"));
    } else if (errorIsAccountDisabled(error)) {
      dispatch(setLoginStatusCode("ACCOUNT_DISABLED"));
    } else if (errorIsInvalidCredentials(error)) {
      dispatch(setLoginStatusCode("INVALID_CREDENTIALS"));
    } else if (errorIsTooManyLoginAttempts(error)) {
      dispatch(setLoginStatusCode("TOO_MANY_LOGIN_ATTEMPTS"));
    } else if (errorIsInvalidRecaptcha(error)) {
      dispatch(setLoginStatusCode("INVALID_RECAPTHA"));
    } else {
      dispatch(setLoginStatusCode("FAILED"));
    }
  }
};

export const logoutUser = () => async (
  dispatch: Dispatch<Action | Function>
) => {
  try {
    const response = await axios.post(endpoints.LOGOUT);
    if (responseIsStatusOk(response)) {
      dispatch(setAuthStatus(false));
      dispatch(dropStateOnUnauth());
      removeItemFromLocalStorageSafe([
        "task_description",
        "task_assignee",
        "task_status",
        "task_type",
        "task_priority",
        "selected_task_links_list",
        "project_id",
        "last_task_id",
        "filter_params"
      ]);
    }
  } catch (e) {
    console.error(e);
    dispatch(snackbarActions.error("Errors.Error"));
  }
};

export const checkAuthStatus = (pathname: string) => async (
  dispatch: Dispatch<Action>
) => {
  try {
    dispatch(setCheckAuthRequestStatus(constants.WAITING));
    const response = await axios.get(endpoints.CHECK_AUTH);
    if (
      response &&
      response.data &&
      response.data.data &&
      response.data.data.length > 0 &&
      response.data.data[0].id
    ) {
      dispatch(setAuthStatus(true));
      dispatch(setUser(response.data.data[0]));
      if (pathname) {
        dispatch(setAuthLastConfirmedSignedInPathname(pathname));
      }
    } else {
      dispatch(setAuthStatus(false));
      dispatch(dropStateOnUnauth());
    }
  } catch {
    dispatch(setAuthStatus(false));
    dispatch(dropStateOnUnauth());
  } finally {
    dispatch(setCheckAuthRequestStatus(constants.NOT_REQUESTED));
    dispatch(setLastLoginCheckPathname(pathname));
  }
};

const setLastLoginCheckPathname = (pathname: string) => ({
  payload: pathname,
  type: constants.SET_AUTH_LAST_LOGIN_CHECK_PATHNAME
});

const setAuthLastConfirmedSignedInPathname = (pathname: string) => ({
  payload: pathname,
  type: constants.SET_AUTH_LAST_CONFIRMED_SIGNED_IN_PATHNAME
});

export const changePassword = (newPass: string, oldPass: string) => async (
  dispatch: Dispatch<Action | Function>
) => {
  try {
    dispatch(setPasswordChangeStatusCode(actions.WAITING));
    const data = {
      new_password: newPass,
      password: oldPass
    };
    const result = await axios.put(endpoints.PUT_CHANGE_PASSWORD, data);
    if (responseIsStatusOk(result)) {
      dispatch(setPasswordChangeStatusCode(actions.SUCCESS));
      dispatch(snackbarActions.success("Success"));
    } else {
      dispatch(setPasswordChangeStatusCode(actions.FAILED));
      dispatch(snackbarActions.error("Failed"));
    }
  } catch (е) {
    console.error(е);
    dispatch(snackbarActions.error("Failed"));
    dispatch(setPasswordChangeStatusCode(actions.FAILED));
  }
};

export const setNewPassWithToken = (token: string, newPass: string) => async (
  dispatch: Dispatch<Action>
) => {
  try {
    dispatch(setResetPasswordStatus(actions.WAITING));
    const response = await axios.put(endpoints.PUT_CHANGE_PASSWORD, {
      token,
      new_password: newPass
    });
    if (responseIsStatusOk(response)) {
      dispatch(setResetPasswordStatus(actions.SUCCESS));
    } else {
      dispatch(setResetPasswordStatus(actions.FAILED));
    }
  } catch (e) {
    if (404 === e.response.status) {
      dispatch(setResetPasswordStatus(actions.TOKEN_EXPIRED));
    } else {
      dispatch(setResetPasswordStatus(actions.FAILED));
    }
  }
};

export const resetPassword = (email: string) => async (
  dispatch: Dispatch<Action>
) => {
  try {
    dispatch(setResetPasswordStatus(actions.WAITING));
    const response = await axios.post(endpoints.POST_USER_RESET_PASSWORD, {
      email
    });
    if (responseIsStatusOk(response)) {
      if (response.data.data[0].email_sent === true) {
        dispatch(setResetPasswordStatus(actions.EMAIL_DELIVERED));
      } else {
        dispatch(setResetPasswordStatus(actions.EMAIL_NOT_DELIVERED));
      }
    }
  } catch (e) {
    if (404 === parseInt(e.response.status)) {
      dispatch(setResetPasswordStatus(actions.EMAIL_NOT_FOUND));
    } else {
      dispatch(setResetPasswordStatus(actions.FAILED));
    }
  }
};

export const setResetPasswordStatusDispatch = (status: any) => (
  dispatch: Dispatch<Action>
) => {
  dispatch(setResetPasswordStatus(status));
};

export const setRegistrationStatusCode = (registrationStatusCode: any) => {
  return {
    type: actions.SET_REGISTRATION_STATUS,
    registrationStatusCode
  };
};

export const setLoginStatusCode = (loginStatusCode: any) => {
  return {
    type: actions.SET_LOGIN_STATUS,
    loginStatusCode
  };
};

export const dropStateOnUnauthDispatch = () => (dispatch: Dispatch<Action>) => {
  dispatch(dropStateOnUnauth());
};

const dropStateOnUnauth = () => ({ type: actions.DROP_STATE_ON_UNAUTH });

const setActivationStatusCode = (activationStatusCode: any) => {
  return {
    type: actions.SET_ACTIVATION_STATUS,
    activationStatusCode
  };
};

const setPasswordChangeStatusCode = (statusCode: any) => {
  return {
    type: actions.SET_PASSWORD_CHANGE_STATUS_CODE,
    payload: statusCode
  };
};

export const setDefaultRegistrationStatusCode = () => {
  return {
    type: actions.SET_REGISTRATION_STATUS,
    registrationStatusCode: "NOT_REQUESTED"
  };
};

export const setDefaultLoginStatusCode = () => {
  return {
    type: actions.SET_LOGIN_STATUS,
    loginStatusCode: "NOT_REQUESTED"
  };
};

const setRegistrationEmail = (registrationEmail: string) => {
  return {
    type: actions.SET_REGISTRATION_EMAIL,
    registrationEmail
  };
};

const setAuthStatus = (isSignedIn: boolean) => ({
  type: actions.SET_IS_SIGN_IN,
  isSignedIn
});

const setCheckAuthRequestStatus = (status: any) => ({
  payload: status,
  type: actions.SET_CHECK_AUTH_REQUEST_STATUS
});

const setResetPasswordStatus = (status: any) => ({
  payload: status,
  type: actions.SET_RESET_PASSWORD_STATUS
});
