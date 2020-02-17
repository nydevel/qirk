import React, { useEffect } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import Breadcrumbs from "../BreadCrumbs/Breadcrumbs.jsx";
import Footer from "../Footer/Footer";
import LoadingScreen from "../LoadingScreen/LoadingScreen.jsx";
import "./Page.sass";
import constants from "../../utils/constants";

function Page({
  onLoginCheckFinished,
  className = "",
  lastLoginCheckPathname,
  authCheckInProgress,
  children,
  pageWrapperStyle,
  pageStyle,
  noFooter = false,
  location: { pathname }
}) {
  useEffect(() => {
    if (
      !authCheckInProgress &&
      lastLoginCheckPathname === pathname &&
      onLoginCheckFinished
    ) {
      onLoginCheckFinished();
    }
  }, [authCheckInProgress, lastLoginCheckPathname, pathname]);

  if (lastLoginCheckPathname === pathname) {
    return (
      <>
        <div style={pageWrapperStyle} className="page-wrapper">
          <Breadcrumbs />
          <div style={pageStyle} className={`page ${className}`}>
            {children}
          </div>
        </div>
        {!noFooter && <Footer />}
      </>
    );
  } else {
    return <LoadingScreen />;
  }
}

export default withRouter(
  connect(state => ({
    lastLoginCheckPathname: state.auth.lastLoginCheckPathname,
    authCheckInProgress: state.auth.checkAuthRequestStatus === constants.WAITING
  }))(Page)
);
