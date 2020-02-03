import React, { useState, useEffect } from "react";
import { t } from "i18next";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { updateProject } from "../../actions/projectActions";
import { setProjectInfoDispatch } from "../../actions/projectActions";
import Button from "../../components/Button/Button";
import InputWithValidation from "../../components/InputWithValidation/InputWithValidation";
import constants from "../../utils/constants";
import endpoints from "../../utils/endpoints";
import WithFetch from "../../components/WithFetch/WithFetch";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import Loading from "../../components/Loading/Loading";
import {
  tagsToOptions,
  langIdArrayToOptions,
  langOptionsToIdArray,
  takeItemFromLocalStorageSafe
} from "../../utils/variousUtils";
import Select from "../../components/Select/Select";
import TextInput from "../../components/TextInput/TextInput";
import Checkbox from "../../components/Checkbox/Checkbox";
import CreatableChips from "../../components/CreatableChips/CreatableChips";
import "./ProjectEdit.sass";
import { toast } from "react-toastify";
import paths from "../../routes/paths";
import GoBackButton from "../../components/GoBackButton/GoBackButton";
import queryString from "query-string";

const mapStateToProps = state => ({
  allLanguages: state.common.languages,
  languagesFetchInProgress:
    state.common.fetchLanguagesStatus === constants.WAITING,
  info: state.project.info,
  orgId: state.organization.id,
  orgUiid: state.organization.info && state.organization.info.ui_id,
  requestInProgress:
    state.project.updateProjectRequestStatus === constants.WAITING,
  fetchInProgress: state.project.fetchStatus === constants.WAITING
});

function ProjectsEdit({
  updateProject,
  languagesFetchInProgress,
  allLanguages,
  requestInProgress,
  info,
  fetchInProgress,
  match: { params }
}) {
  const [recordVersion, setRecordVersion] = useState(null);
  const [projectId, setProjectId] = useState(null);
  const [initialUiid, setInitialUiid] = useState("");
  const [name, setName] = useState("");
  const [projectUIID, setProjectUIID] = useState("");
  const [isPrivate, setIsPrivate] = useState(true);
  const [description, setDescription] = useState("");
  const [tags, setTags] = useState([]);
  const [languages, setLanguages] = useState([]);
  const [UIIDTypingTimeout, setUIIDTypingTimeout] = useState(null);
  const [error, setError] = useState("");
  const [canBePublic, setCanBePublic] = useState(false);
  const filterParams = takeItemFromLocalStorageSafe("filter_params", ""); //fulters from localStorage in URL to TaskSearch

  useEffect(() => {
    setRecordVersion(info && info.record_version);
  }, [info && info.record_version]);

  useEffect(() => {
    setProjectId(info && info.id);
  }, [info && info.id]);

  useEffect(() => {
    setName(info && info.name);
  }, [info && info.name]);

  useEffect(() => {
    setProjectUIID(info && info.ui_id);
  }, [info && info.ui_id]);

  useEffect(() => {
    setInitialUiid(info && info.ui_id);
  }, [info && info.ui_id]);

  useEffect(() => {
    setIsPrivate(info && info.private);
  }, [info && info.private]);

  useEffect(() => {
    setDescription(info && info.description_md);
  }, [info && info.description_md]);

  useEffect(() => {
    setCanBePublic(info && info.can_be_public);
  }, [info && info.can_be_public]);

  useEffect(() => {
    setTags(tagsToOptions(info && info.tags));
  }, [info && info.tags]);

  useEffect(() => {
    setLanguages(langIdArrayToOptions(info && info.languages));
  }, [info && info.languages]);

  const onProjectEdit = async e => {
    e.preventDefault();

    if (!name.trim()) {
      return toast.error(t("required_fields_contain_only_spaces"));
    }

    if (!error) {
      const data = {
        record_version: recordVersion,
        id: projectId,
        name: name.trim(),
        ui_id: projectUIID.trim(),
        private: isPrivate,
        description: description.trim(),
        tags,
        purge_on_delete: false,
        languages: langOptionsToIdArray(languages)
      };
      await updateProject(data);
    }
  };

  const onUIIDBlur = () => {
    if (projectUIID.length === 0 || projectUIID === initialUiid) {
      setError("");
    }
  };
  return (
    <PrivatePage className="organization-project-edit">
      <WithFetch
        fetchList={[constants.FETCH_PROJECT, constants.LAZY_FETCH_LANGUAGES]}
      >
        {fetchInProgress && <Loading />}
        {!fetchInProgress && info && (
          <div>
            <div>
              <GoBackButton
                pathToBack={`${paths.ProjectSingle.toPath({
                  organization_uiid: params && params.organization_uiid,
                  project_uiid: params && params.project_uiid
                })}?${queryString.stringify(filterParams, {
                  arrayFormat: "comma"
                })}`}
              />
            </div>
            <h1 style={{ marginTop: "0" }}>{t("Project edit")}</h1>
            <form onSubmit={onProjectEdit}>
              <fieldset disabled={requestInProgress}>
                <TextInput
                  id="project_title"
                  maxLength={127}
                  required
                  label={t("Title")}
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
                  label={t("Private project")}
                  checked={isPrivate}
                  onChange={e => setIsPrivate(e.target.checked)}
                  // disabled={!canBePublic && isPrivate}
                  disabled={true}
                />

                <div style={{ paddingBottom: 20, paddingTop: 5 }}>
                  <CreatableChips
                    label={t("Technology tags")}
                    maxLength={127}
                    value={tags}
                    onChange={e => setTags(e)}
                  />
                </div>

                <label>
                  {t("Languages.self")}
                  <Select
                    isMulti
                    isLoading={languagesFetchInProgress}
                    options={[...allLanguages]}
                    onChange={e => setLanguages(e)}
                    value={languages}
                  />
                </label>
                <div
                  style={{
                    marginTop: 15
                  }}
                >
                  <TextInput
                    id="project_description"
                    className="projectDescription"
                    textFieldStyle={{ width: "100%" }}
                    maxLength={10000}
                    textarea
                    label={t("Description")}
                    value={description}
                    onChange={e => setDescription(e.target.value)}
                  />
                </div>
                <Button type="submit">{t("Button.Update")}</Button>
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
    mapStateToProps,
    {
      updateProject,
      setProjectInfoDispatch
    }
  )(ProjectsEdit)
);
