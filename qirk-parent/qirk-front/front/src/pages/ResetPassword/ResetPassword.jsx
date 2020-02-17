import React, { useState } from "react";
import { connect } from "react-redux";
import { withRouter, Link } from "react-router-dom";

import {
  resetPassword,
  setNewPassWithToken
} from "./../../actions/authActions";
import "./ResetPassword.sass";
import constants from "../../utils/constants";
import paths from "./../../routes/paths";
import Page from "../../components/Page/Page";
import Button from "../../components/Button/Button";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Typography from "@material-ui/core/Typography";
import TextInput from "../../components/TextInput/TextInput";
import { useTranslation } from "react-i18next";

function ResetPassword(props) {
  const{t}=useTranslation()
  const getErrorMessage = () => {
    if (props.match.params.code && newPass && newPass2) {
      if (newPass !== newPass2) {
        return t("password_change.pass_missmatch_error_text");
      }
      return null;
    }

    switch (props.resetPasswordStatus) {
      case constants.EMAIL_NOT_FOUND:
        return t("email_not_found_error_message");
      case constants.EMAIL_NOT_DELIVERED:
        return t("email_not_delivered_error_message");
      case constants.FAILED:
        return t("Errors.Error");
      default:
        return null;
    }
  };

  const handleSubmitStep1 = () => {
    props.resetPassword(inputEmail);
  };

  const handleSubmitStep2 = () => {
    props.setNewPassWithToken(props.match.params.code, newPass);
  };

  const [inputEmail, setInputEmail] = useState("");
  const [newPass, setNewPass] = useState("");
  const [newPass2, setNewPass2] = useState("");

  return (
    <>
      <Page
        pageWrapperStyle={{ position: "relative" }}
        pageStyle={{ display: "flex", width: "100%", height: "100%" }}
      >
        <div style={{ margin: "auto", width: 400, padding: 50 }}>
          <Card>
            <CardContent>
              <Typography component="h5" variant="h5">
                {t("reset_password_label")}
              </Typography>
              {props.match.params.code &&
                props.resetPasswordStatus === constants.SUCCESS && (
                  <div style={{ paddingTop: 18 }}>
                    <p>{t("password_change_success_message")}</p>
                    <Link to={paths.Login.toPath()}>{t("Sign in")}</Link>
                  </div>
                )}
              {props.match.params.code &&
                props.resetPasswordStatus === constants.TOKEN_EXPIRED && (
                  <div style={{ paddingTop: 18 }}>
                    <div>
                      <p>{t("reset_pass_token_expired_message")}</p>
                      <Link to={paths.ResetPassword.toPath()}>
                        {t("reset_password_button")}
                      </Link>
                    </div>
                  </div>
                )}
              {props.match.params.code &&
                props.resetPasswordStatus !== constants.SUCCESS &&
                props.resetPasswordStatus !== constants.TOKEN_EXPIRED && (
                  <form
                    style={{ paddingTop: 18 }}
                    onSubmit={e => {
                      e.preventDefault();
                      handleSubmitStep2();
                    }}
                  >
                    <fieldset
                      disabled={props.resetPasswordStatus === constants.WAITING}
                    >
                      <label>
                        <span style={{ display: "block", marginBottom: 10 }}>
                          {t("password_change.new_password")}
                        </span>
                        <TextInput
                          type="password"
                          required
                          label={t("password_change.new_password")}
                          value={newPass}
                          onChange={e => setNewPass(e.target.value)}
                        />
                      </label>
                      <label>
                        <span style={{ display: "block", marginBottom: 10 }}>
                          {t("password_change.repeat_new_password")}
                        </span>
                        <TextInput
                          type="password"
                          required
                          errorText={getErrorMessage()}
                          label={t("password_change.repeat_new_password")}
                          value={newPass2}
                          onChange={e => setNewPass2(e.target.value)}
                        />
                      </label>
                      <div>
                        <Button disabled={getErrorMessage() !== null}>
                          {t("password_change.btn_apply")}
                        </Button>
                      </div>
                    </fieldset>
                  </form>
                )}

              {!props.match.params.code &&
                props.resetPasswordStatus === constants.EMAIL_DELIVERED && (
                  <div style={{ paddingTop: 18 }}>
                    {t("reset_pass_email_delivered_success_message")}
                  </div>
                )}
              {!props.match.params.code &&
                props.resetPasswordStatus !== constants.EMAIL_DELIVERED && (
                  <form
                    style={{ paddingTop: 18 }}
                    onSubmit={e => {
                      e.preventDefault();
                      handleSubmitStep1();
                    }}
                  >
                    <fieldset
                      disabled={props.resetPasswordStatus === constants.WAITING}
                    >
                      <TextInput
                        value={inputEmail}
                        onChange={e => setInputEmail(e.target.value)}
                        label={t("Email")}
                        type="email"
                        required
                        errorText={getErrorMessage()}
                      />
                      <div>
                        <Button>{t("reset_password_button")}</Button>
                      </div>
                    </fieldset>
                  </form>
                )}
            </CardContent>
          </Card>
          <div
            style={{
              display: "flex",
              "padding-top": "15px",
              "padding-right": "10px",
              "justify-content": "flex-end"
            }}
          >
            <Link to={paths.Login.toPath()}>
              <Button flat dense>
                {t("Sign In")}
              </Button>
            </Link>
          </div>
        </div>
      </Page>
    </>
  );
}

export default withRouter(
  connect(
    state => ({
      resetPasswordStatus: state.auth.resetPasswordStatus
    }),
    {
      resetPassword,
      setNewPassWithToken
    }
  )(ResetPassword)
);
