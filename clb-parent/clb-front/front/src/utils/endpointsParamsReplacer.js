/**
 * Метод, заменяющий в URL плейсхолдеры под параметры на реальные значения
 * @constructor
 * @param {string} url - URL, в котором нужно заменить параметры вида "{param}"
 * @param {string} params - набор значений, которые последовательно заменять параметры
 */

export const endpointsParamsReplacer = (url, ...params) => {
  let resultUrl = url;
  if (url) {
    for (const param of params) {
      resultUrl = resultUrl.replace(/{.*?}/, param);
    }
  }
  return resultUrl;
};
