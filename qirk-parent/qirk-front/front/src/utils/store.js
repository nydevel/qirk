import { createStore, compose, applyMiddleware } from "redux";
import { createBrowserHistory } from "history";
import { routerMiddleware } from "connected-react-router";
import { createLogger } from "redux-logger";
import thunk from "redux-thunk";
import reducers from "../reducers";

export const history = createBrowserHistory();

const middlewares = [routerMiddleware(history), thunk];

if (process.env.NODE_ENV === "development") {
  middlewares.push(
    createLogger({
      collapsed: true
    })
  );
}

export default createStore(
  reducers(history),
  compose(applyMiddleware(...middlewares))
);
