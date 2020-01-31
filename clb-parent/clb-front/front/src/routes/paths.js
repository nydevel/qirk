import pathToRegexp, { compile } from "path-to-regexp";

function UrlInstance(url) {
  this.url = url;
  this.toPath = compile(url);
  this.toRegexp = pathToRegexp(url);
}

export default {
  Home: new UrlInstance("/"),
  JiraImport: new UrlInstance("/organization/:organization_uiid/jira-import"),
  JiraImportMapping: new UrlInstance(
    "/organization/:organization_uiid/jira-import/:timestamp"
  ),
  AddBalance: new UrlInstance("/add-funds"),
  ChangeLog: new UrlInstance("/changelog"),
  AboutUs: new UrlInstance("/about"),
  UpdatedLicense: new UrlInstance("/license-updated"),
  LicenseContract: new UrlInstance("/license-contract"),
  PrivacyPolicy: new UrlInstance("/privacy-policy"),
  AcceptEmailInvite: new UrlInstance("/accept-email-invite"),
  FeebackForm: new UrlInstance("/feedback"),
  Login: new UrlInstance("/login/:service?"),
  MyProfile: new UrlInstance("/profile"),
  RegisterConfirmation: new UrlInstance("/register"),
  Join: new UrlInstance("/join/:page?/:hash?"),
  ProjectCreate: new UrlInstance(
    "/organization/:organization_uiid/project/new"
  ),
  ProjectCreateWithoutPredefinedOrganization: new UrlInstance("/new-project"),
  ProjectSingle: new UrlInstance(
    "/organization/:organization_uiid/project/:project_uiid"
  ),
  OrganizationCreate: new UrlInstance("/organization/new"),
  OrganizationEdit: new UrlInstance("/organization/:organization_uiid/edit"),
  OrganizationOverview: new UrlInstance("/organization/:organization_uiid"),
  MyOrganizations: new UrlInstance("/my-organizations"),
  MyProjects: new UrlInstance("/my-projects"),
  FavoritesItems: new UrlInstance("/favorites"),
  ProjectEdit: new UrlInstance(
    "/organization/:organization_uiid/project/:project_uiid/edit"
  ),
  ProjectDiscussion: new UrlInstance(
    "/organization/:organization_uiid/project/:project_uiid/discussion"
  ),
  ProjectRoadmap: new UrlInstance(
    "/organization/:organization_uiid/project/:project_uiid/roadmap"
  ),
  ProjectMembers: new UrlInstance(
    "/organization/:organization_uiid/project/:project_uiid/members"
  ),
  ProjectMemberEditing: new UrlInstance(
    "/organization/:organization_uiid/project/:project_uiid/member/:project_member_id/edit"
  ),
  ProjectPredefinedPermissionsInviteEdit: new UrlInstance(
    "/organization/:organization_uiid/project/:project_uiid/members/invite/:invite_id/edit"
  ),
  Documentation: new UrlInstance(
    "/organization/:organization_uiid/project/:project_uiid/documentation"
  ),
  DocumentationEdit: new UrlInstance(
    "/organization/:organization_uiid/project/:project_uiid/documentation/edit"
  ),
  TaskCreate: new UrlInstance(
    "/organization/:organization_uiid/project/:project_uiid/task/new"
  ),
  TaskContent: new UrlInstance(
    "/organization/:organization_uiid/project/:project_uiid/task/:task_number"
  ),
  OrganizationMembers: new UrlInstance(
    "/organization/:organization_uiid/members"
  ),
  OrganizationMemberEdit: new UrlInstance(
    "/organization/:organization_uiid/members/:member_id/edit"
  ),
  OrganizationMemberAdd: new UrlInstance(
    "/organization/:organization_uiid/members/new"
  ),
  Error: new UrlInstance("/error/"),
  ResetPassword: new UrlInstance("/reset-password/:code?")
};
