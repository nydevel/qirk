import React from "react";
import Checkbox from "@material/react-checkbox";
import TextField, { Input } from "@material/react-text-field";
import "./Input.sass";

function Input1({
  isFailedStyle,
  errorText,
  textarea,
  value,
  className = "",
  wrapperClass = "",
  inputClass = "",
  type,
  disabled,
  ...props
}) {
  return (
    <div
      className={`input__wrapper ${wrapperClass ? wrapperClass : className}`}
    >
      {textarea ? (
        <textarea
          {...props}
          disabled={disabled}
          defaultValue={value || ""}
          className={`${errorText || isFailedStyle ? "input--error" : ""} ${
            disabled ? "input--disabled" : ""
          } ${inputClass}`}
        />
      ) : (
        <>
          {type === "checkbox" ? (
            <Checkbox {...props} />
          ) : (
            <TextField outlined {...props}>
              <Input {...props} />
            </TextField>
          )}
        </>
      )}
      <div className="input__error">{errorText}</div>
    </div>
  );
}

export default Input1;
