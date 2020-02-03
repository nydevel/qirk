import React, { useState } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { t } from "i18next";
import endpoints from "../../utils/endpoints";
import { addOrganizationMember } from "../../actions/organizationActions";
import Button from "../../components/Button/Button";
import SelectAsync from "../../components/SelectAsync/SelectAsync";
import useRequestResultToast from "../../utils/hooks/useRequestResultToast";
import actions from "../../utils/constants";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import WithFetch from "../../components/WithFetch/WithFetch";
import constants from "../../utils/constants";
import { uiUserFromUser } from "../../utils/variousUtils";
import Checkbox from "../../components/Checkbox/Checkbox";

function OrganizationMemberAdd({
  addOrganizationMember,
  requestStatus,
  match: { params }
}) {
  const [memberId, setMemberId] = useState("");
  const [isEnabled, setIsEnabled] = useState(true);
  const [isManager, setIsManager] = useState(false);
  useRequestResultToast(requestStatus);

  const onMemberSubmit = async e => {
    e.preventDefault();
    const data = {
      user: memberId.value,
      organization: { ui_id: params.organization_uiid },
      enabled: isEnabled,
      manager: isManager
    };
    await addOrganizationMember(data);
  };

  return (
    <PrivatePage>
      <WithFetch fetchList={[constants.FETCH_ORGANIZATION]}>
        <h1>{t("Adding a member")}</h1>
        <form onSubmit={onMemberSubmit}>
          <fieldset disabled={requestStatus === actions.WAITING}>
            <SelectAsync
              required
              label={t("Add member")}
              isMulti={false}
              value={memberId}
              onChange={e => setMemberId(e)}
              labelGenerator={e => uiUserFromUser(e)}
              listFetchUrl={endpoints.GET_USER_SEARCH_FOR_ORGANIZATION_WITH_OLD_PLACEHOLDERS.replace(
                "{org_uiid}",
                params.organization_uiid
              )}
              listReplaceWithValue={"{alias}"}
              placeholder=""
            />

            <div style={{ paddingTop: 15 }}>
              <Checkbox
                checked={isEnabled}
                label={t("Enabled")}
                onChange={e => setIsEnabled(e.target.checked)}
              />

              <Checkbox
                checked={isManager}
                onChange={e => setIsManager(e.target.checked)}
                label={t("Manager")}
              />

              <Button
                type="submit"
                isLoading={requestStatus === actions.WAITING}
              >
                {t("Button.Add")}
              </Button>
            </div>
          </fieldset>
        </form>
      </WithFetch>
    </PrivatePage>
  );
}

export default withRouter(
  connect(
    state => ({
      requestStatus: state.organization.requestStatus
    }),
    { addOrganizationMember }
  )(OrganizationMemberAdd)
);
