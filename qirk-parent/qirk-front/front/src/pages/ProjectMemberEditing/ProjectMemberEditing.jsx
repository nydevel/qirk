import React, { useState, useEffect } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import { editProjectMember } from "../../actions/projectActions";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import constants from "../../utils/constants";
import WithFetch from "../../components/WithFetch/WithFetch";
import Loading from "../../components/Loading/Loading";
import { uiUserFromUser } from "../../utils/variousUtils";
import Button1 from "../../components/Button/Button";
import Checkbox from "../../components/Checkbox/Checkbox";
import { useTranslation } from "react-i18next";

function ProjectMemberEditing({
  editingMemberInProgress,
  loadingMemberInProgress,
  initialData,
  successfullyLoaded,
  editProjectMember,
  match: { params }
}) {
  const{t}=useTranslation()
  const [isManager, setIsManager] = useState(false);
  const [writeAllowed, setWriteAllowed] = useState(false);

  useEffect(() => {
    if (
      initialData &&
      params.project_member_id &&
      parseInt(initialData.id) === parseInt(params.project_member_id)
    ) {
      setIsManager(initialData.manager);
      setWriteAllowed(initialData.write_allowed);
    }
  }, [initialData, params.project_member_id]);

  useEffect(() => {
    if (true === isManager) {
      setWriteAllowed(true);
    }
  }, [isManager]);

  useEffect(() => {
    if (false === writeAllowed) {
      setIsManager(false);
    }
  }, [writeAllowed]);

  const submitted = e => {
    e.preventDefault();
    editProjectMember({
      id: initialData.id,
      manager: isManager,
      write_allowed: writeAllowed
    });
  };

  return (
    <PrivatePage>
      <WithFetch
        fetchList={[constants.FETCH_PROJECT_MEMBER, constants.FETCH_PROJECT]}
      >
        {loadingMemberInProgress && <Loading />}
        {loadingMemberInProgress === false && true === successfullyLoaded && (
          <div>
            <h1>{`${t("editing_project_member")} ${initialData &&
              uiUserFromUser(initialData && initialData.user)}`}</h1>
            <form onSubmit={submitted}>
              <fieldset disabled={editingMemberInProgress}>
                <Checkbox
                  disabled={editingMemberInProgress}
                  label={t("writing_allowed")}
                  checked={writeAllowed}
                  onChange={e => setWriteAllowed(e.target.checked)}
                />
                <Checkbox
                  disabled={editingMemberInProgress}
                  label={t("project_manager")}
                  checked={isManager}
                  onChange={e => setIsManager(e.target.checked)}
                />

                <div style={{ paddingTop: 15 }}>
                  <Button1>{t("save")}</Button1>
                </div>
              </fieldset>
            </form>
          </div>
        )}
      </WithFetch>
    </PrivatePage>
  );
}

export default withRouter(
  connect(
    state => ({
      loadingMemberInProgress:
        state.project.selectedProjectMemberFetchStatus === constants.WAITING,
      editingMemberInProgress:
        state.project.selectedProjectMemberRequestStatus === constants.WAITING,
      initialData: state.project.selectedProjectMember,
      successfullyLoaded:
        state.project.selectedProjectMemberFetchStatus === constants.SUCCESS
    }),
    { editProjectMember }
  )(ProjectMemberEditing)
);
