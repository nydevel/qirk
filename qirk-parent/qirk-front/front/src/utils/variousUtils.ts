import constants from "./constants";

export const uiUserFromNotification = (notification: any) =>
  notification &&
  uiUserFromUser({
    username: notification.updated_by_username || notification.sender_username,
    full_name:
      notification.sender_full_name || notification.updated_by_full_name
  });

export const uiUserFromUser = (user: any) => {
  if (user) {
    return user.full_name
      ? `${user.full_name} @${user.username}`
      : `@${user.username}`;
  } else {
    return null;
  }
};

export const uiTaskSummary = (taskNumber: any, summary = "...") =>
  `#${taskNumber} ${summary}`;

export const provideTaskTypesOptions = (t: any) =>
  Object.keys(constants.TASK_TYPES).map(key => ({
    value: key,
    label: t(`TaskTypes.${key}`)
  }));

export const provideTaskStatusesOptions = (t: any) =>
  Object.keys(constants.TASK_STATUSES).map(key => ({
    value: key,
    label: t(`TaskStatuses.${key}`)
  }));

export const provideTaskSortOrderOptions = (t: any) =>
  Object.keys(constants.TASK_SORT_ORDER).map(key => ({
    value: key,
    label: t(`TaskSortOrder.${key}`)
  }));

export const regexMatch = (value: any, regex: any) =>
  value &&
  regex &&
  value.match(regex) &&
  value.match(regex)[0] === value.match(regex)["input"];

export const responseIsStatusOk = (response: any) =>
  response && response.data && response.data.status_ok;

export const responseIsEmailSent = (response: any) =>
  response &&
  response.data &&
  response.data.data &&
  response.data.data.length > 0 &&
  response.data.data[0].email_sent === true;

export const errorIsThis = (error: any, status: any) =>
  error &&
  error.response &&
  error.response.data &&
  error.response.data.status_code === status;

export const uiMoney = (amount: any) => amount.toFixed(2);

export const errorIsTooManyLoginAttempts = (error: any) =>
  errorIsThis(error, "TOO_MANY_LOGIN_ATTEMPTS");

export const errorIsLicenseNotAccepted = (error: any) =>
  errorIsThis(error, "LICENSE_NOT_ACCEPTED");

export const errorIsInvalidRecaptcha = (error: any) =>
  errorIsThis(error, "INVALID_RECAPTCHA");

export const errorIsInvalidCsrfToken = (error: any) =>
  errorIsThis(error, "INVALID_CSRF_TOKEN");

export const errorIsAlreadyExists = (error: any) =>
  errorIsThis(error, "ALREADY_EXISTS");

export const errorIsEntityNotFound = (error: any) =>
  errorIsThis(error, "NOT_FOUND");

export const errorIsInvalid = (error: any) => errorIsThis(error, "INVALID");

export const errorIsPermissionDenied = (error: any) =>
  errorIsThis(error, "PERMISSION_DENIED");

export const errorIsConflict = (error: any) => errorIsThis(error, "CONFLICT");

export const errorIsInvalidCredentials = (error: any) =>
  errorIsThis(error, "INVALID_CREDENTIALS");

export const errorIsEntitiesFileNotFound = (error: any) =>
  errorIsThis(error, "ENTITIES_FILE_NOT_FOUND");

export const errorIsNotAuthenticated = (error: any) =>
  errorIsThis(error, "NOT_AUTHENTICATED");

export const errorIsFileTooLarge = (error: any) =>
  errorIsThis(error, "FILE_TOO_LARGE");

export const errorIsInvalidEmail = (error: any) =>
  errorIsThis(error, "INVALID_EMAIL");

export const errorIsYandexCloudUploadFailed = (error: any) =>
  errorIsThis(error, "YANDEX_CLOUD_UPLOAD_FAILED");

export const errorIsAccountDisabled = (error: any) =>
  errorIsThis(error, "ACCOUNT_DISABLED");

export const scrollToRef = (ref: any) => {
  if (ref && ref.current && window) {
    window.scrollTo(0, ref.current.offsetTop);
  }
};

export const cutFirstString = (text: any, limitOfSymbols = 80) => {
  try {
    let result = text.trim();

    if (!result) {
      return "";
    }

    result = result.substring(0, limitOfSymbols);
    result = result.split("\n", 1)[0];

    return result;
  } catch (e) {
    console.error(e);

    return text;
  }
};

export const parseHashtags = (inputText: any) => {
  try {
    var regex = /(?:^|\s)(?:#)([a-zA-Z0-9_.-]+)/gm;
    var matches = [];
    var match;

    while ((match = regex.exec(inputText))) {
      const possibleTag = match[1];

      if (possibleTag.length >= 1 && possibleTag.length <= 127) {
        matches.push(possibleTag);
      }
    }

    return matches;
  } catch (e) {
    console.error(e);
    return [];
  }
};

export const processTaskDescription = (text: any, tags: any) => {
  let resultText = text;
  let lastTag: any = null;
  let lastTagIndex = -1;

  if (!text) {
    return "";
  }

  for (let i = 0; i < tags.length; i++) {
    const tag = tags[i];

    const currentTagIndex = resultText.lastIndexOf(tag);

    if (currentTagIndex !== -1 && currentTagIndex > lastTagIndex) {
      lastTag = tag;
      lastTagIndex = currentTagIndex;
    }
  }

  if (lastTag && lastTagIndex !== -1) {
    tags
      .filter((tag: any) => tag !== lastTag)
      .forEach((tag: any) => {
        resultText = resultText.replace(new RegExp(" " + tag, "g"), "");
        resultText = resultText.replace(new RegExp(tag, "g"), "");
      });

    return removeAllButLastOccurance(resultText, lastTag);
  } else {
    return text;
  }
};

export function removeAllButLastOccurance(string: any, tag: any) {
  // ok if string contains at least one tag, otherwise breaks

  const parts = string.split(tag).map((part: any) => {
    const lastCharacterIsSpace = part.endsWith("  ");

    if (lastCharacterIsSpace) {
      const cutPart = part.substring(0, part.length - 1);
      return cutPart;
    } else {
      return part;
    }
  });
  return parts.slice(0, -1).join("") + tag + parts.slice(-1);
}

// TODO Denis: use this
export const clickIsInside = (event: any, ref: any) =>
  ref && ref.current && ref.current.contains(event && event.target);

export const saveItemToLocalStorageSafe = (key: any, value: any) => {
  try {
    return localStorage.setItem(key, JSON.stringify(value));
  } catch (e) {
    console.error(e);
  }
};

export const takeItemFromLocalStorageSafe = (
  key: string,
  defaultValue: any = null
) => {
  try {
    const lsVal = localStorage.getItem(key);
    return lsVal === null ? defaultValue : JSON.parse(lsVal);
  } catch (e) {
    console.error(e);

    return defaultValue;
  }
};

export const removeItemFromLocalStorageSafe = function(args: any) {
  try {
    return args.forEach((item: any) => localStorage.removeItem(item));
  } catch (e) {
    console.error(e);
  }
};

export const setTimeOutToWrite = (function() {
  let timer: any = 0;
  return function(callback: any, ms: any) {
    clearTimeout(timer);
    timer = setTimeout(callback, ms);
  };
})();
