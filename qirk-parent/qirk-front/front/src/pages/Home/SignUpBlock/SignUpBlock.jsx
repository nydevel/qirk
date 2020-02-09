import React from "react";
import { withRouter } from "react-router-dom";
import { connect } from "react-redux";
import { emailSignUp } from "../../../actions/authActions";
import SignUpForm from "../../../components/AuthHandler/SignUpForm/SignUpForm";
import "./SignUpBlock.sass";

const mapStateToProps = state => {
  return {
    isSignedIn: state.auth.isSignedIn,
    predefinedOrganizationId: state.user.predefined_organization
  };
};

function SignUpBlock({ isSignedIn }) {
  return (
    <div className="sign-up-block">
      <div className="sign-up-block__registration">
        {!isSignedIn && (
          <div className="sign-up-block__form">
            <SignUpForm />
          </div>
        )}
      </div>
    </div>
  );
}

export default withRouter(
  connect(
    mapStateToProps,
    { emailSignUp }
  )(SignUpBlock)
);
