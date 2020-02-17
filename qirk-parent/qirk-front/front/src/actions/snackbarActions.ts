import { Snackbar } from "../utils/types/Snackbar";
import { Action } from "../utils/types/Action";
import constants from "../utils/constants";
import { SnackbarType } from "../utils/enums/SnackbarType";
import { Dispatch } from "react";

const _setSnackbar = (snackbar: Snackbar): Action => ({
  type: constants.SET_SNACKBAR,
  payload: snackbar
});

export const error = (message: Snackbar["message"]) => (
  dispatch: Dispatch<Action>
) => {
  dispatch(
    _setSnackbar({
      message,
      type: SnackbarType.ERROR
    })
  );
};

const success = (message: Snackbar["message"]) => (
  dispatch: Dispatch<Action>
) => {
  dispatch(
    _setSnackbar({
      message,
      type: SnackbarType.SUCCESS
    })
  );
};

const info = (message: Snackbar["message"]) => (dispatch: Dispatch<Action>) => {
  dispatch(
    _setSnackbar({
      message,
      type: SnackbarType.INFO
    })
  );
};

const warn = (message: Snackbar["message"]) => (dispatch: Dispatch<Action>) => {
  dispatch(
    _setSnackbar({
      message,
      type: SnackbarType.WARN
    })
  );
};

export default {
  success,
  warn,
  info,
  error
};
