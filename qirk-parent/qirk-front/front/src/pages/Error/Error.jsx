import React from "react";
import { t } from "i18next";
import Page from "../../components/Page/Page";

function Error() {
  return (
    <Page>
      <h1>{`${t("something_went_wrong")} ...`}</h1>
    </Page>
  );
}

export default Error;
