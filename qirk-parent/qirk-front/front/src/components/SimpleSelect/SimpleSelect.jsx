import React from "react";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import FormControl from "@material-ui/core/FormControl";
import Select from "@material-ui/core/Select";
import FilledInput from "@material-ui/core/FilledInput";

function SimpleSelect({
  label,
  id,
  value,
  onChange,
  options,
  style,
  className,
  variant = "filled",
  ...props
}) {
  return (
    <FormControl style={style} variant={variant} className={className}>
      <InputLabel htmlFor={id}>{label}</InputLabel>
      <Select
        {...props}
        value={value}
        onChange={e =>
          onChange({ ...options.find(o => o.value === e.target.value) })
        }
        input={<FilledInput name={id} id={id} />}
      >
        {options.map((o, index) => (
          <MenuItem key={o.value + index} value={o.value}>
            {o.label}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
}

export default SimpleSelect;
