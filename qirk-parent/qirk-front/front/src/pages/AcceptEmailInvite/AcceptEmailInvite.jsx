import React from "react";
import { withRouter } from "react-router-dom";
import { t } from "i18next";
import { Card, CardContent, Typography } from "@material-ui/core";
import Page from "../../components/Page/Page";
import ConfirmRegistrationForm from "../../components/ConfirmRegistrationForm/ConfirmRegistrationForm";

function AcceptEmailInvite() {
  return (
    <Page
      pageWrapperStyle={{ position: "relative" }}
      pageStyle={{ display: "flex", width: "100%", height: "100%" }}
    >
      <div style={{ margin: "auto", width: 400, padding: 50 }}>
        <Card>
          <CardContent>
            <Typography component="h5" variant="h5">
              {t("accepting_invite")}
            </Typography>
            <div style={{ paddingTop: 16, paddingBottom: 24 }}>
              {t("please_create_username_and_type_in_your_full_name")}
            </div>
            <ConfirmRegistrationForm />
          </CardContent>
        </Card>
      </div>
    </Page>
  );
}

export default withRouter(AcceptEmailInvite);
