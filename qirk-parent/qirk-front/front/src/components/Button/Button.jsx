import React from "react";
import PropTypes from "prop-types";
import Button from "@material/react-button";

import "@material/react-button/dist/button.css";
import "./Button.sass";

function Button1({
  asLink = false,
  className = "",
  onClick = () => {},
  children = null,
  secondary = false,
  isLoading = false,
  disabled = false,
  flat = false,
  style = {},
  ...props
}) {
  return (
    <Button
      raised={!flat}
      dense={!flat}
      {...props}
      disabled={disabled || isLoading}
      className={`${asLink ? "button--as-link" : "button"} ${className} ${
        secondary ? "button--secondary" : ""
      } ${disabled ? "button--disabled" : ""}`}
      onClick={onClick}
      style={style}
    >
      {children}
    </Button>
  );
}

Button1.propTypes = {
  onClick: PropTypes.func,
  className: PropTypes.string,
  asLink: PropTypes.bool,
  children: PropTypes.any.isRequired,
  secondary: PropTypes.bool,
  isLoading: PropTypes.bool
};

export default Button1;
