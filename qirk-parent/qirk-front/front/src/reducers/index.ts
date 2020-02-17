import { connectRouter } from "connected-react-router";
import { combineReducers } from "redux";
import ui from "./uiReducer";
import auth from "./authReducer";
import chat from "./chatReducer";
import user from "./userReducer";
import invite from "./inviteReducer";
import project from "./projectReducer";
import roadmap from "./roadmapReducer";
import favorites from "./favoritesReducer";
import taskSearch from "./taskSearchReducer";
import cacheUsers from "./cacheUsersReducer";
import attachments from "./attachmentsReducer";
import notifications from "./notificationReducer";
import helpForSelectedTask from "./helpForSelectedTaskReducer";

export default (history: any /* todo */) =>
  combineReducers({
    ui,
    auth,
    chat,
    user,
    invite,
    project,
    roadmap,
    favorites,
    taskSearch,
    cacheUsers,
    attachments,
    notifications,
    helpForSelectedTask,
    router: connectRouter(history)
  });
