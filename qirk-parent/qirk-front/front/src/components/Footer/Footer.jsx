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
        ></div>
        <div style={{ paddingBottom: 16 }}>{t("footer_copyright")}</div>
      </Mobile>
      <PC>
        <div
          className="footer"
          style={{
            display: "flex"
          }}
        >
          <div style={{ flex: 1, display: "flex" }}></div>
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
