import React from "react";
import { Route, Switch } from "react-router-dom";

import paths from "./paths";

import Home from "../pages/Home/Home";
import Join from "../pages/Join/Join";
import Login from "../pages/Login/Login";
import MyProfile from "../pages/MyProfile/MyProfile";
import MyProjects from "../pages/MyProjects/MyProjects";
import NotFoundPage from "../pages/NotFoundPage/NotFoundPage";
import ProjectCreate from "../pages/ProjectCreate/ProjectCreate";
import ProjectEdit from "../pages/ProjectEdit/ProjectEdit";
import ProjectSingle from "../pages/ProjectView/ProjectView";
import ProjectDiscussion from "../pages/ProjectDiscussion/ProjectDiscussion";
import ResetPassword from "../pages/ResetPassword/ResetPassword";
import TaskCreate from "../pages/TaskCreate/TaskCreate";
import TaskContent from "../pages/TaskContent/TaskContent";
import Documentation from "../pages/ProjectDocumentation/ProjectDocumentation";
import DocumentationEdit from "../pages/ProjectDocumentationEdit/ProjectDocumentationEdit";
import ProjectMembersOverview from "../pages/ProjectMembersOverview/ProjectMembersOverview";
import ErrorPage from "../pages/Error/Error";
import ProjectMemberEditing from "../pages/ProjectMemberEditing/ProjectMemberEditing";
import FavoritesItems from "../pages/FavoritesItems/FavoritesItems";
import ProjectPredefinedPermissionsInviteEdit from "../pages/ProjectPredefinedPermissionsInviteEdit/ProjectPredefinedPermissionsInviteEdit";
import AcceptEmailInvite from "../pages/AcceptEmailInvite/AcceptEmailInvite";
import RegisterConfirmation from "../pages/RegisterConfirmation/RegisterConfirmation";
import Roadmap from "../pages/Roadmap/Roadmap";
import JiraImport from "../pages/JiraImport/JiraImport";
import JiraImportMappingPage from "../pages/JiraImport/JiraImportMappingPage";

export default () => (
  <Switch>
    <Route exact path={paths.Home.url} component={Home} />
    <Route exact path={paths.JiraImport.url} component={JiraImport} />
    <Route
      exact
      path={paths.JiraImportMapping.url}
      component={JiraImportMappingPage}
    />
    <Route
      exact
      path={paths.RegisterConfirmation.url}
      component={RegisterConfirmation}
    />
    <Route
      exact
      path={paths.ProjectCreateWithoutPredefinedOrganization.url}
      component={ProjectCreate}
    />
    <Route
      exact
      path={paths.AcceptEmailInvite.url}
      component={AcceptEmailInvite}
    />
    <Route
      exact
      path={paths.ProjectPredefinedPermissionsInviteEdit.url}
      component={ProjectPredefinedPermissionsInviteEdit}
    />
    <Route exact path={paths.Login.url} component={Login} />
    <Route exact path={paths.Join.url} component={Join} />
    <Route exact path={paths.MyProfile.url} component={MyProfile} />
    <Route exact path={paths.ProjectCreate.url} component={ProjectCreate} />
    <Route exact path={paths.MyProjects.url} component={MyProjects} />
    <Route exact path={paths.FavoritesItems.url} component={FavoritesItems} />
    <Route exact path={paths.ResetPassword.url} component={ResetPassword} />
    <Route
      exact
      path={paths.ProjectMemberEditing.url}
      component={ProjectMemberEditing}
    />
    <Route exact path={paths.ProjectSingle.url} component={ProjectSingle} />
    <Route
      exact
      path={paths.ProjectDiscussion.url}
      component={ProjectDiscussion}
    />
    <Route exact path={paths.ProjectRoadmap.url} component={Roadmap} />
    <Route exact path={paths.TaskCreate.url} component={TaskCreate} />
    <Route exact path={paths.TaskContent.url} component={TaskContent} />
    <Route exact path={paths.ProjectEdit.url} component={ProjectEdit} />
    <Route exact path={paths.Documentation.url} component={Documentation} />
    <Route
      exact
      path={paths.DocumentationEdit.url}
      component={DocumentationEdit}
    />
    <Route
      exact
      path={paths.ProjectMembers.url}
      component={ProjectMembersOverview}
    />
    <Route exact path={paths.Error.url} component={ErrorPage} />
    <Route component={NotFoundPage} />
  </Switch>
);
