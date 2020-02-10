import { compile, pathToRegexp } from "path-to-regexp";

function UrlInstance(url) {
  this.url = url;
  this.toPath = compile(url);
  this.toRegexp = pathToRegexp(url);
}

export default {
  Home: new UrlInstance("/"),
  JiraImport: new UrlInstance("/jira-import"),
  JiraImportMapping: new UrlInstance("/jira-import/:timestamp"),
  AcceptEmailInvite: new UrlInstance("/accept-email-invite"),
  Login: new UrlInstance("/login/:service?"),
  MyProfile: new UrlInstance("/profile"),
  RegisterConfirmation: new UrlInstance("/register"),
  Join: new UrlInstance("/join/:page?/:hash?"),
  ProjectCreate: new UrlInstance("/project/new"),
  ProjectCreateWithoutPredefinedOrganization: new UrlInstance("/new-project"),
  ProjectSingle: new UrlInstance("/project/:project_uiid"),
  MyProjects: new UrlInstance("/my-projects"),
  FavoritesItems: new UrlInstance("/favorites"),
  ProjectEdit: new UrlInstance("/project/:project_uiid/edit"),
  ProjectDiscussion: new UrlInstance("/project/:project_uiid/discussion"),
  ProjectRoadmap: new UrlInstance("/project/:project_uiid/roadmap"),
  ProjectMembers: new UrlInstance("/project/:project_uiid/members"),
  ProjectMemberEditing: new UrlInstance(
    "/project/:project_uiid/member/:project_member_id/edit"
  ),
  ProjectPredefinedPermissionsInviteEdit: new UrlInstance(
    "/project/:project_uiid/members/invite/:invite_id/edit"
  ),
  Documentation: new UrlInstance("/project/:project_uiid/documentation"),
  DocumentationEdit: new UrlInstance(
    "/project/:project_uiid/documentation/edit"
  ),
  TaskCreate: new UrlInstance("/project/:project_uiid/task/new"),
  TaskContent: new UrlInstance("/project/:project_uiid/task/:task_number"),
  Error: new UrlInstance("/error/"),
  ResetPassword: new UrlInstance("/reset-password/:code?")
};
