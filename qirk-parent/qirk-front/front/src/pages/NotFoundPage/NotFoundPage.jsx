import React from "react";
import "./NotFoundPage.sass";
import Page from "../../components/Page/Page";
import { useTranslation } from "react-i18next";

function NotFoundPage() {const{t}=useTranslation()
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
