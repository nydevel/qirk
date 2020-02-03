import "normalize.css";
import React from "react";
import { withRouter } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Routes from "../routes/routes";
import AuthHandler from "./AuthHandler/AuthHandler";
import "../i18n";
import BottomStickyPolicy from "./BottomStickyPolicy/BottomStickyPolicy";
import NotificationSocketController from "./NotificationSocketController/NotificationSocketController";
import Dashboard from "./Dashboard/Dashboard";
import TopAppBar from "@material/react-top-app-bar";
import HeaderMaterial from "./HeaderMaterial/HeaderMaterial";
import "@material/react-top-app-bar/dist/top-app-bar.css";
import "@material/react-material-icon/dist/material-icon.css";
import "@material/react-drawer/dist/drawer.css";
import "@material/react-list/dist/list.css";
import "@material/react-layout-grid/dist/layout-grid.css";
import "@material/react-typography/dist/typography.css";
import "@material/react-card/dist/card.css";
import "@material/react-checkbox/dist/checkbox.css";
import "@material/react-text-field/dist/text-field.css";
import "@material/react-chips/dist/chips.css";
import "@material/react-material-icon/dist/material-icon.css";
import "@material/react-radio/dist/radio.css";
import "./App.sass";

function App() {
  return (
    <>
      <AuthHandler>
        <TopAppBar>
          <HeaderMaterial />
        </TopAppBar>
        <Dashboard>
          <Routes />
        </Dashboard>
      </AuthHandler>
      <BottomStickyPolicy />
      <NotificationSocketController />
      <ToastContainer
        bodyClassName="qa_toast_text"
        className="qa_toast_container"
        autoClose={3000}
      />
    </>
  );
}

const connectedWithRouterApp = withRouter(App);

export default connectedWithRouterApp;
