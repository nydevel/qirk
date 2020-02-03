import React, { useEffect } from "react";
import { withRouter } from "react-router-dom";
import { connect } from "react-redux";
import { checkAuthStatus } from "../../actions/authActions";
import LoadingScreen from "../LoadingScreen/LoadingScreen";
import "./AuthHandler.sass";
import constants from "../../utils/constants";

function AuthHandler({
  checkAuthStatus,
  authCheckInProgress,
  children,
  lastLoginCheckPathname,
  location: { pathname }
}) {
  useEffect(() => {
    checkAuthStatus(pathname);
  }, [pathname]);

  if (authCheckInProgress || lastLoginCheckPathname !== pathname) {
    return <LoadingScreen />;
  } else {
    return children;
  }
}

export default withRouter(
  connect(
    state => ({
      authCheckInProgress:
        state.auth.checkAuthRequestStatus === constants.WAITING,
      lastLoginCheckPathname: state.auth.lastLoginCheckPathname
    }),
    { checkAuthStatus }
  )(AuthHandler)
);
