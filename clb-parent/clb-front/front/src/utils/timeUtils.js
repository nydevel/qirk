import moment from "moment";

const formatTime = (time, format) => moment(time).format(format);

export const uiDateTime = time => formatTime(time, "LLLL");

export const uiDate = time => formatTime(time, "LL");

export const uiTime = time => formatTime(time, "LT");

export const debugDateTime = time => formatTime(time, "DD.MM.YYYY k:mm:ss (x)");

export const unixMillisecondTimestamp = time => formatTime(time, "x"); // task search timestamp
