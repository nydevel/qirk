import React from "react";
import { t } from "i18next";
import constants from "./constants";

String.prototype.replaceAll = function(search, replacement) {
  var target = this;
  return target.replace(new RegExp(search, "g"), replacement);
};

export const uiUserFromNotification = notification =>
  notification &&
  uiUserFromUser({
    username: notification.updated_by_username || notification.sender_username,
    full_name:
      notification.sender_full_name || notification.updated_by_full_name
  });

export const uiUserFromOrgMember = orgMember => {
  if (orgMember && orgMember.user) {
    const disabledPrefix =
      orgMember.enabled === false ? (
        <span className="org-member-disabled">
          {t("disabled_prefix_label")}
        </span>
      ) : (
        ""
      );
    return (
      <span>
        {disabledPrefix} {uiUserFromUser(orgMember.user)}
      </span>
    );
  } else {
    return null;
  }
};

export const uiUserFromUser = user => {
  if (user) {
    return user.full_name
      ? `${user.full_name} @${user.username}`
      : `@${user.username}`;
  } else {
    return null;
  }
};

export const uiTaskSummary = (taskNumber, summary = "...") =>
  `#${taskNumber} ${summary}`;

export const provideTaskTypesOptions = () =>
  Object.keys(constants.TASK_TYPES).map(key => ({
    value: key,
    label: t(`TaskTypes.${key}`)
  }));

export const provideTaskStatusesOptions = () =>
  Object.keys(constants.TASK_STATUSES).map(key => ({
    value: key,
    label: t(`TaskStatuses.${key}`)
  }));

export const provideTaskSortByOptions = () =>
  Object.keys(constants.TASK_SORT_BY).map(key => ({
    value: key,
    label: t(`TaskSortBy.${key}`)
  }));

export const provideTaskSortOrderOptions = () =>
  Object.keys(constants.TASK_SORT_ORDER).map(key => ({
    value: key,
    label: t(`TaskSortOrder.${key}`)
  }));

export const langToOption = lang => ({
  value: lang.id,
  label: t(`Languages.${lang.name_code}`)
});

export const langOptionsToIdArray = options =>
  options && options.length > 0
    ? options.map(option => parseInt(option.value))
    : [];

export const langIdArrayToOptions = (idArray, allLangOptions) => {
  if (idArray && allLangOptions && idArray.length > 0) {
    return idArray.map(langId => ({
      value: langId,
      label: allLangOptions.some(option => parseInt(option.value) === langId)
        ? allLangOptions.find(option => parseInt(option.value) === langId).label
        : " ... "
    }));
  } else {
    return [];
  }
};

export const regexMatch = (value, regex) =>
  value &&
  regex &&
  value.match(regex) &&
  value.match(regex)[0] === value.match(regex)["input"];

export const responseIsStatusOk = response =>
  response && response.data && response.data.status_ok;

export const responseIsEmailSent = response =>
  response &&
  response.data &&
  response.data.data &&
  response.data.data.length > 0 &&
  response.data.data[0].email_sent === true;

export const errorIsThis = (error, status) =>
  error &&
  error.response &&
  error.response.data &&
  error.response.data.status_code === status;

export const uiMoney = amount => amount.toFixed(2);

export const errorIsTooManyLoginAttempts = error =>
  errorIsThis(error, "TOO_MANY_LOGIN_ATTEMPTS");

export const errorIsLicenseNotAccepted = error =>
  errorIsThis(error, "LICENSE_NOT_ACCEPTED");

export const errorIsInvalidRecaptcha = error =>
  errorIsThis(error, "INVALID_RECAPTCHA");

export const errorIsInvalidCsrfToken = error =>
  errorIsThis(error, "INVALID_CSRF_TOKEN");

export const errorIsAlreadyExists = error =>
  errorIsThis(error, "ALREADY_EXISTS");

export const errorIsEntityNotFound = error => errorIsThis(error, "NOT_FOUND");

export const errorIsInvalid = error => errorIsThis(error, "INVALID");

export const errorIsPermissionDenied = error =>
  errorIsThis(error, "PERMISSION_DENIED");

export const errorIsConflict = error => errorIsThis(error, "CONFLICT");

export const errorIsInvalidCredentials = error =>
  errorIsThis(error, "INVALID_CREDENTIALS");

export const errorIsEntitiesFileNotFound = error =>
  errorIsThis(error, "ENTITIES_FILE_NOT_FOUND");

export const errorIsNotAuthenticated = error =>
  errorIsThis(error, "NOT_AUTHENTICATED");

export const errorIsFileTooLarge = error =>
  errorIsThis(error, "FILE_TOO_LARGE");

export const errorIsInvalidEmail = error => errorIsThis(error, "INVALID_EMAIL");

// export const errorIsDropboxAuthorizationFailed = error =>
//   errorIsThis(error, "DROPBOX_AUTHORIZATION_FAILED");

export const errorIsYandexCloudUploadFailed = error =>
  errorIsThis(error, "YANDEX_CLOUD_UPLOAD_FAILED");

export const errorIsAccountDisabled = error =>
  errorIsThis(error, "ACCOUNT_DISABLED");

export const tagsToOptions = tags =>
  tags && tags.length > 0 ? tags.map(tag => tag.name) : [];

export const tagOptionsToTagArray = tagOptions =>
  tagOptions.map(tagOption => tagOption.value);

export const scrollToRef = ref => {
  if (ref && ref.current && window) {
    window.scrollTo(0, ref.current.offsetTop);
  }
};

export const cutFirstString = (text, limitOfSymbols = 80) => {
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

export const parseHashtags = inputText => {
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

export const processTaskDescription = (text, tags) => {
  let resultText = text;
  let lastTag = null;
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
      .filter(tag => tag !== lastTag)
      .forEach(tag => {
        resultText = resultText.replace(new RegExp(" " + tag, "g"), "");
        resultText = resultText.replace(new RegExp(tag, "g"), "");
      });

    return removeAllButLastOccurance(resultText, lastTag);
  } else {
    return text;
  }
};

export function removeAllButLastOccurance(string, tag) {
  // ok if string contains at least one tag, otherwise breaks

  const parts = string.split(tag).map(part => {
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
export const clickIsInside = (event, ref) =>
  ref && ref.current && ref.current.contains(event && event.target);

export const saveItemToLocalStorageSafe = (key, value) => {
  try {
    return localStorage.setItem(key, JSON.stringify(value));
  } catch (e) {
    console.error(e);
  }
};

export const takeItemFromLocalStorageSafe = (key, defaultValue = null) => {
  try {
    const lsVal = JSON.parse(localStorage.getItem(key));

    return lsVal === null || lsVal === undefined ? defaultValue : lsVal;
  } catch (e) {
    console.error(e);

    return defaultValue;
  }
};

export const removeItemFromLocalStorageSafe = function() {
  try {
    const args = [...arguments];

    return args.forEach(item => localStorage.removeItem(item));
  } catch (e) {
    console.error(e);
  }
};

export const setTimeOutToWrite = (function() {
  let timer = 0;
  return function(callback, ms) {
    clearTimeout(timer);
    timer = setTimeout(callback, ms);
  };
})();
