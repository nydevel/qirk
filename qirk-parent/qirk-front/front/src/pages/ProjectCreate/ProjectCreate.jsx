import React, { useState } from "react";

import endpoints from "../../utils/endpoints";
import { connect } from "react-redux";
import Button from "../../components/Button/Button";
import { withRouter } from "react-router-dom";
import { createProject } from "../../actions/projectActions";
import InputWithValidation from "../../components/InputWithValidation/InputWithValidation";
import actions from "../../utils/constants";
import constants from "../../utils/constants";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import WithFetch from "../../components/WithFetch/WithFetch";

import TextInput from "../../components/TextInput/TextInput";
import Checkbox from "../../components/Checkbox/Checkbox";
import { toast } from "react-toastify";
import queryString from "query-string";
import { useTranslation } from "react-i18next";

const mapStateToProps = state => ({
});

function ProjectsCreate({
  createProject,
  match: { params },
  requestStatus,
  languages,
  languagesFetchInProgress,
  location
}) {
  const{t}=useTranslation()
  const parsed = queryString.parse(location.search);

  const [name, setName] = useState("");
  const [projectUIID, setProjectUIID] = useState("");
  const [isPrivate, setIsPrivate] = useState(true);
  const [makeMeMember, setMakeMeMember] = useState(true);
  const [description, setDescription] = useState("");
  const [UIIDTypingTimeout, setUIIDTypingTimeout] = useState(null);
  const [error, setError] = useState("");

  const onProjectSubmit = async e => {
    e.preventDefault();

    if (!name.trim()) {
      return toast.error(t("required_fields_contain_only_spaces"));
    }

    if (!error) {
      const data = {
        name: name.trim(),
        ui_id: projectUIID.trim(),
        private: isPrivate,
        description: description.trim(),

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

  return (
    <PrivatePage className="organization-project-create">
      <WithFetch fetchList={[constants.LAZY_FETCH_LANGUAGES]}>
        <h1>{t("Project creation")}</h1>
        <form onSubmit={onProjectSubmit}>
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
            disabled={UIIDTypingTimeout}
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
  connect(mapStateToProps, { createProject })(ProjectsCreate)
);
