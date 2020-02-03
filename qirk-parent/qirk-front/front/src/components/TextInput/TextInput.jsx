import React, { forwardRef } from "react";
import TextField, { HelperText, Input } from "@material/react-text-field";

function TextInput({
  label,
  value,
  onChange,
  helperText,
  errorText,
  disabled,
  textarea,
  required,
  ref,
  className,
  inputClassName = "",
  textFieldStyle,
  isValid,
  hideErrorArea = false,
  ...props
}) {
  return (
    <>
      <TextField
        outlined
        style={textFieldStyle}
        className={className}
        textarea={textarea}
        helperText={helperText ? <HelperText>{helperText}</HelperText> : null}
        label={label}
      >
        <Input
          ref={ref}
          isValid={isValid || !errorText}
          required={required}
          disabled={disabled}
          value={value}
          onChange={onChange}
          className={inputClassName}
          {...props}
        />
      </TextField>
      {!hideErrorArea && (
        <div
          style={{
            height: 28,
            paddingTop: 4,
            color: "#b00020",
            fontSize: "0.8em"
          }}
        >
          {errorText}
        </div>
      )}
    </>
  );
}

export default forwardRef(TextInput);
