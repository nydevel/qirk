import React from "react";
import { t } from "i18next";
import "./NotFoundPage.sass";
import Page from "../../components/Page/Page";

function NotFoundPage() {
  return (
    <Page className="not-found-page">
      <div className="not-found-page__text">
        <div className="not-found-page__text_back">{t("Not found")}</div>
        <div className="not-found-page__text_front">404</div>
      </div>
    </Page>
  );
}

export default NotFoundPage;
