import React from "react";
import { withRouter } from "react-router-dom";
import { connect } from "react-redux";
import queryString from "query-string";
import SignInForm from "../../components/AuthHandler/SignInForm/SignInForm";
import "./Login.sass";
import Page from "../../components/Page/Page";

const mapStateToProps = state => ({
  isSignedIn: state.auth.isSignedIn
});

function Login({ isSignedIn, history, location }) {
  if (isSignedIn) {
    const parsed = queryString.parse(location.search);
    if (parsed && parsed.bounce_to) {
      history.push(parsed.bounce_to);
    } else {
      history.push("/");
    }
  }

  return (
    <Page
      pageWrapperStyle={{ position: "relative" }}
      pageStyle={{ display: "flex", width: "100%", height: "100%" }}
    >
      <div style={{ margin: "auto", width: 400, padding: 50 }}>
        <SignInForm small />
      </div>
    </Page>
  );
}

export default withRouter(connect(mapStateToProps)(Login));
