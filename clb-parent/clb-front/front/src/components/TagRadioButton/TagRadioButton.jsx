import React from "react";
import classNames from "classnames";
import "./TagRadioButton.sass";

const TagRadioButton = ({
  clickable = true,
  selected,
  children,
  onClick,
  ...props
}) => {
  return (
    <span
      onClick={e => {
        if (clickable && onClick) {
          onClick(e);
        }
      }}
      className={classNames({
        "trb-tag": true,
        "trb-selected-tag": selected,
        "trb-clickable-tag": clickable
      })}
      {...props}
    >
      {children}
    </span>
  );
};

export default TagRadioButton;
