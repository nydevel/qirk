import React from "react";
import { Link } from "react-router-dom";

function Crumb(props) {
  return (
    <span>
      {props.hasNext && props.url ? (
        <Link to={props.url}>{props.name}</Link>
      ) : (
        <span>{props.name}</span>
      )}
      {props.hasNext && ` > `}
    </span>
  );
}

export default Crumb;
