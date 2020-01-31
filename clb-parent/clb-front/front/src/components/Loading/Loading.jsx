import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import CircularProgress from "@material-ui/core/CircularProgress";
import "./Loading.sass";

const useStyles = makeStyles(() => ({
  progress: {
    color: "#6200ee"
  }
}));

export default function Loading({ style, ...props }) {
  const classes = useStyles();

  return (
    <div className="loading-wrapper" style={style}>
      <CircularProgress className={classes.progress} {...props} />
    </div>
  );
}
