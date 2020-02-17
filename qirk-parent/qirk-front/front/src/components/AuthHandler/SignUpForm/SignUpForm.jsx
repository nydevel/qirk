import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import PropTypes from "prop-types";
import { FormControl, FormControlLabel, Checkbox } from "@material-ui/core";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Typography from "@material-ui/core/Typography";
import { connect } from "react-redux";
import LoadingScreen from "../../LoadingScreen/LoadingScreen";
import Button from "../../Button/Button";
import { resendRegistrationEmail } from "../../../actions/authActions";
import { emailSignUp } from "../../../actions/authActions";
import { setDefaultRegistrationStatusCode } from "../../../actions/authActions";
import axios from "../../../utils/axios";
import endpoints from "../../../utils/endpoints";
import {
  responseIsStatusOk,
  errorIsInvalid,
  regexMatch,
  errorIsInvalidEmail
} from "../../../utils/variousUtils";
import constants from "../../../utils/constants";
import TextInput from "../../TextInput/TextInput";
import paths from "../../../routes/paths";
import { useTranslation } from "react-i18next";

const mapStateToProps = state => ({
  registrationStatusCode: state.auth.registrationStatusCode
});

function SignUpForm({
  emailSignUp,
  registrationStatusCode,
  setDefaultRegistrationStatusCode,
  resendRegistrationEmail
}) {
  const { t } = useTranslation();

  const [agree, setAgree] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [email, setEmail] = useState("");
  const [emailTypingTimeout, setEmailTypingTimeout] = useState(null);
  const [emailError, setEmailError] = useState("");
  const [commonError, setCommonError] = useState("");
  useEffect(() => {
    if (registrationStatusCode === "EMAIL_IN_USE") {
      setEmailError(t("Errors.EmailTaken"));
    } else if (registrationStatusCode === "FAILED") {
      setCommonError(t("Errors.Error"));
    }
  }, [registrationStatusCode]);

  useEffect(() => {
    setDefaultRegistrationStatusCode();
    setIsLoading(false);
  }, []);

  const onSignUp = e => {
    e.preventDefault();

    if (!emailError) {
      emailSignUp({ email });
    }
  };

  const checkEmail = email => {
    setEmail(email);
    if (emailTypingTimeout) {
      clearTimeout(emailTypingTimeout);
    }
    const lcEmail = email.toLowerCase();
    setEmailTypingTimeout(
      setTimeout(async () => {
        try {
          if (regexMatch(lcEmail, constants.REGEX_EMAIL)) {
            const response = await axios.get(
              `${endpoints.CHECK_EMAIL_AVAILABILITY}?email=${email}`
            );
            if (
              responseIsStatusOk(response) &&
              response.data.data &&
              response.data.data.length > 0 &&
              response.data.data[0].exists
            ) {
              setEmailError(t("Errors.EmailTaken"));
            } else {
              setEmailError("");
            }
          } else {
            setEmailError(t("Errors.InvalidEmail"));
          }
        } catch (error) {
          if (errorIsInvalid(error)) {
            setEmailError(t("Errors.InvalidEmail"));
          } else if (errorIsInvalidEmail(error)) {
            setEmailError(t("Errors.InvalidEmail"));
          }
        } finally {
          setEmailTypingTimeout(null);
        }
      }, 500)
    );
  };

  if (isLoading) {
    return <LoadingScreen />;
  }

  if (registrationStatusCode === constants.EMAIL_NOT_DELIVERED) {
    return (
      <div>
        <p>{t("AccountRegistration.EmailNotDelivered")}</p>
        <Button asLink onClick={() => resendRegistrationEmail()}>
          {t("AccountRegistration.ResendButton")}
        </Button>
      </div>
    );
  }

  if (registrationStatusCode === "PENDING") {
    return (
      <Card style={{ padding: 20 }}>
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center"
          }}
        >
          {t("AccountRegistration.FirstLine")}
          <br />
          {t("AccountRegistration.SecondLine")}{" "}
          <Button
            style={{ marginTop: 10 }}
            asLink
            onClick={() => resendRegistrationEmail()}
          >
            {t("AccountRegistration.ResendButton")}
          </Button>
        </div>
      </Card>
    );
  }

  const isWaiting = registrationStatusCode === "LOADING";

  return (
    <Card>
      <CardContent>
        <Typography component="h5" variant="h5">
          {t("Sign Up")}
        </Typography>
        <form style={{ paddingTop: 18 }} onSubmit={e => onSignUp(e)}>
          <fieldset disabled={isWaiting}>
            <TextInput
              label="Email"
              id="email"
              autoComplete="new-email"
              type="email"
              value={email}
              errorText={emailError}
              hideErrorArea={!emailError}
              required
              onChange={e => checkEmail(e.target.value)}
            />

            <FormControl
              style={{ marginBottom: "8px", marginTop: emailError ? 0 : 8 }}
            >
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
                    <div style={{ fontSize: 13 }}>
                      <span>{t("agree_with")} </span>
                      <Link to={paths.LicenseContract.toPath()}>
                        {t("license_contract")}
                      </Link>
                      <span> {t("and")} </span>
                      <Link to={paths.PrivacyPolicy.toPath()}>
                        {t("privacy_policy")}
                      </Link>
                    </div>
                  </>
                }
              />
            </FormControl>

            <Button
              type="submit"
              className="auth-form__form-button"
              isLoading={isWaiting}
              style={{ color: "white" }}
              disabled={emailTypingTimeout || !agree}
            >
              {t("Sign up")}
            </Button>
            {commonError && (
              <div className="auth-form__error-message">{commonError}</div>
            )}
          </fieldset>
        </form>
      </CardContent>
    </Card>
  );
}

SignUpForm.propTypes = {
  small: PropTypes.bool,
  emailSignUp: PropTypes.func.isRequired
};

export default connect(mapStateToProps, {
  emailSignUp,
  resendRegistrationEmail,
  setDefaultRegistrationStatusCode
})(SignUpForm);
