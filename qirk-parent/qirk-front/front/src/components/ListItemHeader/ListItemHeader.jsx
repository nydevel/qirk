import React from "react";
import PropTypes from "prop-types";
import "./ListItemHeader.sass";

function ListItemHeader({ fields, className }) {
  return (
    <div className={`list-item-header ${className}`}>
      {fields && fields.map((field, index) => <div key={index}>{field}</div>)}
    </div>
  );
}

ListItemHeader.propTypes = {
  fields: PropTypes.array.isRequired,
  className: PropTypes.string
};

export default ListItemHeader;
