import { SnackbarType } from "../enums/SnackbarType";

export type Snackbar = {
  message: string | null;
  type: SnackbarType;
};
