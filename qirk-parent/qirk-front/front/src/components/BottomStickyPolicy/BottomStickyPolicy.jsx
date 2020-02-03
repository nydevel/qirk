import React, { useState, useEffect } from "react";
import { t } from "i18next";
import { Link } from "react-router-dom";
import constants from "../../utils/constants";
import "./BottomStickyPolicy.sass";
import paths from "../../routes/paths";
import { Button } from "@material-ui/core";

function BottomStickyPolicy() {
  const lsCookiesAccepted = localStorage[constants.LS_COOKIES_ACCEPTED];

  const [cookiesAccepted, setCookiesAccepted] = useState(lsCookiesAccepted);

  useEffect(() => {
    setCookiesAccepted(lsCookiesAccepted);
  }, [lsCookiesAccepted]);

  return (
    <>
      {!cookiesAccepted && (
        <div className="bottom-sticky-policy">
          <span>{t("bottom_policy_text")} </span>
          <span style={{ marginRight: 10 }}>
            <Link to={paths.PrivacyPolicy.toPath()}>{t("privacy_policy")}</Link>
          </span>

          <Button
            onClick={() => {
              localStorage[constants.LS_COOKIES_ACCEPTED] = true;
              setCookiesAccepted(true);
            }}
          >
            {t("ok")}
          </Button>
        </div>
      )}
    </>
  );
}

export default BottomStickyPolicy;
