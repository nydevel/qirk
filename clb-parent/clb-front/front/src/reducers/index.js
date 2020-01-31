import { connectRouter } from "connected-react-router";
import { combineReducers } from "redux";
import ui from "./uiReducer";
import auth from "./authReducer";
import chat from "./chatReducer";
import user from "./userReducer";
import invite from "./inviteReducer";
import common from "./commonReducer";
import project from "./projectReducer";
import balance from "./balanceReducer";
import roadmap from "./roadmapReducer";
import favorites from "./favoritesReducer";
import taskSearch from "./taskSearchReducer";
import userSearch from "./userSearchReducer";
import cacheUsers from "./cacheUsersReducer";
import attachments from "./attachmentsReducer";
import organization from "./organizationReducer";
import notifications from "./notificationReducer";
import projectSearch from "./projectSearchReducer";
import organizationSearch from "./organizationSearchReducer";
import helpForSelectedTask from "./helpForSelectedTaskReducer";

export default history =>
  combineReducers({
    ui,
    auth,
    chat,
    user,
    invite,
    common,
    project,
    balance,
    roadmap,
    favorites,
    taskSearch,
    userSearch,
    cacheUsers,
    attachments,
    organization,
    notifications,
    projectSearch,
    organizationSearch,
    helpForSelectedTask,
    router: connectRouter(history)
  });
