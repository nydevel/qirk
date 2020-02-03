import React, { useEffect } from "react";
import { withRouter } from "react-router-dom";
import { connect } from "react-redux";
import queryString from "query-string";
import paths from "../../routes/paths";
import constants from "../../utils/constants";
import LoadingScreen from "../LoadingScreen/LoadingScreen";

function PrivateComponent({
  isSignedIn,
  authCheckInProgress,
  children,
  onLoginConfirmed,
  lastConfirmedSignedInPathname,
  history,
  location: { pathname }
}) {
  useEffect(() => {
    if (!isSignedIn && authCheckInProgress === false) {
      history.push(
        `${paths.Login.toPath()}?${queryString.stringify({
          bounce_to: pathname
        })}`
      );
    }
  }, [isSignedIn, authCheckInProgress]);

  useEffect(() => {
    if (
      isSignedIn &&
      !authCheckInProgress &&
      lastConfirmedSignedInPathname === pathname &&
      onLoginConfirmed
    ) {
      onLoginConfirmed();
    }
  }, [
    isSignedIn,
    authCheckInProgress,
    lastConfirmedSignedInPathname,
    pathname
  ]);

  if (
    authCheckInProgress ||
    !isSignedIn ||
    lastConfirmedSignedInPathname !== pathname
  ) {
    return <LoadingScreen />;
  }

  return children;
}

export default withRouter(
  connect(state => ({
    isSignedIn: state.auth.isSignedIn,
    authCheckInProgress:
      state.auth.checkAuthRequestStatus === constants.WAITING,
    lastConfirmedSignedInPathname: state.auth.lastConfirmedSignedInPathname
  }))(PrivateComponent)
);
