import React, { useState, useEffect } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { t } from "i18next";
import Button from "./../../components/Button/Button";
import endpoints from "./../../utils/endpoints";
import {
  fetchOrganizationById,
  editOrganization,
  setOrganizationInfoDispatch
} from "./../../actions/organizationActions";
import InputWithValidation from "./../../components/InputWithValidation/InputWithValidation";
import actions from "./../../utils/constants";
import useRequestResultToast from "./../../utils/hooks/useRequestResultToast";
import constants from "./../../utils/constants";
import paths from "./../../routes/paths";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import WithFetch from "../../components/WithFetch/WithFetch";
import Loading from "../../components/Loading/Loading";
import Select from "../../components/Select/Select";
import {
  langOptionsToIdArray,
  langIdArrayToOptions
} from "../../utils/variousUtils";
import TextInput from "../../components/TextInput/TextInput";
import Checkbox from "../../components/Checkbox/Checkbox";
import { toast } from "react-toastify";
import GoBackButton from "../../components/GoBackButton/GoBackButton";

function OrganizationEdit({
  orgId,
  requestStatus,
  editOrganization,
  info,
  isLoading,
  history,
  languages,
  languagesFetchInProgress,
  match: { params }
}) {
  const [orgName, setOrgName] = useState("");
  const [orgIsPrivate, setOrgIsPrivate] = useState(false);
  const [orgUIID, setOrgUIID] = useState("");
  const [orgLanguages, setOrgLanguages] = useState([]);
  const [UIIDTypingTimeout, setUIIDTypingTimeout] = useState(null);
  const [recordVersion, setRecordVersion] = useState(-1);
  const [error, setError] = useState("");

  useRequestResultToast(requestStatus);

  useEffect(() => {
    if (info && info.languages && info.languages.length > 0) {
      setOrgLanguages(langIdArrayToOptions(info.languages, languages));
    } else {
      setOrgLanguages([]);
    }
  }, [info, info && info.languages, languages && languages.length]);

  useEffect(() => {
    if (info && info.record_version) {
      setRecordVersion(info.record_version);
    }
  }, [info, info && info.record_version]);

  useEffect(() => {
    if (info && info.name) {
      setOrgName(info.name);
    }
  }, [info, info && info.name]);

  useEffect(() => {
    if (info && info.ui_id) {
      setOrgUIID(info.ui_id);
    }
  }, [info, info && info.ui_id]);

  useEffect(() => {
    if (info) {
      setOrgIsPrivate(info.is_private);
    }
  }, [info]);

  useEffect(() => {
    if (info && info.ui_id) {
      history.push(
        paths.OrganizationEdit.toPath({ organization_uiid: info.ui_id })
      );
    }
  }, [info && info.ui_id]);

  const onUIIDBlur = () => {
    if (orgUIID.length === 0 || orgUIID === info.ui_id) {
      setError("");
    }
  };

  const onOrgEdit = async e => {
    e.preventDefault();

    if (!orgName.trim()) {
      return toast.error(t("required_fields_contain_only_spaces"));
    }

    if (!error) {
      const data = {
        id: orgId,
        name: orgName.trim(),
        ui_id: orgUIID.trim(),
        private: orgIsPrivate,
        languages: langOptionsToIdArray(orgLanguages),
        record_version: recordVersion,
        purge_on_delete: false
      };
      await editOrganization(data);
    }
  };
  return (
    <PrivatePage className="organization-edit">
      <WithFetch
        fetchList={[
          constants.LAZY_FETCH_LANGUAGES,
          constants.FETCH_ORGANIZATION
        ]}
      >
        <div>
          <GoBackButton
            pathToBack={paths.OrganizationOverview.toPath({
              organization_uiid: params.organization_uiid || "organization_uiid"
            })}
          />
        </div>
        <h1 style={{ marginTop: "0" }}>{t("Organization edit")}</h1>
        {isLoading === true && <Loading />}
        {info && isLoading === false && (
          <form acceptCharset="UTF-8" onSubmit={onOrgEdit}>
            <fieldset disabled={requestStatus === actions.WAITING}>
              <TextInput
                id="organization_name"
                label={t("Organization name")}
                maxLength={127}
                value={orgName}
                onChange={e => setOrgName(e.target.value)}
                required
              />
              <InputWithValidation
                name="ui_id"
                pattern={constants.REGEX_UIID}
                maxLength="23"
                label={t("Organization uiid")}
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
                defaultChecked={orgIsPrivate}
                onChange={e => setOrgIsPrivate(e.target.checked)}
              />
              <div style={{ paddingBottom: 20 }}>
                <Select
                  label={t("Languages.self")}
                  isMulti
                  isLoading={languagesFetchInProgress}
                  options={[...languages]}
                  onChange={e => setOrgLanguages(e)}
                  value={orgLanguages}
                />
              </div>
              <Button
                type="submit"
                disabled={UIIDTypingTimeout}
                isLoading={requestStatus === actions.WAITING}
              >
                {t("Button.Update")}
              </Button>
            </fieldset>
          </form>
        )}
      </WithFetch>
    </PrivatePage>
  );
}

export default withRouter(
  connect(
    state => ({
      orgId: state.organization.info && state.organization.info.id,
      info: state.organization.info,
      fetchStatus: state.organization.fetchStatus,
      requestStatus: state.organization.requestStatus,
      isLoading: state.organization.fetchStatus === constants.WAITING,
      languages: state.common.languages,
      languagesFetchInProgress:
        state.common.fetchLanguagesStatus === constants.WAITING
    }),
    {
      fetchOrganizationById,
      editOrganization,
      setOrganizationInfoDispatch
    }
  )(OrganizationEdit)
);
