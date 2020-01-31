import React, { useEffect } from "react";
import SignUpForm from "../../components/AuthHandler/SignUpForm/SignUpForm";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { t } from "i18next";
import { activateAccount } from "../../actions/authActions";
import "./Join.sass";
import { toast } from "react-toastify";
import Page from "../../components/Page/Page";

const mapStateToProps = state => ({
  isSignedIn: state.auth.isSignedIn,
  registrationStatusCode: state.auth.registrationStatusCode,
  activationStatusCode: state.auth.activationStatusCode
});

function Join({
  activationStatusCode,
  activateAccount,
  isSignedIn,
  history,
  match
}) {
  useEffect(() => {
    if (activationStatusCode === "SUCCESS") {
      toast.success(t("AccountRegistration.AccountActivated"));
      history.push("/");
    } else if (activationStatusCode === "ALREADY_ACTIVATED") {
      history.push("/");
    } else if (activationStatusCode === "ERROR") {
      toast.error(t("Errors.Error"));
      history.push("/");
    }
  }, [activationStatusCode]);
  useEffect(() => {
    if (isSignedIn) {
      history.push("/");
    } else {
      tryActivateAccount();
    }
  }, []);

  const isConfirmingAccount =
    match.params.page === "activate" && match.params.hash;

  const tryActivateAccount = async () => {
    if (isConfirmingAccount) {
      activateAccount(match.params.hash);
    }
  };

  return (
    <Page
      pageWrapperStyle={{ position: "relative" }}
      pageStyle={{ display: "flex", width: "100%", height: "100%" }}
    >
      <div style={{ margin: "auto", width: 400, padding: 50 }}>
        <SignUpForm small />
      </div>
    </Page>
  );
}

export default withRouter(
  connect(
    mapStateToProps,
    { activateAccount }
  )(Join)
);
