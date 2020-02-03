import React from "react";
import { withRouter, Link } from "react-router-dom";
import "./AllProjectTasks.sass";
import AllProjectTaskItem from "./AllProjectTasksItem/AllProjectTaskItem";

function AllProjectTasks({ data, ...props }) {
  if (!data || data.length === 0) {
    return null;
  }

  return (
    <div className="all-project-tasks-content" {...props}>
      {data.map((itemData, index) => (
        <Link
          className="all-project-tasks-content__item-link"
          key={index}
          to={itemData.link}
        >
          <AllProjectTaskItem key={index} {...itemData} />
        </Link>
      ))}
    </div>
  );
}

export default withRouter(AllProjectTasks);
