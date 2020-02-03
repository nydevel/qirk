import pathToRegexp from "path-to-regexp";

/**
 * Метод, заменяющий параметр в URL
 * @constructor
 * @param {string} match - объект match, который получается пропсом из withRouter
 * @param {string} param - параметр, который заменяем
 * @param {string} value - значение, на который заменяем
 */

const urlParamReplacer = (match, param, value) => {
  const toPath = pathToRegexp.compile(match.path);
  return toPath({ ...match.params, [param]: value });
};

export default urlParamReplacer;
