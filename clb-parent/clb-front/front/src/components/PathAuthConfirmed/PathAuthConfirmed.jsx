import React from "react";
import { withRouter } from "react-router-dom";
import { connect } from "react-redux";
import constants from "../../utils/constants";

function PathAuthConfirmed({
  isSignedIn,
  authCheckInProgress,
  children,
  lastConfirmedSignedInPathname,
  location: { pathname }
}) {
  if (
    authCheckInProgress ||
    !isSignedIn ||
    lastConfirmedSignedInPathname !== pathname
  ) {
    return null;
  }

  return children;
}

export default withRouter(
  connect(state => ({
    isSignedIn: state.auth.isSignedIn,
    authCheckInProgress:
      state.auth.checkAuthRequestStatus === constants.WAITING,
    lastConfirmedSignedInPathname: state.auth.lastConfirmedSignedInPathname
  }))(PathAuthConfirmed)
);
