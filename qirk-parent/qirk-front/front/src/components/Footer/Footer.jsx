import React from "react";
import { Link } from "react-router-dom";
import { t } from "i18next";
import Responsive from "react-responsive";
import paths from "../../routes/paths";
import "./Footer.sass";
import { connect } from "react-redux";

// TODO Denis uncomment when static pages are ready
const breakpointPx = 1250;
const PC = props => <Responsive {...props} minWidth={breakpointPx} />;
const Mobile = props => <Responsive {...props} maxWidth={breakpointPx - 1} />;

function Footer({ isSignedIn }) {
  return (
    <>
      <Mobile>
        <div
          style={{
            display: "flex",
            alignItems: "center"
          }}
        >
          <div style={{ flex: 1, display: "flex", padding: "16px 0" }}>
            <div
              style={{
                display: "flex",
                padding: 16,
                paddingTop: 0,
                paddingBottom: 0,
                paddingLeft: 0
              }}
            >
              <Link
                style={{ margin: "auto" }}
                to={paths.PrivacyPolicy.toPath()}
              >
                {t("privacy_policy")}
              </Link>
            </div>
            <div
              style={{
                display: "flex",
                padding: 16,
                paddingTop: 0,
                paddingBottom: 0,
                paddingLeft: 0
              }}
            >
              <Link
                style={{ margin: "auto" }}
                to={paths.LicenseContract.toPath()}
              >
                {t("license_contract")}
              </Link>
            </div>
            <div
              style={{
                display: "flex",
                padding: 16,
                paddingTop: 0,
                paddingBottom: 0,
                paddingLeft: 0
              }}
            >
              <Link style={{ margin: "auto" }} to={paths.ChangeLog.toPath()}>
                {t("changelog")}
              </Link>
            </div>
            {!isSignedIn && (
              <div
                style={{
                  display: "flex",
                  padding: 16,
                  paddingTop: 0,
                  paddingBottom: 0,
                  paddingLeft: 0
                }}
              >
                <Link
                  style={{ margin: "auto" }}
                  to={paths.FeebackForm.toPath()}
                >
                  {t("leave_feedback")}
                </Link>
              </div>
            )}
          </div>
        </div>
        <div style={{ paddingBottom: 16 }}>{t("footer_copyright")}</div>
      </Mobile>
      <PC>
        <div
          className="footer"
          style={{
            display: "flex"
          }}
        >
          <div style={{ flex: 1, display: "flex" }}>
            <div style={{ display: "flex", paddingRight: 32 }}>
              <Link style={{ margin: "auto" }} to={paths.ChangeLog.toPath()}>
                {t("changelog")}
              </Link>
            </div>
            <div style={{ display: "flex", paddingRight: 32 }}>
              <Link
                style={{ margin: "auto" }}
                to={paths.LicenseContract.toPath()}
              >
                {t("license_contract")}
              </Link>
            </div>
            <div style={{ display: "flex", paddingRight: 32 }}>
              <Link
                style={{ margin: "auto" }}
                to={paths.PrivacyPolicy.toPath()}
              >
                {t("privacy_policy")}
              </Link>
            </div>
            {!isSignedIn && (
              <div style={{ display: "flex", paddingRight: 32 }}>
                <Link
                  style={{ margin: "auto" }}
                  to={paths.FeebackForm.toPath()}
                >
                  {t("leave_feedback")}
                </Link>
              </div>
            )}
          </div>
          <div style={{ display: "flex" }}>
            <span style={{ margin: "auto" }}>{t("footer_copyright")}</span>
          </div>
        </div>
      </PC>
    </>
  );
}

export default connect(state => ({ isSignedIn: state.auth.isSignedIn }))(
  Footer
);
