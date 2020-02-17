import React from "react";
import Page from "../../components/Page/Page";
import { useTranslation } from "react-i18next";

function Error() {
  const{t}=useTranslation()
  return (
    <Page>
      <h1>{`${t("something_went_wrong")} ...`}</h1>
    </Page>
  );
}

export default Error;
