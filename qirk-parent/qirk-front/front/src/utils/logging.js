export const applyLoggingSettings = () => {
  console.log(process.env);
  const debug = process.env.REACT_APP_DEBUG_LOGS;
  const error = process.env.REACT_APP_ERROR_LOGS;
  const errorMethods = ["warn", "error"];
  const debugMethods = ["log", "debug", "info"];

  if (!window.console) window.console = {};

  if (!debug) {
    disableConsoleMethods(debugMethods);
  }

  if (!error) {
    disableConsoleMethods(errorMethods);
  }
};

const disableConsoleMethods = funcs => {
  for (var i = 0; i < funcs.length; i++) {
    console[funcs[i]] = function() {};
  }
};
