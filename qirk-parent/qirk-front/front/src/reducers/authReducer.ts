import actions from "../utils/constants";
import { RequestStatus } from "../utils/enums/RequestStatus";

export type AuthReducerState = {
  resetPasswordStatus: RequestStatus;
  passwordChangeStatusCode: RequestStatus;
  registrationStatusCode: RequestStatus;
  loginStatusCode: RequestStatus;
  activationStatusCode: RequestStatus;
  registrationEmail: string;
  isSignedIn: boolean;
  checkAuthRequestStatus: RequestStatus;
  lastConfirmedSignedInPathname: string;
  lastLoginCheckPathname: string;
};

const INITIAL_STATE: AuthReducerState = {
  resetPasswordStatus: RequestStatus.NOT_REQUESTED,
  passwordChangeStatusCode: RequestStatus.NOT_REQUESTED,
  registrationStatusCode: RequestStatus.NOT_REQUESTED,
  loginStatusCode: RequestStatus.NOT_REQUESTED,
  activationStatusCode: RequestStatus.NOT_REQUESTED,
  registrationEmail: "",
  isSignedIn: false,
  checkAuthRequestStatus: RequestStatus.NOT_REQUESTED,
  lastConfirmedSignedInPathname: "",
  lastLoginCheckPathname: ""
};

export default (
  state: AuthReducerState = INITIAL_STATE,
  action: any //Action // TODO
): AuthReducerState => {
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
