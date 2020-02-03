import React from "react";
import { connect } from "react-redux";
import { t } from "i18next";
import { Cell, Grid, Row } from "@material/react-layout-grid";
import Responsive from "react-responsive";
import Page from "../../components/Page/Page";
import SignUpForm from "../../components/AuthHandler/SignUpForm/SignUpForm";
import "./Home.sass";
import Footer from "../../components/Footer/Footer";
import DefaultHomePage from "../../pages/DefaultHomePage/DefaultHomePage";

const breakpointCollapseMenuPx = 1020;

const PC = props => (
  <Responsive {...props} minWidth={breakpointCollapseMenuPx} />
);

const Mobile = props => (
  <Responsive {...props} maxWidth={breakpointCollapseMenuPx - 1} />
);

const mapStateToProps = state => ({
  isSignedIn: state.auth.isSignedIn
});

function Home({ isSignedIn }) {
  if (isSignedIn) {
    return <DefaultHomePage />;
  } else {
    return (
      <>
        <Mobile>
          <Page
            pageWrapperStyle={{ position: "relative" }}
            className="main-landing-page"
            pageStyle={{
              display: "flex",
              width: "100%",
              height: "100%",
              padding: 10
            }}
            noFooter={true}
          >
            <div style={{ width: "100%" }}>
              <div className="main-text-on-home-page">
                <h4
                  style={{
                    fontSize: "65px",
                    textTransform: "uppercase",
                    lineHeight: "60px",
                    margin: 0,
                    padding: 0,
                    paddingTop: 40,
                    color: "#3c3c3c"
                  }}
                >
                  {t("free")}
                </h4>
                <div
                  style={{
                    fontSize: "50px",
                    textTransform: "uppercase",
                    lineHeight: "60px",
                    margin: 0,
                    padding: 0,
                    paddingBottom: 40,
                    paddingTop: "10px",
                    color: "#3c3c3c"
                  }}
                >
                  <p style={{ wordBreak: "break-word" }}>
                    {t("task_tracking")} {t("and_project_lc")}{" "}
                    {t("management_lc")}
                  </p>
                </div>
              </div>
              <div className="sign-up-form-in-home-page">
                <SignUpForm small />
              </div>
              <div
                className="mobile-features-block"
                style={{ background: "#6200ee" }}
              >
                <div style={{ paddingBottom: 16 }}>
                  <h4
                    style={{
                      fontSize: "40px",
                      textTransform: "uppercase",
                      lineHeight: "60px",
                      margin: 0,
                      padding: "45px 0px 20px 0px"
                    }}
                  >
                    {t("free_and_unlimited")}
                  </h4>
                  <div style={{ fontSize: "20px" }} className="feature-list">
                    <p>- {t("for_public_projects")}</p>
                  </div>
                </div>
                <div>
                  <h4
                    style={{
                      fontSize: "40px",
                      textTransform: "uppercase",
                      lineHeight: "60px",
                      margin: 0,
                      padding: "45px 0px 20px 0px"
                    }}
                  >
                    {t("private_projects")}
                  </h4>
                  <div style={{ fontSize: "20px" }} className="feature-list">
                    <p>- {t("up_to_5_users")}</p>
                    <p>- {t("6_users")}</p>
                    <p style={{ fontSize: 11, paddingTop: 0 }}>
                      {t("*_decryption")}
                    </p>
                  </div>
                </div>
              </div>
              <div className="default-footer">
                <Footer />
              </div>
            </div>
          </Page>
        </Mobile>
        <PC>
          <Page
            pageWrapperStyle={{ position: "relative" }}
            className="main-landing-page"
            pageStyle={{
              display: "flex",
              width: "100%",
              height: "100%"
            }}
            noFooter={true}
          >
            <div style={{ width: "100%" }}>
              <Grid style={{ width: "100%", padding: 30 }}>
                <Row className="first-row" style={{ minWidth: 975 }}>
                  <Cell columns={1}></Cell>
                  <Cell className="main-text-on-home-page" columns={7}>
                    <h4
                      style={{
                        fontSize: "100px",
                        textTransform: "uppercase",
                        lineHeight: "72px",
                        margin: 0,
                        padding: 0,
                        color: "#3c3c3c"
                      }}
                    >
                      {t("free")}
                    </h4>
                    <div
                      style={{
                        fontSize: "75px",
                        textTransform: "uppercase",
                        lineHeight: "75px",
                        margin: 0,
                        padding: 0,
                        paddingTop: "10px",
                        color: "#3c3c3c"
                      }}
                    >
                      <p>{t("task_tracking")}</p>
                      <p>{t("and_project_lc")}</p>
                      <p>{t("management_lc")}</p>
                    </div>
                  </Cell>
                  <Cell columns={3}>
                    <div className="sign-up-form-in-home-page">
                      <SignUpForm small />
                    </div>
                  </Cell>
                  <Cell columns={1}></Cell>
                </Row>
              </Grid>
              <Grid style={{ width: "100%", padding: 30 }}>
                <Row className="first-row">
                  <Cell columns={12}>
                    <div className="default-footer">
                      <Footer />
                    </div>
                  </Cell>
                </Row>
              </Grid>
            </div>
          </Page>
        </PC>
      </>
    );
  }
}

export default connect(mapStateToProps)(Home);
