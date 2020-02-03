import React from "react";
import Select from "react-select";
import List, { ListItem, ListItemText } from "@material/react-list";
import TextField from "@material-ui/core/TextField";
import "./TypableSelectInput.sass";

function TypableSelectInput({
  name,
  label,
  value,
  options,
  placeholder = "",
  disabled,
  onChange,
  isLoading,
  className,
  isClearable,
  isSearchable,
  menuPlacement,
  maxHeightOfMenuList = 350,
  ...props
}) {
  // ok
  const MenuList = ({ children, ...props }) => (
    <List
      style={{ maxHeight: maxHeightOfMenuList, overflowY: "auto" }}
      {...props}
    >
      {children}
    </List>
  );

  // ok
  const Option = ({ innerRef, children, innerProps }) => (
    <ListItem className="crs-Option" ref={innerRef} {...innerProps}>
      <ListItemText primaryText={children} />
    </ListItem>
  );

  // --
  function inputComponent({ inputRef, ...props }) {
    return <div ref={inputRef} {...props} />;
  }

  // ??
  const ValueContainer = ({ children }) => (
    <div className="crs-ValueContainer-TSI" style={{ flex: 1 }}>
      {children}
    </div>
  );

  // --
  const Control = ({ children, innerProps, innerRef }) => (
    <TextField
      variant="standard"
      className="crs-TextField-Control"
      label={label}
      InputLabelProps={{
        shrink: true // TODO Denis
      }}
      fullWidth
      InputProps={{
        inputComponent,
        inputProps: {
          ref: innerRef,
          style: {
            display: "flex",
            alignItems: "center",
            padding: 0,
            height: "auto",
            background: "#fff"
          },
          children,
          ...innerProps
        }
      }}
    >
      {children}
    </TextField>
  );

  const styles = {
    control: base => ({
      ...base,
      border: "0px solid pink",
      boxShadow: "none",
      width: "100%"
    }),
    input: () => ({ display: "flex", maxWidth: 180 }),
    singleValue: () => ({ position: "absolute", top: "0px" })
  };

  return (
    <Select
      styles={styles}
      name={name}
      value={value}
      options={options}
      onChange={onChange}
      className={className}
      isDisabled={disabled}
      isLoading={isLoading}
      isClearable={isClearable}
      isSearchable={isSearchable}
      components={{ MenuList, Option, ValueContainer, Control }}
      menuPlacement={menuPlacement || "auto"}
      placeholder={placeholder}
      {...props}
    />
  );
}

export default TypableSelectInput;
