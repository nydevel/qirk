import React, { useRef } from "react";
import AsyncSelect from "react-select/lib/Async";
import PropTypes from "prop-types";
import { t } from "i18next";
import List, { ListItem, ListItemText } from "@material/react-list";
import TextField from "@material-ui/core/TextField";
import axios from "../../utils/axios";
import { responseIsStatusOk } from "../../utils/variousUtils";

function SelectAsync({
  listFetchUrl,
  listReplaceWithValue,
  // [{ value: 1, label: "4head"}]
  value,
  valueName,
  valueWrapper,
  labelName,
  labelWrapper,
  labelGenerator,
  onChange,
  required,
  isMulti,
  label,
  isClearable = true,
  className,
  ...props
}) {
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

  const ValueContainer = ({ children }) => (
    <div
      className="crs-ValueContainer-SelectAsync"
      style={{ flex: 1, overflow: "hidden" }}
    >
      {children}
    </div>
  );

  const Control = ({ children, innerProps, innerRef }) => (
    <TextField
      variant="filled"
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
    menu: base => ({ ...base, zIndex: 2 }),
    input: () => ({ display: "flex", maxWidth: 180 }),
    singleValue: () => ({})
  };

  const inputRef = useRef(null);

  const loadOptions = async inputValue => {
    const fetchUrl = listReplaceWithValue
      ? listFetchUrl.replace(listReplaceWithValue, inputValue)
      : listFetchUrl;
    const response = await axios.get(fetchUrl);
    if (responseIsStatusOk(response)) {
      return response.data.data.map(item => {
        const rValue = valueWrapper
          ? item[valueWrapper][valueName]
          : item[valueName];

        const rLabel = labelGenerator
          ? labelGenerator(item)
          : (labelWrapper && item[labelWrapper][labelName]) || item[labelName];

        return {
          value: rValue,
          label: rLabel
        };
      });
    }
  };

  return (
    <>
      <AsyncSelect
        components={{ MenuList, Option, ValueContainer, Control }}
        required={required}
        className={`react-select ${className}`}
        placeholder={`${t("Button.Select")}...`}
        isSearchable={true}
        ref={inputRef}
        isClearable={isClearable}
        isMulti={isMulti}
        loadOptions={loadOptions}
        onChange={e => onChange(e || "")}
        value={value}
        defaultOptions
        styles={styles}
        {...props}
      />
      {/* костыль для поддеркжи required (к черту эту либу) */}
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
        onFocus={() => inputRef.current.focus()}
      />
    </>
  );
}

SelectAsync.defaultProps = {
  valueName: "id",
  labelName: "name"
};

SelectAsync.propTypes = {
  placeholder: PropTypes.string,
  value: PropTypes.any,
  listFetchUrl: PropTypes.string,
  valueName: PropTypes.string,
  labelName: PropTypes.string,
  onChange: PropTypes.func
};

export default SelectAsync;
