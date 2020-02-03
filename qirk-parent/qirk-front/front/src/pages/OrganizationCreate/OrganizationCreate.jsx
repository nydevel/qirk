import React, { useState } from "react";
import Button from "../../components/Button/Button";
import { withRouter } from "react-router-dom";
import { createOrganization } from "../../actions/organizationActions";
import { connect } from "react-redux";
import actions from "../../utils/constants";
import useRequestResultToast from "../../utils/hooks/useRequestResultToast";
import InputWithValidation from "../../components/InputWithValidation/InputWithValidation";
import Select from "../../components/Select/Select";
import endpoints from "../../utils/endpoints";
import { t } from "i18next";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import WithFetch from "../../components/WithFetch/WithFetch";
import constants from "../../utils/constants";
import { langOptionsToIdArray } from "../../utils/variousUtils";
import TextInput from "../../components/TextInput/TextInput";
import Checkbox from "../../components/Checkbox/Checkbox";
import { toast } from "react-toastify";

const mapStateToProps = state => ({
  requestStatus: state.organization.requestStatus,
  languages: state.common.languages,
  languagesFetchInProgress:
    state.common.fetchLanguagesStatus === constants.WAITING
});

function OrganizationCreate({
  createOrganization,
  requestStatus,
  languages,
  languagesFetchInProgress
}) {
  const [orgName, setOrgName] = useState("");
  const [orgIsPrivate, setOrgIsPrivate] = useState(true);
  const [orgUIID, setOrgUIID] = useState("");
  const [UIIDTypingTimeout, setUIIDTypingTimeout] = useState(null);
  const [orgLanguages, setOrgLanguages] = useState([]);
  const [error, setError] = useState("");
  useRequestResultToast(requestStatus);

  const onUIIDBlur = () => {
    if (orgUIID.length === 0) {
      setError("");
    }
  };

  const onOrgCreate = async e => {
    e.preventDefault();

    if (!orgName.trim()) {
      return toast.error(t("required_fields_contain_only_spaces"));
    }

    if (!error) {
      const data = {
        name: orgName.trim(),
        ui_id: orgUIID.trim(),
        private: orgIsPrivate,
        languages: langOptionsToIdArray(orgLanguages)
      };
      await createOrganization(data);
    }
  };

  return (
    <PrivatePage className="organization-create">
      <WithFetch fetchList={[constants.LAZY_FETCH_LANGUAGES]}>
        <h1>{t("Organization create")}</h1>
        <form acceptCharset="UTF-8" onSubmit={e => onOrgCreate(e)}>
          <TextInput
            id="organization_name"
            maxLength={127}
            label={t("Organization name")}
            value={orgName}
            onChange={e => setOrgName(e.target.value)}
            required
          />
          <InputWithValidation
            id="ui_id"
            pattern={constants.REGEX_UIID}
            maxLength="23"
            label={t("Organization uiid full")}
            onBlur={() => onUIIDBlur()}
            errorState={[error, setError]}
            value={orgUIID}
            onChange={setOrgUIID}
            typingTimeoutState={[UIIDTypingTimeout, setUIIDTypingTimeout]}
            title={t("UIID requirements")}
            url={endpoints.CHECK_ORG_UIID_AVAILABILITY}
            urlReplaceParam="{ui_id}"
            failedValidationText={t("Errors.UIIDTaken")}
            patternValidationText={t("UIID requirements")}
          />
          <Checkbox
            tooltip={{
              isTooltip: true,
              tooltipId: "private_checkbox_help",
              tooltipDataTip: t("private_checkbox_help")
            }}
            disabled={true}
            label={t("Organization private")}
            checked={orgIsPrivate}
            onChange={e => setOrgIsPrivate(e.target.checked)}
          />
          <Select
            label={t("Languages.self")}
            isMulti
            isLoading={languagesFetchInProgress}
            options={[...languages]}
            onChange={e => setOrgLanguages(e)}
            value={orgLanguages}
          />
          <div style={{ paddingTop: 30 }}>
            <Button
              type="submit"
              disabled={UIIDTypingTimeout}
              isLoading={requestStatus === actions.WAITING}
            >
              {t("Button.Create")}
            </Button>
          </div>
        </form>
      </WithFetch>
    </PrivatePage>
  );
}

export default withRouter(
  connect(
    mapStateToProps,
    { createOrganization }
  )(OrganizationCreate)
);
