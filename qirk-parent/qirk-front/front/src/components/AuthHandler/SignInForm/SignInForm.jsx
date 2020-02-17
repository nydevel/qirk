import React, { useState, useEffect } from "react";
import { withRouter } from "react-router-dom";
import { Link } from "react-router-dom";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import classNames from "classnames";
import MButton from "@material-ui/core/Button";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Typography from "@material-ui/core/Typography";
import { emailSignIn } from "../../../actions/authActions";
import Button from "../../Button/Button";
import LoadingScreen from "../../LoadingScreen/LoadingScreen";
import {
  setDefaultLoginStatusCode,
  setResetPasswordStatusDispatch
} from "../../../actions/authActions";
import paths from "./../../../routes/paths";
import constants from "../../../utils/constants";
import TextInput from "../../TextInput/TextInput";
import { toast } from "react-toastify";
import endpoints from "../../../utils/endpoints";
import {
  responseIsEmailSent,
  responseIsStatusOk
} from "../../../utils/variousUtils";
import axios from "../../../utils/axios";
import { FormControl, FormControlLabel, Checkbox } from "@material-ui/core";
import { useTranslation } from "react-i18next";

const mapStateToProps = state => ({
  isSignedIn: state.auth.isSignedIn,
  loginStatusCode: state.auth.loginStatusCode
});

function SignInForm({
  emailSignIn,
  setDefaultLoginStatusCode,
  isSignedIn,
  loginStatusCode,
  setResetPasswordStatusDispatch
}) {
  const { t } = useTranslation();

  const [resendingEmailInProgress, setResendingEmailInProgress] = useState(
    false
  );
  const [token, setToken] = useState(null); //recaptha token
  const [grecaptchaActive, setGrecaptchaActive] = useState(false); //when occurred error TooManyLoginAttempts
  const [agree, setAgree] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [commonError, setCommonError] = useState("");
  const [loadingScreen] = useState({
    isLoading: false,
    message: ""
  });

  const resendEmail = async () => {
    if (resendingEmailInProgress) {
      return toast.error(t("this_request_is_already_in_progress"));
    }

    try {
      setResendingEmailInProgress(true);

      const response = await axios.post(endpoints.POST_USER_RESEND_EMAIL, {
        email
      });

      if (responseIsStatusOk(response)) {
        if (responseIsEmailSent(response)) {
          toast.success(t("email_sent"));
        } else {
          toast.error(t("email_not_delivered_error_message"));
        }
      } else {
        toast.error(t("Errors.Error"));
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setResendingEmailInProgress(false);
    }
  };

  const checkForGrecaptha = () => {
    if (!grecaptchaActive && !token) {
      window.grecaptcha.ready(function() {
        window.grecaptcha
          .execute(process.env.REACT_APP_SITE_KEY_FOR_GRECAPTCHA, {
            action: "homepage"
          })
          .then(function(token) {
            setToken(token);
            setGrecaptchaActive(true);
          });
      });
    } else {
      window.grecaptcha
        .execute(process.env.REACT_APP_SITE_KEY_FOR_GRECAPTCHA, {
          action: "homepage"
        })
        .then(function(token) {
          setToken(token);
        });
    }
  };

  useEffect(() => {
    if (loginStatusCode === "ACCOUNT_DISABLED") {
      setCommonError("");
    }
  }, [email]);

  useEffect(() => {
    if (loginStatusCode === "LICENSE_NOT_ACCEPTED") {
      setCommonError(
        "Please accept updated license contract and privacy policy."
      );
    } else if (loginStatusCode === "INVALID_CREDENTIALS") {
      grecaptchaActive && checkForGrecaptha();
      setCommonError(t("Errors.WrongCombination"));
    } else if (loginStatusCode === "FAILED") {
      setCommonError(t("Errors.Error"));
    } else if (loginStatusCode === "ACCOUNT_DISABLED") {
      setCommonError(
        <>
          <div style={{ paddingBottom: 8 }}>
            {t("not_yet_confirmed_account_message")}
          </div>
          <div style={{ paddingBottom: 8 }}>
            {t("didnt_receive_email_question")}
          </div>
          <div style={{ paddingTop: 10 }}>
            <MButton
              size="small"
              style={{ color: "#6200ee" }}
              type="button"
              onClick={() => resendEmail()}
              disabled={resendingEmailInProgress}
            >
              {t("send_again")}
            </MButton>
          </div>
        </>
      );
    } else if (
      loginStatusCode === "TOO_MANY_LOGIN_ATTEMPTS" ||
      loginStatusCode === "INVALID_RECAPTHA"
    ) {
      checkForGrecaptha();
      setCommonError("Too many login attempts");
    }
  }, [loginStatusCode]);

  useEffect(() => {
    if (isSignedIn && loginStatusCode === "SUCCESS") {
      setDefaultLoginStatusCode();
    }
  }, [isSignedIn, loginStatusCode]);

  const onSignIn = e => {
    e.preventDefault();
    emailSignIn(email, password, agree, token);
  };

  const isWaiting = loginStatusCode === "LOADING";

  if (loadingScreen.isLoading) {
    return <LoadingScreen message={loadingScreen.message} />;
  }

  return (
    <>
      <Card>
        <CardContent>
          <Typography component="h5" variant="h5">
            {t("Sign In")}
          </Typography>
          <form
            className="qa_sign_in_form"
            style={{ paddingTop: 18 }}
            onSubmit={e => onSignIn(e)}
          >
            <fieldset disabled={isWaiting || resendingEmailInProgress}>
              <TextInput
                label="Email"
                id="email"
                required
                inputClassName="qa_sign_in_form_email_input"
                value={email}
                onChange={e => setEmail(e.target.value)}
              />
              <TextInput
                label={t("Password")}
                id="password"
                type="password"
                inputClassName="qa_sign_in_form_password_input"
                value={password}
                required
                onChange={e => setPassword(e.target.value)}
                //hideErrorArea={true}
              />
              {/* <div className="google-info__recaptcha">
                This site is protected by reCAPTCHA and the Google{" "}
                <a href="https://policies.google.com/privacy">Privacy Policy</a>{" "}
                and{" "}
                <a href="https://policies.google.com/terms">Terms of Service</a>{" "}
                apply.
              </div> */}

              {loginStatusCode === "LICENSE_NOT_ACCEPTED" && (
                <FormControl style={{ marginBottom: 16 }}>
                  <FormControlLabel
                    control={
                      <Checkbox
                        checked={agree}
                        onChange={e => setAgree(e.target.checked)}
                        id="agree_with_license_and_privacy_policy"
                        color="primary"
                      />
                    }
                    label={
                      <>
                        <span>{t("agree_with")} </span>
                        <Link to={paths.LicenseContract.toPath()}>
                          {t("license_contract")}
                        </Link>
                        <span> {t("and")} </span>
                        <Link to={paths.PrivacyPolicy.toPath()}>
                          {t("privacy_policy")}
                        </Link>
                      </>
                    }
                  />
                </FormControl>
              )}

              <Button
                type="submit"
                className={classNames({
                  "auth-form__form-button": true,
                  qa_sign_in_form_sign_in_btn: true
                })}
                isLoading={isWaiting}
              >
                {t("Sign In")}
              </Button>
              {commonError && (
                <div
                  className={classNames({
                    "auth-form__error-message": true,
                    qa_sign_in_form_error: true
                  })}
                >
                  {commonError}
                </div>
              )}
            </fieldset>
          </form>
        </CardContent>
      </Card>
      <div
        style={{
          display: "flex",
          paddingTop: "15px",
          paddingRight: "10px",
          justifyContent: "flex-end"
        }}
      >
        <Link
          onClick={() => {
            setResetPasswordStatusDispatch(constants.NOT_REQUESTED);
          }}
          to={paths.ResetPassword.toPath()}
        >
          <Button flat dense>
            {t("reset_pass_link_name")}
          </Button>
        </Link>
      </div>
    </>
  );
}

SignInForm.propTypes = {
  small: PropTypes.bool,
  failedLoginMessage: PropTypes.string
};

export default withRouter(
  connect(mapStateToProps, {
    emailSignIn,
    setDefaultLoginStatusCode,
    setResetPasswordStatusDispatch
  })(SignInForm)
);
