import React, { useState, useEffect } from "react";
import { connect } from "react-redux";
import { toast } from "react-toastify";
import { useTranslation } from "react-i18next";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import WithFetch from "../../components/WithFetch/WithFetch";
import constants from "../../utils/constants";
import Loading from "../../components/Loading/Loading";
import {
  uiUserFromUser,
  responseIsStatusOk,
  errorIsEntityNotFound
} from "../../utils/variousUtils";
import axios from "../../utils/axios";
import endpoints from "../../utils/endpoints";
import Checkbox from "../../components/Checkbox/Checkbox";
import Button1 from "../../components/Button/Button";


function ProjectPredefinedPermissionsInviteEdit({
  projectName,
  match: {
    params: { invite_id }
  }
}) {
  const { t } = useTranslation();

  const [invite, setInvite] = useState(null);
  const [isManager, setIsManager] = useState(false);
  const [allowWrite, setAllowWrite] = useState(false);
  const [isFetching, setIsFetching] = useState(false);
  const [updateInProgress, setUpdateInProgress] = useState(false);

  useEffect(() => {
    if (isManager) {
      setAllowWrite(true);
    }
  }, [isManager]);

  useEffect(() => {
    if (!allowWrite) {
      setIsManager(false);
    }
  }, [allowWrite]);

  const updateInvite = async () => {
    if (updateInProgress) {
      return toast.error(t("this_request_is_already_in_progress"));
    }

    try {
      setUpdateInProgress(true);
      const response = await axios.put(
        endpoints.PUT_GRANTED_PERMISSIONS_PROJECT_INVITE,
        {
          id: invite_id,
          manager: isManager,
          write_allowed: allowWrite
        }
      );
      if (responseIsStatusOk(response)) {
        toast.success(t("Updated"));
      } else {
        toast.error(t("Errors.Error"));
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setUpdateInProgress(false);
    }
  };

  const fetchInvite = async id => {
    try {
      setIsFetching(true);

      const response = await axios.get(
        endpoints.GET_GRANTED_PERMISSIONS_PROJECT_INVITE,
        { params: { id } }
      );

      if (
        responseIsStatusOk(response) &&
        response.data.data &&
        response.data.data.length > 0
      ) {
        const fetchedInvite = response.data.data[0];
        setInvite(fetchedInvite);
        setIsManager(fetchedInvite.manager);
        setAllowWrite(fetchedInvite.write_allowed);
      } else {
        setInvite(null);
        toast.error(t("Errors.Error"));
      }
    } catch (e) {
      if (errorIsEntityNotFound(e)) {
        setInvite(null);
        toast.error(t("not_found"));
      } else {
        toast.error(t("Errors.Error"));
      }
    } finally {
      setIsFetching(false);
    }
  };

  return (
    <PrivatePage
      onLoginConfirmed={() => {
        fetchInvite(invite_id);
      }}
    >
      <WithFetch fetchList={[constants.FETCH_PROJECT]}>
        {isFetching && <Loading />}

        {!isFetching && invite === null && <div>{t("not_found")}</div>}

        {!isFetching && invite && (
          <>
            <h1>{`${t("editing_invite")} ${t("of_lc")} ${
              invite.email ? invite.email : uiUserFromUser(invite.user)
            } ${t("to_lc")} ${projectName}`}</h1>
            <form
              onSubmit={e => {
                e.preventDefault();
                updateInvite();
              }}
            >
              <fieldset disabled={updateInProgress}>
                <Checkbox
                  disabled={updateInProgress}
                  label={t("allow_writing")}
                  checked={allowWrite}
                  onChange={e => setAllowWrite(e.target.checked)}
                />
                <Checkbox
                  disabled={updateInProgress}
                  label={t("project_manager")}
                  checked={isManager}
                  onChange={e => setIsManager(e.target.checked)}
                />
                <div style={{ paddingTop: 12 }}>
                  <Button1 disabled={updateInProgress}>{t("update")}</Button1>
                </div>
              </fieldset>
            </form>
          </>
        )}
      </WithFetch>
    </PrivatePage>
  );
}

export default connect(state => ({
  projectName: state.project.info && state.project.info.name
}))(ProjectPredefinedPermissionsInviteEdit);
