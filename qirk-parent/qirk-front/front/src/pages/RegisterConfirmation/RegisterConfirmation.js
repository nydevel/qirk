import React from "react";
import { Link } from "react-router-dom";
import { Card, CardContent, Typography } from "@material-ui/core";
import ConfirmRegistrationForm from "../../components/ConfirmRegistrationForm/ConfirmRegistrationForm";
import Page from "../../components/Page/Page";
import paths from "../../routes/paths";
import Button from "../../components/Button/Button";
import { useTranslation } from "react-i18next";

const RegisterConfirmation = () => {
  const { t } = useTranslation();
  return (
    <Page
      pageWrapperStyle={{ position: "relative" }}
      pageStyle={{ display: "flex", width: "100%", height: "100%" }}
    >
      <div style={{ margin: "auto", width: 400, padding: 50 }}>
        <Card>
          <CardContent>
            <div style={{ paddingBottom: 16 }}>
              <Typography component="h5" variant="h5">
                {t("final_registration_step")}
              </Typography>
            </div>
            <div style={{ paddingBottom: 21 }}>
              {t("final_registration_step_message")}
            </div>
            <ConfirmRegistrationForm />
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
  );
};

export default RegisterConfirmation;
