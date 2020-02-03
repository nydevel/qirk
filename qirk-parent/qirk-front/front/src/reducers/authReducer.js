import actions from "../utils/constants";

const INITIAL_STATE = {
  resetPasswordStatus: actions.NOT_REQUESTED,
  passwordChangeStatusCode: actions.NOT_REQUESTED,
  registrationStatusCode: actions.NOT_REQUESTED,
  loginStatusCode: actions.NOT_REQUESTED,
  activationStatusCode: actions.NOT_REQUESTED,
  registrationEmail: "",
  isSignedIn: false,
  checkAuthRequestStatus: actions.NOT_REQUESTED,
  lastConfirmedSignedInPathname: "",
  lastLoginCheckPathname: ""
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case actions.SET_AUTH_LAST_CONFIRMED_SIGNED_IN_PATHNAME:
      return { ...state, lastConfirmedSignedInPathname: action.payload };

    case actions.SET_AUTH_LAST_LOGIN_CHECK_PATHNAME:
      return { ...state, lastLoginCheckPathname: action.payload };

    case actions.SET_CHECK_AUTH_REQUEST_STATUS:
      return { ...state, checkAuthRequestStatus: action.payload };
    case actions.SET_RESET_PASSWORD_STATUS:
      return { ...state, resetPasswordStatus: action.payload };
    case actions.SET_PASSWORD_CHANGE_STATUS_CODE:
      return { ...state, passwordChangeStatusCode: action.payload };
    case actions.SET_IS_SIGN_IN:
      return { ...state, isSignedIn: action.isSignedIn };
    case actions.SET_REGISTRATION_EMAIL:
      return { ...state, registrationEmail: action.registrationEmail };
    case actions.SET_REGISTRATION_STATUS:
      return {
        ...state,
        registrationStatusCode: action.registrationStatusCode
      };
    case actions.SET_ACTIVATION_STATUS:
      return { ...state, activationStatusCode: action.activationStatusCode };
    case actions.SET_LOGIN_STATUS:
      return { ...state, loginStatusCode: action.loginStatusCode };
    case actions.RESET_AUTH_STATE:
      return { ...INITIAL_STATE };
    case actions.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };
    default:
      return state;
  }
};
