import React, { useState } from "react";
import { t } from "i18next";
import { Link } from "react-router-dom";
import { withRouter } from "react-router-dom";
import qs from "query-string";
import { Card, CardContent, Typography } from "@material-ui/core";
import Page from "../../components/Page/Page";
import paths from "../../routes/paths";
import Button from "../../components/Button/Button";
import axios from "../../utils/axios";
import endpoints from "../../utils/endpoints";
import { responseIsStatusOk } from "../../utils/variousUtils";

const UpdatedLicense = ({ history, location: { search } }) => {
  const { bounce_to } = qs.parse(search);

  const [accepting, setAccepting] = useState(false);

  const accept = async () => {
    try {
      setAccepting(true);

      const response = await axios.post(
        endpoints.POST_ACCEPT_LICENSE,
        {},
        {
          headers: {
            "License-Accepted": true
          }
        }
      );

      if (responseIsStatusOk(response)) {
        history.push(bounce_to || paths.Home.toPath());
      }
    } catch (e) {
      console.error(e);
    } finally {
      setAccepting(false);
    }
  };

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
                {t("action_required")}
              </Typography>
            </div>
            <div style={{ paddingBottom: 21 }}>
              {t("we_updated")}{" "}
              <Link to={paths.LicenseContract.toPath()}>
                {t("license_contract")}
              </Link>{" "}
              {t("and")}{" "}
              <Link to={paths.PrivacyPolicy.toPath()}>
                {t("privacy_policy")}
              </Link>
              . {t("please_confirm_acceptance")}.
            </div>
            <form
              onSubmit={e => {
                e.preventDefault();
                accept();
              }}
            >
              <fieldset disabled={accepting}>
                <Button style={{ width: "100%" }} disabled={accepting}>
                  {t("accept")}
                </Button>
              </fieldset>
            </form>
          </CardContent>
        </Card>
      </div>
    </Page>
  );
};

export default withRouter(UpdatedLicense);
