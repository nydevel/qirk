import { useEffect } from "react";
import actions from "../constants";
import { t } from "i18next";
import { toast } from "react-toastify";

const ignoreDuplicatedToasts = true; // Для actions с несколькими dispatch() внутри функции
const timeoutInMillis = 250;

let lastRequestStatus = null;
let lastSpecificErrorMessages = null;
let lastToastInMillis = 0;

export default function useRequestResultToast(
  requestResult,
  specificErrorMessages
) {
  useEffect(() => {
    const currentMillis = Date.now();

    if (
      ignoreDuplicatedToasts &&
      lastRequestStatus === requestResult &&
      lastSpecificErrorMessages === specificErrorMessages &&
      currentMillis - lastToastInMillis < timeoutInMillis
    ) {
      return;
    }

    lastRequestStatus = requestResult;
    lastSpecificErrorMessages = specificErrorMessages;
    lastToastInMillis = currentMillis;

    switch (requestResult) {
      case actions.SUCCESS:
        toast.success(
          (specificErrorMessages && specificErrorMessages.SUCCESS) ||
            t("Success")
        );
        break;
      case actions.FAILED:
        toast.error(
          (specificErrorMessages && specificErrorMessages.FAILED) ||
            t("Errors.Error")
        );
        break;
      case actions.CONFLICT:
        toast.error(
          (specificErrorMessages && specificErrorMessages.CONFLICT) ||
            t("Errors.Conflict")
        );
        break;
      case actions.CONSTRAINT_VIOLATION:
        toast.error(
          (specificErrorMessages &&
            specificErrorMessages.CONSTRAINT_VIOLATION) ||
            t("Errors.ConstraintViolation")
        );
        break;
      default:
        // Do nothing
        break;
    }
  });
}
