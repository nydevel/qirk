import axios from "axios";

const headers = {
  Accept: "text/plain",
  "Content-Type": "application/json"
};

const instance = axios.create({
  baseURL: `${process.env.REACT_APP_SERVER_URL}${process.env.REACT_APP_STATISTICS_API_PREFIX}`,
  withCredentials: true,
  headers
});

export default instance;
