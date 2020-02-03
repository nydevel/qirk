import "@babel/polyfill";
import ReactDOM from "react-dom";
import React from "react";
import App from "./components/App";
import { Provider } from "react-redux";
import { ConnectedRouter } from "connected-react-router";
import store from "./utils/store";
import { history } from "./utils/store";
import { applyLoggingSettings } from "./utils/logging";

applyLoggingSettings();

ReactDOM.render(
  <Provider store={store}>
    <ConnectedRouter history={history}>
      <App />
    </ConnectedRouter>
  </Provider>,
  document.querySelector("#root")
);
