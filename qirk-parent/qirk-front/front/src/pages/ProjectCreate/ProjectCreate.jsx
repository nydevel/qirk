import React, { useState, useEffect } from "react";
import { t } from "i18next";
import endpoints from "../../utils/endpoints";
import { connect } from "react-redux";
import Button from "../../components/Button/Button";
import { withRouter } from "react-router-dom";
import { createProject } from "../../actions/projectActions";
import InputWithValidation from "../../components/InputWithValidation/InputWithValidation";
import actions from "../../utils/constants";
import useRequestResultToast from "../../utils/hooks/useRequestResultToast";
import constants from "../../utils/constants";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import WithFetch from "../../components/WithFetch/WithFetch";
import Select from "../../components/Select/Select";
import {
  langOptionsToIdArray,
  responseIsStatusOk
} from "../../utils/variousUtils";
import CreatableChips from "../../components/CreatableChips/CreatableChips";
import TextInput from "../../components/TextInput/TextInput";
import Checkbox from "../../components/Checkbox/Checkbox";
import { toast } from "react-toastify";
import SimpleSelect from "../../components/SimpleSelect/SimpleSelect";
import axios from "../../utils/axios";
import Loading from "../../components/Loading/Loading";
import queryString from "query-string";

const mapStateToProps = state => ({
  orgId: state.organization.id,
  requestStatus: state.organization.requestStatus,
  languages: state.common.languages,
  languagesFetchInProgress:
    state.common.fetchLanguagesStatus === constants.WAITING
});

function ProjectsCreate({
  createProject,
  match: { params },
  requestStatus,
  languages,
  languagesFetchInProgress,
  location
}) {
  const parsed = queryString.parse(location.search);

  const [loginConfirmed, setLoginConfirmed] = useState(false);
  const [predefinedOrganizationMode, setPredefinedOrganizationMode] = useState(
    false
  );
  const [loadingOrgs, setLoadingOrgs] = useState(false);
  const [orgOptions, setOrgOptions] = useState([]);
  const [selectedOrg, setSelectedOrg] = useState(null);
  const [name, setName] = useState("");
  const [projectUIID, setProjectUIID] = useState("");
  const [isPrivate, setIsPrivate] = useState(true);
  const [makeMeMember, setMakeMeMember] = useState(true);
  const [description, setDescription] = useState("");
  const [tags, setTags] = useState([]);
  const [projectLanguages, setProjectLanguages] = useState([]);
  const [UIIDTypingTimeout, setUIIDTypingTimeout] = useState(null);
  const [error, setError] = useState("");
  useRequestResultToast(requestStatus);

  useEffect(() => {
    if (params && params.organization_uiid) {
      setPredefinedOrganizationMode(true);
    } else {
      setPredefinedOrganizationMode(false);
    }
  }, [params]);

  useEffect(() => {
    if (loginConfirmed && !predefinedOrganizationMode) {
      fetchOrganizations();
    }
  }, [loginConfirmed, predefinedOrganizationMode]);

  useEffect(() => {
    if (
      orgOptions &&
      orgOptions.length > 0 &&
      !predefinedOrganizationMode &&
      null === selectedOrg
    ) {
      setSelectedOrg(orgOptions[0]);
    }
  }, [orgOptions, selectedOrg, predefinedOrganizationMode]);

  const onProjectSubmit = async e => {
    e.preventDefault();

    if (!name.trim()) {
      return toast.error(t("required_fields_contain_only_spaces"));
    }

    if (!error) {
      const data = {
        name: name.trim(),
        ui_id: projectUIID.trim(),
        organization: {
          ui_id: predefinedOrganizationMode
            ? params && params.organization_uiid
            : selectedOrg && selectedOrg.value
        },
        private: isPrivate,
        description: description.trim(),
        tags,
        languages: langOptionsToIdArray(projectLanguages),

        make_me_member: makeMeMember,
        redirect_to_defhomepage: parsed.redirect_to_defhomepage || false
      };
      await createProject(data);
    }
  };

  const onUIIDBlur = () => {
    if (projectUIID.length === 0) {
      setError("");
    }
  };

  const fetchOrganizations = async () => {
    try {
      setLoadingOrgs(true);
      const response = await axios.get(endpoints.ORGANIZATION_LIST, {
        params: { can_create_project: true }
      });

      if (responseIsStatusOk(response)) {
        setOrgOptions(
          response.data.data.map(org => ({
            value: org.ui_id,
            label: org.name
          }))
        );
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setLoadingOrgs(false);
    }
  };

  return (
    <PrivatePage
      onLoginConfirmed={() => setLoginConfirmed(true)}
      className="organization-project-create"
    >
      <WithFetch
        fetchList={[
          constants.FETCH_ORGANIZATION,
          constants.LAZY_FETCH_LANGUAGES
        ]}
      >
        <h1>{t("Project creation")}</h1>
        <form onSubmit={onProjectSubmit}>
          {loadingOrgs && <Loading />}
          {orgOptions &&
            orgOptions.length > 0 &&
            selectedOrg &&
            selectedOrg.value && (
              <div style={{ paddingBottom: 30 }}>
                <SimpleSelect
                  style={{ width: "100%" }}
                  label={t("Organization")}
                  value={selectedOrg && selectedOrg.value}
                  onChange={e => setSelectedOrg(e)}
                  options={orgOptions}
                />
              </div>
            )}
          <TextInput
            id="project_title"
            required
            label={t("Title")}
            maxLength="127"
            value={name}
            onChange={e => setName(e.target.value)}
          />
          <InputWithValidation
            id="project_uiid"
            name="ui_id"
            pattern={constants.REGEX_UIID}
            maxLength="23"
            placeholder={t("Project uiid")}
            failedValidationText={t("Errors.UIIDTaken")}
            patternValidationText={t("UIID requirements")}
            onBlur={() => onUIIDBlur()}
            errorState={[error, setError]}
            value={projectUIID}
            onChange={setProjectUIID}
            typingTimeoutState={[UIIDTypingTimeout, setUIIDTypingTimeout]}
            title={t("UIID requirements")}
            url={endpoints.CHECK_PROJECT_UIID}
            urlReplaceParam="{ui_id}"
            label={t("Project uiid full")}
          />
          <Checkbox
            tooltip={{
              isTooltip: true,
              tooltipId: "private_checkbox_help",
              tooltipDataTip: t("private_checkbox_help")
            }}
            disabled={true}
            label={t("Private project")}
            checked={isPrivate}
            onChange={e => setIsPrivate(e.target.checked)}
          />
          <div style={{ paddingBottom: 20, paddingTop: 5 }}>
            <CreatableChips
              value={tags}
              maxLength="127"
              style={{ paddingBottom: 15 }}
              onChange={e => setTags(e)}
              label={t("Technology tags")}
            />
          </div>
          <Select
            label={t("Languages.self")}
            isMulti
            isLoading={languagesFetchInProgress}
            options={[...languages]}
            onChange={e => setProjectLanguages(e)}
            value={projectLanguages}
          />
          <div
            style={{
              marginTop: 15
            }}
          >
            <TextInput
              id="project_description"
              className="projectDescription"
              textFieldStyle={{ width: "100%" }}
              textarea
              label={t("Description")}
              value={description}
              onChange={e => setDescription(e.target.value)}
            />
          </div>
          {!parsed.redirect_to_defhomepage && (
            <div style={{ paddingBottom: 5 }}>
              <Checkbox
                label={t("make_me_project_member")}
                name="make_me_project_member"
                checked={makeMeMember}
                onChange={e => setMakeMeMember(e.target.checked)}
              />
            </div>
          )}
          <Button
            type="submit"
            disabled={UIIDTypingTimeout || loadingOrgs}
            isLoading={requestStatus === actions.WAITING}
          >
            {t("Button.Create")}
          </Button>
        </form>
      </WithFetch>
    </PrivatePage>
  );
}

export default withRouter(
  connect(
    mapStateToProps,
    { createProject }
  )(ProjectsCreate)
);
