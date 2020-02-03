import React from "react";
import Radio, { NativeRadioControl } from "@material/react-radio";

function RadioInput({
  label,
  value,
  name,
  id,
  onChange,
  checked,
  disabled,
  className,
  ...props
}) {
  return (
    <Radio {...props} className={className} label={label} key={value}>
      <NativeRadioControl
        checked={checked}
        disabled={disabled}
        name={name}
        value={value}
        id={id}
        onChange={onChange}
      />
    </Radio>
  );
}

export default RadioInput;
