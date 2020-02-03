import axios from "axios";

const headers = {
  Accept: "application/json",
  "Content-Type": "application/json"
};

const instance = axios.create({
  baseURL: `${process.env.MONEY_SERVER_URL}${process.env.MONEY_SERVER_API_PREFIX}`,
  withCredentials: true,
  headers
});

export default instance;
