import { ConnectedRouter } from "connected-react-router";
import { UiReducerState } from "../../reducers/uiReducer";
import { AuthReducerState } from "../../reducers/authReducer";
import { ChatReducerState } from "../../reducers/chatReducer";
import { UserReducerState } from "../../reducers/userReducer";
import { InviteReducerState } from "../../reducers/inviteReducer";
import { ProjectReducerState } from "../../reducers/projectReducer";
import { RoadmapReducerState } from "../../reducers/roadmapReducer";
import { FavoriteReducerState } from "../../reducers/favoritesReducer";
import { TaskSearchReducerState } from "../../reducers/taskSearchReducer";
import { CacheUsersReducerState } from "../../reducers/cacheUsersReducer";
import { AttachmentsReducerState } from "../../reducers/attachmentsReducer";
import { NotificationReducerState } from "../../reducers/notificationReducer";
import { HelpForSelectedTaskReducerState } from "../../reducers/helpForSelectedTaskReducer";

export type State = {
  ui: UiReducerState;
  auth: AuthReducerState;
  chat: ChatReducerState;
  user: UserReducerState;
  invite: InviteReducerState;
  project: ProjectReducerState;
  roadmap: RoadmapReducerState;
  favorites: FavoriteReducerState;
  taskSearch: TaskSearchReducerState;
  cacheUsers: CacheUsersReducerState;
  attachments: AttachmentsReducerState;
  notifications: NotificationReducerState;
  helpForSelectedTask: HelpForSelectedTaskReducerState;

  router: ConnectedRouter;
};
