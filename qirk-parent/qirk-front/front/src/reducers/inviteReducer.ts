import constants from "../utils/constants";
import { RequestStatus } from "../utils/enums/RequestStatus";
import { Action } from "../utils/types/Action";

export type InviteReducerState = {
  inviteUserToProjectRequestStatus: RequestStatus;
};

const INITIAL_STATE: InviteReducerState = {
  inviteUserToProjectRequestStatus: RequestStatus.NOT_REQUESTED
};

export default (
  state: InviteReducerState = INITIAL_STATE,
  action: Action
): InviteReducerState => {
  switch (action.type) {
    case constants.SET_INVITE_USER_TO_PROJECT_REQUEST_STATUS:
      return { ...state, inviteUserToProjectRequestStatus: action.payload };

    case constants.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };

    default:
      return state;
  }
};
