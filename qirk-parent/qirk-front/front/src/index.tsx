import React from "react";
import ReactDOM from "react-dom";
import { Provider } from "react-redux";
import { ConnectedRouter } from "connected-react-router";
import App from "./components/App";
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
  document.getElementById("root")
);

const versionComment = document.createComment(
  `UI v${process.env.REACT_APP_VERSION}`
);

document.body.appendChild(versionComment);
