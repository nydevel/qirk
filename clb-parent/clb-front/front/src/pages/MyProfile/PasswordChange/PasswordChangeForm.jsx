import React, { useState, useEffect } from "react";
import { connect } from "react-redux";
import Button from "../../../components/Button/Button";
import { t } from "i18next";
import TextInput from "../../../components/TextInput/TextInput";
import { changePassword } from "../../../actions/authActions";

function PasswordChangeForm(props) {
  const [currentPass, setCurrentPass] = useState("");
  const [newPassOne, setNewPassOne] = useState("");
  const [newPassTwo, setNewPassTwo] = useState("");

  const [inputErrors, setInputErrors] = useState({
    newPassOne: "",
    newPassTwo: "",
    currentPass: ""
  });

  let hasErrors = false;

  const clearPassInputs = () => {
    setCurrentPass("");
    setNewPassOne("");
    setNewPassTwo("");
  };

  const checkAndSetErrors = () => {
    let errorTexts = { newPassOne: "", newPassTwo: "", currentPass: "" };

    if (newPassOne !== newPassTwo) {
      errorTexts.newPassOne = errorTexts.newPassTwo = t(
        "password_change.pass_missmatch_error_text"
      );
      hasErrors = true;
    }

    if (newPassOne === "") {
      errorTexts.newPassOne = t("password_change.field_should_not_be_empty");
      hasErrors = true;
    }

    if (newPassTwo === "") {
      errorTexts.newPassTwo = t("password_change.field_should_not_be_empty");
      hasErrors = true;
    }

    if (!currentPass) {
      errorTexts.currentPass = t("password_change.field_should_not_be_empty");
      hasErrors = true;
    }

    setInputErrors(errorTexts);
  };

  const isZeroInputErrors = () => {
    return !hasErrors;
  };

  const changePassword = async () => {
    await props.changePassword(newPassOne, currentPass);
  };

  const updatePassOrPrintErrors = e => {
    e.preventDefault();
    checkAndSetErrors();
    if (isZeroInputErrors()) {
      changePassword();
      clearPassInputs();
    }
  };

  useEffect(() => {
    if (props.passwordChangeStatusCode === "FAILED") {
      setInputErrors({
        ...inputErrors,
        currentPass: t("password_change.wrong_current_password")
      });
    }
  }, [props.passwordChangeStatusCode]);

  return (
    <div>
      <h1>{t("password_change.change_password")}</h1>
      <form
        onSubmit={updatePassOrPrintErrors}
        className="password_change.profile__column"
      >
        <fieldset disabled={props.passwordChangeStatusCode === "WAITING"}>
          <TextInput
            id="new_password"
            type="password"
            value={newPassOne}
            errorText={inputErrors["newPassOne"]}
            label={t("password_change.new_password")}
            onChange={e => setNewPassOne(e.target.value)}
          />
          <TextInput
            id="new_password_repeat"
            type="password"
            value={newPassTwo}
            errorText={inputErrors["newPassTwo"]}
            label={t("password_change.repeat_new_password")}
            onChange={e => setNewPassTwo(e.target.value)}
          />
          <TextInput
            id="current_password"
            type="password"
            value={currentPass}
            errorText={inputErrors["currentPass"]}
            label={t("password_change.current_password")}
            onChange={e => setCurrentPass(e.target.value)}
          />
          <Button
            className="profile__update-button"
            isLoading={props.isWaiting}
          >
            {t("Button.Update")}
          </Button>
        </fieldset>
      </form>
    </div>
  );
}

const mapStateToProps = state => ({
  passwordChangeStatusCode: state.auth.passwordChangeStatusCode
});

export default connect(
  mapStateToProps,
  { changePassword }
)(PasswordChangeForm);
