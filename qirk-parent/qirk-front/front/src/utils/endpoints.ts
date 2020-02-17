export default {
  CHECK_EMAIL_AVAILABILITY: "/user/check-email/",
  REGISTER: "/user/register/",
  RESEND_REGISTRATION_EMAIL: "/user/resend-email/",
  POST_USER_RESEND_EMAIL: "/user/resend-email/",
  USER_ACTIVATE: "/user/activate/",
  LOGIN: "/authn/login/",
  LOGOUT: "/authn/logout",
  CHECK_AUTH: "/authn/check",
  GET_CSRF_REFRESH: "/csrf/refresh",

  POST_USER_REGISTER_NO_PASSWORD: "/user/register/",
  POST_USER_RESET_PASSWORD: "/user/reset-password/",
  POST_USER_ACTIVATE_NO_PASSWORD: "/user/activate/",
  GET_USER_CHECK_USERNAME: "/user/check-username/",

  POST_ACCEPT_LICENSE: "/user/accept-license/",

  USER: "/user/profile/",
  GET_USER_SEARCH_FOR_PROJECT: "user/search-for-project/",
  GET_USER_SEARCH_BY_ALIAS: "/user/search-by-alias/",
  GET_USER: "/user/",
  GET_USER_INVITE_TO_PROJECT_OPTIONS: "/project/list-invite-options/",

  PROJECT: "/project/",
  PUT_PROJECT_DOCUMENTATION: "/project/documentation/",
  CHECK_PROJECT_UIID: "/project/check-ui-id/?ui_id={ui_id}",
  PROJECT_BY_UIID: "/project/?ui_id={ui_id}",
  PROJECT_BY_ID: "/project/?id={id}",
  GET_PROJECT_LIST_BY_USER: "/project/list-by-user/",
  GET_SEARCH_PROJECT: "/project/search/",
  GET_PROJECT: "/project/",
  GET_PROJECT_TOP: "/project/top/",

  POST_GRANTED_PERMISSIONS_PROJECT_INVITE:
    "/granted-permissions-project-invite/",

  POST_GRANTED_PERMISSIONS_PROJECT_INVITE_CREATE_BY_EMAIL:
    "/granted-permissions-project-invite/create-by-email/",

  PUT_GRANTED_PERMISSIONS_PROJECT_INVITE:
    "/granted-permissions-project-invite/",

  GET_GRANTED_PERMISSIONS_PROJECT_INVITE:
    "/granted-permissions-project-invite/",

  GET_GRANTED_PERMISSIONS_PROJECT_INVITE_LIST_BY_USER:
    "/granted-permissions-project-invite/list-by-user/",

  POST_GRANTED_PERMISSIONS_PROJECT_INVITE_ACCEPT:
    "/granted-permissions-project-invite/accept/",

  PUT_GRANTED_PERMISSIONS_PROJECT_INVITE_REJECT:
    "/granted-permissions-project-invite/reject/",

  GET_GRANTED_PERMISSIONS_PROJECT_INVITE_LIST_BY_PROJECT:
    "/granted-permissions-project-invite/list-by-project/",

  DELETE_GRANTED_PERMISSIONS_PROJECT_INVITE_CANCEL:
    "/granted-permissions-project-invite/cancel/",

  PUT_PROJECT_INVITE_TOKEN_REJECT: "/project-invite-token/reject/",

  GET_PROJECT_INVITE_CHECK_TOKEN: "/project-invite-token/check-token/",

  POST_PROJECT_INVITE_TOKEN_ACCEPT: "/project-invite-token/accept/",

  POST_PROJECT_INVITE: "/project-invite/",

  GET_PROJECT_INVITE_LIST_BY_USER: "/project-invite/list-by-user/",
  GET_PROJECT_INVITE_LIST_BY_PROJECT: "/project-invite/list-by-project/",

  PUT_PROJECT_INVITE_ACCEPT: "/project-invite/accept/",
  PUT_PROJECT_INVITE_REJECT: "/project-invite/reject/",
  DELETE_PROJECT_INVITE_CANCEL: "/project-invite/cancel/",
  POST_PROJECT_INVITE_EXECUTE: "/project-invite/execute/",

  POST_PROJECT_MEMBER_CREATE_BATCH: "/project-member/create-batch/",
  POST_PROJECT_MEMBER: "/project-member/",
  GET_PROJECT_MEMBER: "/project-member/",
  PUT_PROJECT_MEMBER: "/project-member/",
  DELETE_PROJECT_MEMBER: "/project-member/",
  DELETE_PROJECT_MEMBER_LEAVE: "/project-member/leave/",

  GET_PROJECT_MEMBERS_LIST_BY_PROJECT: "/project-member/list-by-project/",
  GET_PROJECT_MEMBERS_LIST_BY_USER: "/project-member/list-by-user/",

  POST_PROJECT_APPLICATION: "/project-application/",
  GET_PROJECT_APPLICATIONS_LIST_BY_PROJECT:
    "/project-application/list-by-project/",
  GET_PROJECT_APPLICATIONS_LIST_BY_USER: "/project-application/list-by-user/",
  DELETE_PROJECT_APPLICATION: "/project-application/cancel/",
  PUT_PROJECT_APPLICATION_REJECT: "/project-application/reject/",
  POST_PROJECT_APPLICATION_ACCEPT: "/project-application/accept/",

  GET_LIST_LINK_OPTIONS: "/task/list-link-options/",
  TASK: "/task/",
  TASK_BY_NUMBER: "/task/?project_ui_id={project_uiid}&number={number}",
  //GET_TASK_SEARCH: "/task/search/",
  GET_TASK_SEARCH: "/task/text-search/",
  TASK_MIGRATE: "/task/migrate/",
  POST_TASK_LINK: "/task/link/",
  REMOVE_TASK_LINK: "/task/link/",

  LANGUAGES_LIST: "/language/list/",

  FILE_UPLOAD: "/file/upload-v2/",
  TASK_ATTACHMENTS: "/attachment/list/",
  ATTACHMENT: "/attachment/",

  GET_FAVORITES: "/user-favorite/list/",
  POST_ADD_TO_FAVORITES: "/user-favorite/",
  PUT_MOVE_FAVORITES: "/user-favorite/move/",
  DELETE_FAVORITE: "/user-favorite/",

  PUT_CHANGE_PASSWORD: "/user/change-password/",
  GET_CHAT_TOKEN: "/{lc_chat_type}/chat-token/",

  DELETE_TASK_HASHTAG: "/task-hashtag/",
  GET_TASK_HASHTAG_SEARCH: "/task-hashtag/search/",
  POST_TASK_SUBSCRIBER: "/task-subscriber/",
  DELETE_TASK_SUBSCRIBER: "/task-subscriber/",
  GET_TASK_SUBSCRIBER_LIST: "/task-subscriber/list/",

  GET_USER_NOTIFICATION_TOKEN: "/user/notification-token/",
  GET_USER_CREDIT_TOKEN: "/user/credit-token/",
  GET_USER_BALANCE: "/user-account/",

  POST_ROAD: "/road/",
  PUT_ROAD: "/road/",
  DELETE_ROAD: "/road/",
  PUT_ROAD_MOVE: "/road/move/",
  GET_ROAD_LIST: "/road/list/",

  PUT_TASK_SLASH_CARD: "/task/card/",

  POST_TASK_CARD: "/task-card/",
  PUT_TASK_CARD: "/task-card/",
  DELETE_TASK_CARD: "/task-card/",
  PUT_TASK_CARD_MOVE: "/task-card/move/",
  GET_TASK_CARD_LIST: "/task-card/list/",
  PUT_TASK_CARD_ARCHIVE: "/task-card/archive/",
  GET_TASK_CARD_ARCHIVE: "/task-card/archive/",
  GET_TASK_LIST_CARDLESS: "/task/list-cardless/",

  POST_JIRA_IMPORT_UPLOAD: "/jira-import/upload/",
  GET_JIRA_IMPORT_LIST_UPLOADS: "/jira-import/list-uploads/",
  GET_JIRA_IMPORT_LIST_PROJECTS: "/jira-import/list-projects/",
  GET_JIRA_IMPORT_LIST_PROJECTS_DATA: "/jira-import/list-projects-data/",
  POST_JIRA_IMPORT: "/jira-import/",

  GET_STATIC_DATA_CHANGELOG: "changelog.html",
  GET_STATIC_DATA_PRIVACY_POLICY: "privacy-policy.html",
  GET_STATIC_DATA_LICENSE: "license-contract.html",

  WS_CHAT: "wss://{domain}:{port}/clb-chat-web/chat",
  WS_NOTIFICATIONS: "wss://{domain}:{port}/clb-notification-web/notification"
};
