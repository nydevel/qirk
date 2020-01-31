import React from "react";
import classNames from "classnames";
import MaterialCheckbox from "@material/react-checkbox";
import "./Checkbox.sass";
import WithTooltip from "../WithTooltip/WithTooltip";

function Checkbox({
  label,
  checked = false,
  className = "",
  tooltip = {},
  ...props
}) {
  const {
    isTooltip = false,
    tooltipId = "tooltip",
    tooltipDataTip = ""
  } = tooltip;
  return (
    <label>
      <div className="custom-mdc-checkbox">
        <MaterialCheckbox
          className={
            "" +
            className +
            " " +
            classNames({
              "checkbox-input": true,
              qa_checkbox_checked: checked,
              qa_checkbox_unchecked: !checked
            })
          }
          checked={checked}
          {...props}
        />
        {isTooltip ? (
          <WithTooltip dataTip={tooltipDataTip} id={tooltipId}>
            <span className="checkbox-label" style={{ margin: 0 }}>
              {label}
            </span>
          </WithTooltip>
        ) : (
          <span className="checkbox-label">{label}</span>
        )}
      </div>
    </label>
  );
}

export default Checkbox;
