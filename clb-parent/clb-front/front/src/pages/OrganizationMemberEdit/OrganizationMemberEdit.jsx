import React, { useState } from "react";
import { t } from "i18next";
import axios from "../../utils/axios";
import endpoints from "../../utils/endpoints";
import { connect } from "react-redux";
import { editOrganizationMember } from "../../actions/organizationActions";
import { withRouter } from "react-router-dom";
import Button from "../../components/Button/Button";
import { toast } from "react-toastify";
import actions from "../../utils/constants";
import useRequestResultToast from "../../utils/hooks/useRequestResultToast";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import { responseIsStatusOk, uiUserFromUser } from "../../utils/variousUtils";
import WithFetch from "../../components/WithFetch/WithFetch";
import constants from "../../utils/constants";
import Loading from "../../components/Loading/Loading";
import Checkbox from "../../components/Checkbox/Checkbox";

const mapStateToProps = state => ({
  currentUser: state.user,
  id: state.organization.info && state.organization.info.id,
  canManage: state.organization.info && state.organization.info.can_manage,
  requestStatus: state.organization.requestStatus
});

function OrganizationPeopleAdd({
  requestStatus,
  currentUser,
  canManage,
  match,
  editOrganizationMember
}) {
  const [isLoading, setIsLoading] = useState(true);
  const [uiUser, setUiUser] = useState("");
  const [userId, setUserId] = useState(-1);
  const [isEnabled, setIsEnabled] = useState(false);
  const [isManager, setIsManager] = useState(false);
  const [recordVersion, setRecordVersion] = useState("");
  useRequestResultToast(requestStatus);

  const fetchMember = async () => {
    try {
      const response = await axios.get(
        endpoints.ORG_MEMBER_BY_ID.replace(
          "{member_id}",
          match.params.member_id
        )
      );
      if (responseIsStatusOk(response)) {
        setIsManager(response.data.data[0].manager);
        setIsEnabled(response.data.data[0].enabled);
        setRecordVersion(response.data.data[0].record_version);
        setUiUser(uiUserFromUser(response.data.data[0].user));
        setUserId(response.data.data[0].user.id);
      }
      setIsLoading(false);
    } catch (e) {
      console.error("e", e);
      toast.error(t("Errors.FetchFail"));
    }
  };

  const onEdit = async e => {
    e.preventDefault();
    const data = {
      id: match.params.member_id,
      manager: isManager,
      enabled: isEnabled,
      record_version: recordVersion
    };
    await editOrganizationMember(data);
  };

  return (
    <PrivatePage onLoginConfirmed={() => fetchMember()}>
      <WithFetch fetchList={[constants.FETCH_ORGANIZATION]}>
        <h1>
          {t("editing_organization_member")} {uiUser}
        </h1>
        {isLoading && <Loading />}
        {!isLoading && (
          <form onSubmit={onEdit}>
            <Checkbox
              label={t("Enabled")}
              disabled={userId === currentUser.id}
              checked={isEnabled}
              onChange={e => setIsEnabled(e.target.checked)}
            />
            <Checkbox
              label={t("Manager")}
              disabled={isManager && canManage && userId === currentUser.id}
              checked={isManager}
              onChange={e => setIsManager(e.target.checked)}
            />
            <div style={{ paddingTop: 15 }}>
              <Button
                type="submit"
                isLoading={requestStatus === actions.WAITING}
              >
                {t("Button.Save")}
              </Button>
            </div>
          </form>
        )}
      </WithFetch>
    </PrivatePage>
  );
}

export default withRouter(
  connect(
    mapStateToProps,
    { editOrganizationMember }
  )(OrganizationPeopleAdd)
);
