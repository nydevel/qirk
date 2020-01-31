import React from "react";
import "./ListItem.sass";

function ListItem({ children, className }) {
  return (
    <div className={`list-item ${className}`}>
      {children &&
        React.Children.toArray(children)
          .filter(child => child)
          .map((child, index) => <div key={index}>{child}</div>)}
    </div>
  );
}

export default ListItem;
