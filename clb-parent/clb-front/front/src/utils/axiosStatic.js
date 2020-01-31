import axios from "axios";

const headers = {
  Accept: "text/html",
  "Content-Type": "application/json"
};

const instance = axios.create({
  baseURL: `${process.env.REACT_APP_FRONT_URL}${process.env.REACT_APP_FRONT_STATIC_PAGES_PATH}`,
  withCredentials: true,
  headers
});

export default instance;
