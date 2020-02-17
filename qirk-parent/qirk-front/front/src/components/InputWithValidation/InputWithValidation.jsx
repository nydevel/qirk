import React, { useState, useEffect } from "react";

import axios from "../../utils/axios";
import { responseIsStatusOk } from "../../utils/variousUtils";
import TextInput from "../TextInput/TextInput";
import { useTranslation } from "react-i18next";

// Инпут, который проверяет валидность введенного значения по url
function InputWithValidation({
  url,
  urlReplaceParam,
  onChange,
  errorState: [error, setError],
  failedValidationText,
  typingTimeoutState: [timeoutState, setTimeoutState],
  pattern,
  patternValidationText,
  value,
  label,
  ...props
}) {
  const{t}=useTranslation()
  const [initialValue, setInitialValue] = useState(value);

  useState(() => {
    setInitialValue(value);
  }, [value]);

  const checkValue = (value, pattern = null) => {
    onChange(value);

    if (timeoutState) {
      clearTimeout(timeoutState);
    }

    if (initialValue === value) {
      return setError("");
    }

    if (value === "new") {
      return setError(failedValidationText || t("Errors.Error"));
    }

    if (
      value &&
      pattern &&
      (!value.match(pattern) ||
        value.match(pattern)[0] !== value.match(pattern)["input"])
    ) {
      if (patternValidationText) {
        setError(patternValidationText);
      }
      return;
    }

    setTimeoutState(
      setTimeout(async () => {
        try {
          const response = await axios.get(url.replace(urlReplaceParam, value));
          if (
            responseIsStatusOk(response) &&
            response.data.data &&
            response.data.data.length > 0 &&
            response.data.data[0].exists
          ) {
            setError(failedValidationText || t("Errors.Error"));
          } else {
            setError("");
          }
        } catch (error) {
          setError(t("Errors.Error"));
        } finally {
          setTimeoutState(null);
        }
      }, 500)
    );
  };

  const [isValid, setIsValid] = useState(true);
  const [errorText, setErrorText] = useState("");

  useEffect(() => {
    if (error) {
      setIsValid(false);
      setErrorText(error);
    } else {
      setIsValid(true);
      setErrorText("");
    }
  }, [error]);

  return (
    <TextInput
      value={value}
      pattern={pattern}
      errorText={errorText}
      isValid={isValid}
      label={label}
      onChange={e => checkValue(e.target.value, pattern)}
      {...props}
    />
  );
}

export default InputWithValidation;
