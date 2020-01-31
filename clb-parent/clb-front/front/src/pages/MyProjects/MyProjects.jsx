import React, { useState, useEffect } from "react";
import axios from "../../utils/axios";
import endpoints from "../../utils/endpoints";
import { Link } from "react-router-dom";
import { connect } from "react-redux";
import { toast } from "react-toastify";
import { t } from "i18next";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import ProjectListItem from "../../components/ProjectListItem/ProjectListItem";
import paths from "../../routes/paths";
import Loading from "../../components/Loading/Loading";
import {
  responseIsStatusOk,
  errorIsNotAuthenticated
} from "../../utils/variousUtils";
import "./MyProjects.sass";
import Button1 from "../../components/Button/Button";

function MyProjects({ favProjects, favIdOrder }) {
  const getFavId = projId => {
    const fav = favProjects.find(fav => fav.project.id === projId);

    return fav ? fav.id : -1;
  };
  const getFavIndex = projId => {
    const index = favIdOrder.findIndex(favId => favId === getFavId(projId));

    return index === -1 ? Infinity : index;
  };

  const [projects, setProjects] = useState([]);
  const [
    predefinedPermissionsInvites,
    setPredefinedPermissionsInvites
  ] = useState([]);
  const [incomingInvites, setIncomingInvites] = useState([]);
  const [myApplications, setMyApplications] = useState([]);
  const [isLoadingIncomingInvites, setIsLoadingIncomingInvites] = useState(
    true
  );
  const [isLoadingMyApplications, setIsLoadingMyApplications] = useState(true);
  const [
    isLoadingPredefinedPermissionsInvites,
    setIsLoadingPredefinedPermissionsInvites
  ] = useState(true);
  const [isLoading, setIsLoading] = useState(true);
  const [inviteRequestInProgress, setInviteRequestInProgress] = useState(false);
  const [
    predefinedInviteRequestInProgress,
    setPredefinedInviteRequestInProgress
  ] = useState(false);
  const [cancelRequestInProgress, setCancelRequestInProgress] = useState(false);

  const fetchPredefinedPermissionsInvites = async () => {
    try {
      setIsLoadingPredefinedPermissionsInvites(true);
      const response = await axios.get(
        endpoints.GET_GRANTED_PERMISSIONS_PROJECT_INVITE_LIST_BY_USER
      );
      if (responseIsStatusOk(response)) {
        setPredefinedPermissionsInvites(response.data.data);
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setIsLoadingPredefinedPermissionsInvites(false);
    }
  };

  const fetchMyProjects = async () => {
    try {
      setIsLoading(true);
      const response = await axios.get(endpoints.GET_PROJECT_LIST_BY_USER);
      if (responseIsStatusOk(response) && response.data.data) {
        setProjects(response.data.data);
      }
    } catch (err) {
      if (errorIsNotAuthenticated(err)) {
        // ok, just ignore
      } else {
        toast.error(t("Errors.Error"));
      }
    } finally {
      setIsLoading(false);
    }
  };

  const fetchIncomingInvites = async () => {
    try {
      setIsLoadingIncomingInvites(true);
      const response = await axios.get(
        endpoints.GET_PROJECT_INVITE_LIST_BY_USER
      );
      if (responseIsStatusOk(response) && response.data.data) {
        setIncomingInvites(response.data.data);
      }
    } catch (err) {
      if (errorIsNotAuthenticated(err)) {
        // ok, just ignore
      } else {
        toast.error(t("Errors.Error"));
      }
    } finally {
      setIsLoadingIncomingInvites(false);
    }
  };

  const fetchMyApplications = async () => {
    try {
      setIsLoadingMyApplications(true);
      const response = await axios.get(
        endpoints.GET_PROJECT_APPLICATIONS_LIST_BY_USER
      );
      if (responseIsStatusOk(response) && response.data.data) {
        setMyApplications(response.data.data);
      } else {
        toast.error(t("Errors.Error"));
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setIsLoadingMyApplications(false);
    }
  };

  const updateInvite = invite => {
    setIncomingInvites(
      incomingInvites.map(inv =>
        inv.id === invite.id ? { ...inv, ...invite } : { ...inv }
      )
    );
  };

  const acceptPredefinedPermissionsInvite = async invite => {
    if (predefinedInviteRequestInProgress) {
      return toast.error(t("this_request_is_already_in_progress"));
    }

    try {
      setPredefinedInviteRequestInProgress(true);

      const response = await axios.post(
        endpoints.POST_GRANTED_PERMISSIONS_PROJECT_INVITE_ACCEPT,
        {
          id: invite.id
        }
      );

      if (responseIsStatusOk(response)) {
        setProjects([invite.project, ...projects]);
        setPredefinedPermissionsInvites(
          predefinedPermissionsInvites.filter(inv => inv.id !== invite.id)
        );
        toast.success(t("Success"));
      } else {
        toast.error(t("Errors.Error"));
      }
    } catch (e) {
      toast.error(t("Errors.Error"));
    } finally {
      setPredefinedInviteRequestInProgress(false);
    }
  };

  const rejectPredefinedPermissionsInvite = async (
    invite,
    reported = false
  ) => {
    if (predefinedInviteRequestInProgress) {
      return toast.error(t("this_request_is_already_in_progress"));
    }

    try {
      setPredefinedInviteRequestInProgress(true);

      const response = await axios.put(
        endpoints.PUT_GRANTED_PERMISSIONS_PROJECT_INVITE_REJECT,
        {
          id: invite.id,
          reported
        }
      );

      if (responseIsStatusOk(response)) {
        setPredefinedPermissionsInvites(
          predefinedPermissionsInvites.filter(inv => inv.id !== invite.id)
        );
        toast.success(t("Success"));
      } else {
        toast.error(t("Errors.Error"));
      }
    } catch (e) {
      toast.error(t("Errors.Error"));
    } finally {
      setPredefinedInviteRequestInProgress(false);
    }
  };

  const acceptInvite = async invite => {
    try {
      setInviteRequestInProgress(true);
      const response = await axios.put(endpoints.PUT_PROJECT_INVITE_ACCEPT, {
        id: invite.id
      });

      if (
        responseIsStatusOk(response) &&
        response.data.data &&
        response.data.data.length > 0
      ) {
        updateInvite({
          ...response.data.data[0],
          status: { name_code: "ACCEPTED" }
        });
        toast.success(t("Success"));
      } else {
        toast.error(t("Errors.Error"));
      }
    } catch (e) {
      toast.error(t("Errors.Error"));
    } finally {
      setInviteRequestInProgress(false);
    }
  };

  const rejectInvite = async (invite, reported = false) => {
    try {
      setInviteRequestInProgress(true);

      const response = await axios.put(endpoints.PUT_PROJECT_INVITE_REJECT, {
        id: invite.id,
        reported
      });

      if (
        responseIsStatusOk(response) &&
        response.data.data &&
        response.data.data.length > 0
      ) {
        updateInvite({
          ...response.data.data[0],
          status: { name_code: "REJECTED" }
        });
        toast.success(t("Success"));
      } else {
        toast.error(t("Errors.Error"));
      }
    } catch (e) {
      toast.error(t("Errors.Error"));
    } finally {
      setInviteRequestInProgress(false);
    }
  };

  const cancelApplication = async id => {
    try {
      setCancelRequestInProgress(true);
      const response = await axios.delete(
        endpoints.DELETE_PROJECT_APPLICATION,
        { data: { id } }
      );
      if (responseIsStatusOk(response)) {
        toast.success(t("canceled_successfully"));
        setMyApplications(myApplications.filter(a => a.id !== id));
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setCancelRequestInProgress(false);
    }
  };

  useEffect(() => {
    if (incomingInvites && incomingInvites.length) {
      const filtered = incomingInvites.filter(
        invite =>
          invite &&
          invite.status &&
          invite.status.name_code &&
          invite.status.name_code !== "REJECTED"
      );
      if (filtered.length !== incomingInvites.length) {
        setIncomingInvites(filtered);
      }
    }
  }, [incomingInvites]);

  useEffect(() => {
    if (myApplications && myApplications.length) {
      const filtered = myApplications.filter(
        a =>
          a &&
          a.status &&
          a.status.name_code &&
          a.status.name_code !== "REJECTED"
      );
      if (filtered.length !== myApplications.length) {
        setMyApplications(filtered);
      }
    }
  }, [myApplications]);

  return (
    <PrivatePage
      className="organization-list"
      onLoginConfirmed={() => {
        fetchMyProjects();
        fetchIncomingInvites();
        fetchMyApplications();
        fetchPredefinedPermissionsInvites();
      }}
    >
      <div>
        {(isLoadingIncomingInvites ||
          isLoadingMyApplications ||
          isLoadingPredefinedPermissionsInvites) &&
          !isLoading && <Loading />}

        {myApplications &&
          myApplications.length > 0 &&
          !isLoadingMyApplications && (
            <div className="invites">
              <h1>{t("my_applications")}</h1>
              {myApplications.map(a => (
                <div key={a.id} className="invite-project-wrapper">
                  <div className="invite-project">
                    <Link
                      to={paths.ProjectSingle.toPath({
                        project_uiid:
                          (a && a.project && a.project.ui_id) || "proj",
                        organization_uiid:
                          (a &&
                            a.project &&
                            a.project.organization &&
                            a.project.organization.ui_id) ||
                          "org"
                      })}
                    >
                      <span>{a.project.name}</span>
                    </Link>
                    <div className="controls">
                      <Button1
                        disabled={cancelRequestInProgress}
                        onClick={() => cancelApplication(a.id)}
                      >
                        {t("cancel")}
                      </Button1>
                    </div>
                  </div>
                  {a.text && <div className="invite-text">{a.text}</div>}
                </div>
              ))}
            </div>
          )}
        {((incomingInvites && incomingInvites.length > 0) ||
          (predefinedPermissionsInvites &&
            predefinedPermissionsInvites.length > 0)) &&
          (!isLoadingPredefinedPermissionsInvites &&
            !isLoadingIncomingInvites) && (
            <div className="invites">
              <h1>{t("my_projects_incoming_invites")}</h1>
              {predefinedPermissionsInvites.map(invite => (
                <div key={invite.id} className="invite-project-wrapper">
                  <div className="invite-project">
                    <Link
                      to={paths.ProjectSingle.toPath({
                        project_uiid:
                          (invite && invite.project && invite.project.ui_id) ||
                          "proj",
                        organization_uiid:
                          (invite &&
                            invite.project &&
                            invite.project.organization &&
                            invite.project.organization.ui_id) ||
                          "org"
                      })}
                    >
                      <span>{invite.project.name}</span>
                    </Link>

                    <div className="controls">
                      <Button1
                        disabled={predefinedInviteRequestInProgress}
                        onClick={() =>
                          acceptPredefinedPermissionsInvite(invite)
                        }
                      >
                        {t("accept_invite")}
                      </Button1>
                      <Button1
                        disabled={predefinedInviteRequestInProgress}
                        onClick={() =>
                          rejectPredefinedPermissionsInvite(invite)
                        }
                      >
                        {t("reject_invite")}
                      </Button1>
                      <Button1
                        disabled={predefinedInviteRequestInProgress}
                        onClick={() =>
                          rejectPredefinedPermissionsInvite(invite, true)
                        }
                      >
                        {t("reject_and_report")}
                      </Button1>
                    </div>
                  </div>
                </div>
              ))}
              {incomingInvites.map(invite => (
                <div key={invite.id} className="invite-project-wrapper">
                  <div className="invite-project">
                    <Link
                      to={paths.ProjectSingle.toPath({
                        project_uiid:
                          (invite && invite.project && invite.project.ui_id) ||
                          "proj",
                        organization_uiid:
                          (invite &&
                            invite.project &&
                            invite.project.organization &&
                            invite.project.organization.ui_id) ||
                          "org"
                      })}
                    >
                      <span>{invite.project.name}</span>
                    </Link>
                    {invite.status.name_code === "PENDING" && (
                      <div className="controls">
                        <Button1
                          disabled={inviteRequestInProgress}
                          onClick={() => acceptInvite(invite)}
                        >
                          {t("accept_invite")}
                        </Button1>
                        <Button1
                          disabled={inviteRequestInProgress}
                          onClick={() => rejectInvite(invite)}
                        >
                          {t("reject_invite")}
                        </Button1>
                        <Button1
                          disabled={inviteRequestInProgress}
                          onClick={() => rejectInvite(invite, true)}
                        >
                          {t("reject_and_report")}
                        </Button1>
                      </div>
                    )}
                    {invite.status.name_code === "ACCEPTED" && (
                      <div className="controls">{t("accepted")}</div>
                    )}
                  </div>
                  {invite.text && (
                    <div className="invite-text">{invite.text}</div>
                  )}
                </div>
              ))}
            </div>
          )}

        <h1>{t("my_projects_label")}</h1>

        <div style={{ paddingBottom: 10 }}>
          <Link to={paths.ProjectCreateWithoutPredefinedOrganization.toPath()}>
            <Button1>{t("Create project")}</Button1>
          </Link>
        </div>

        {isLoading && <Loading />}
        {!isLoading &&
          projects &&
          projects.length > 0 &&
          projects
            .sort((p1, p2) => getFavIndex(p1.id) - getFavIndex(p2.id))
            .map(p => (
              <ProjectListItem
                key={p.id}
                p={p}
                orgUiId={(p.organization && p.organization.ui_id) || "org"}
              />
            ))}

        {!isLoading && projects && projects.length === 0 && (
          <div>
            <div style={{ paddingBottom: 10 }}>
              {t("you_are_not_a_member_of_any_project")}
            </div>
          </div>
        )}
      </div>
    </PrivatePage>
  );
}

export default connect(state => ({
  favIdOrder: state.favorites.favoriteIdOrder,
  favProjects: state.favorites.listOfFavorites
}))(MyProjects);
