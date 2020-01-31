import React from "react";
import { FormControl, InputLabel, Select, MenuItem } from "@material-ui/core";

const TaskSearchSelectFilter = ({
  id = "",
  name = "",
  label = "",
  value = "",
  options = [],
  multiple = false,
  placeholder = "",
  formControlStyle = {},
  onChange = () =>
    console.error("onChange prop in TaskSearchSelectFilter not specified"),
  ...props
}) => {
  return (
    <FormControl style={formControlStyle} {...props}>
      <InputLabel htmlFor={id}>{label}</InputLabel>
      <Select
        value={value}
        onChange={onChange}
        multiple={multiple}
        inputProps={{
          id,
          name
        }}
      >
        {placeholder && (
          <MenuItem value="" disabled>
            {placeholder}
          </MenuItem>
        )}
        {options &&
          options.length > 0 &&
          options.map(o => (
            <MenuItem key={`${o.value}${o.label}`} value={o.value}>
              {o.label}
            </MenuItem>
          ))}
      </Select>
    </FormControl>
  );
};

export default TaskSearchSelectFilter;
