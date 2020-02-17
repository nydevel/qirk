import React, { useRef } from "react";
import PropTypes from "prop-types";
import ReactSelect from "react-select";

import List, { ListItem, ListItemText } from "@material/react-list";
import TextField from "@material-ui/core/TextField";
import { ChipSet, Chip } from "@material/react-chips";
import MaterialIcon from "@material/react-material-icon";
import "./Select.sass";
import { useTranslation } from "react-i18next";

const Select = React.forwardRef(
  (
    {
      options,
      value,
      onChange,
      isMulti,
      required,
      name,
      className,
      label,
      ...props
    },
    ref
  ) => {
    const { t } = useTranslation();

    const inputRef = useRef(null);
    const contextRef = ref && ref.current ? ref : inputRef;

    const MenuList = ({ children, ...props }) => (
      <List {...props}>{children}</List>
    );

    const Option = ({ innerRef, children, innerProps }) => (
      <ListItem className="crs-Option" ref={innerRef} {...innerProps}>
        <ListItemText primaryText={children} />
      </ListItem>
    );

    function inputComponent({ inputRef, ...props }) {
      return <div ref={inputRef} {...props} />;
    }

    function MultiValue(props) {
      return (
        <Chip
          tabIndex={-1}
          label={props.children}
          onDelete={props.removeProps.onClick}
          trailingIcon={<MaterialIcon icon="cancel" {...props.removeProps} />}
        />
      );
    }

    const ValueContainer = ({ children }) => (
      <ChipSet
        className="crs-ValueContainer"
        style={{ flex: 1, marginTop: -10 }}
      >
        {children}
      </ChipSet>
    );

    const Control = ({ children, innerProps, innerRef }) => (
      <TextField
        variant="outlined"
        className="crs-TextField-Control"
        label={label}
        InputLabelProps={{
          shrink: true // TODO Denis
        }}
        style={{ width: "100%" }}
        InputProps={{
          inputComponent,
          inputProps: {
            ref: innerRef,
            style: { display: "flex" },
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
      input: base => ({ ...base, marginTop: 10, display: "flex" }),
      valueContainer: base => ({ ...base, flex: 1, marginTop: -10 }),
      singleValue: () => ({ position: "absolute", top: "28px" })
    };

    return (
      <React.Fragment>
        <ReactSelect
          menuPlacement="auto"
          components={{ MenuList, Option, MultiValue, ValueContainer, Control }}
          styles={styles}
          className={`react-select ${className}`}
          value={value}
          ref={contextRef}
          onChange={e => onChange(e)}
          placeholder={`${t("Button.Select")}...`}
          name={name}
          isMulti={isMulti}
          isSearchable={true}
          isClearable={true}
          options={options}
          {...props}
        />
        <input
          tabIndex={-1}
          value={value}
          required={required}
          onChange={e => onChange(e)}
          style={{
            opacity: 0,
            width: 0,
            height: 0,
            left: "50%",
            position: "absolute"
          }}
          onFocus={() => contextRef.current.focus()}
        />
      </React.Fragment>
    );
  }
);

Select.propTypes = {
  options: PropTypes.array
};

export default Select;
