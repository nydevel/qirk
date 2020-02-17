import moment from "moment";

const formatTime = (time: any, format: any) => moment(time).format(format);

export const uiDateTime = (time: any) => formatTime(time, "LLLL");

export const uiDate = (time: any) => formatTime(time, "LL");

export const uiTime = (time: any) => formatTime(time, "LT");

export const debugDateTime = (time: any) =>
  formatTime(time, "DD.MM.YYYY k:mm:ss (x)");

export const unixMillisecondTimestamp = (time: any) => formatTime(time, "x"); // task search timestamp
