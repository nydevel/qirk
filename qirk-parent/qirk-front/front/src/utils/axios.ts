import axios from "axios";
import queryString from "query-string";
import cookies from "js-cookie";
import paths from "../routes/paths";
import {
  errorIsNotAuthenticated,
  errorIsInvalidCsrfToken
} from "./variousUtils";
import { fetchCsrf } from "../actions/authActions";

const headers = {
  Accept: "application/json",
  "Content-Type": "application/json"
};

const CSRF_COOKIE_KEY = "qirk-csrf";

const instance = axios.create({
  baseURL: `${process.env.REACT_APP_SERVER_URL}${process.env.REACT_APP_API_PREFIX}`,
  withCredentials: true,
  headers
});

instance.interceptors.request.use(config => {
  config.headers["X-CSRF-TOKEN"] = cookies.get(CSRF_COOKIE_KEY);
  return config;
});

const noBouncePaths = ["/", paths.Login.toPath()];

instance.interceptors.response.use(
  r => r,
  error => {
    const pathname = window.location.pathname;

    if (errorIsInvalidCsrfToken(error)) {
      return fetchCsrf().then(() => {
        error.config.headers["X-CSRF-TOKEN"] = cookies.get(CSRF_COOKIE_KEY);
        error.config.url = error.config.url.replace(
          `/${process.env.REACT_APP_API_PREFIX}`,
          ""
        );
        return instance.request(error.config);
      });
    } else if (errorIsNotAuthenticated(error)) {
      if (pathname && !noBouncePaths.some(url => url === pathname)) {
        window.location.href = `${paths.Login.toPath()}?${queryString.stringify(
          {
            bounce_to: pathname
          }
        )}`;
      } else {
        window.location.href = `${paths.Login.toPath()}`;
      }

      throw new Error("Session expired");
    } else {
      return Promise.reject(error);
    }
  }
);

export default instance;
