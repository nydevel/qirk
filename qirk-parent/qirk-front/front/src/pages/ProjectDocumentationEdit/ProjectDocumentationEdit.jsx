import React, { useState } from "react";
import { connect } from "react-redux";
import { updateProjectDocumentation } from "../../actions/projectActions";
import WithFetch from "../../components/WithFetch/WithFetch";
import constants from "../../utils/constants";
import Loading from "../../components/Loading/Loading";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import Button1 from "../../components/Button/Button";
import TextInput from "../../components/TextInput/TextInput";
import { useTranslation } from "react-i18next";

function ProjectDocumentationEdit({
  projectInfo,
  loadingInProgress,
  canUpdateProject,
  documentationRequestStatus,
  updateProjectDocumentation
}) {
  const{t}=useTranslation()
  const [documentation, setDocumentation] = useState("");
  useState(() => {
    setDocumentation(projectInfo && projectInfo.documentation_md);
  }, [projectInfo && projectInfo.documentation_md]);

  const patchDocumentation = e => {
    e.preventDefault();
    updateProjectDocumentation(documentation.trim());
  };

  return (
    <PrivatePage>
      <WithFetch fetchList={[constants.FETCH_PROJECT]}>
        {loadingInProgress && <Loading />}
        {!loadingInProgress && projectInfo && (
          <div>
            <h1>
              {projectInfo.name}: {t("documentation_editing")}
            </h1>
            <form onSubmit={patchDocumentation}>
              <fieldset
                disabled={
                  documentationRequestStatus === constants.WAITING ||
                  false === canUpdateProject
                }
              >
                <TextInput
                  textarea
                  textFieldStyle={{ width: "100%" }}
                  label={t("documentation_label_name")}
                  value={documentation || ""}
                  onChange={e => setDocumentation(e.target.value)}
                />
                <div>
                  <Button1 type="submit">{t("Update")}</Button1>
                </div>
              </fieldset>
            </form>
          </div>
        )}
      </WithFetch>
    </PrivatePage>
  );
}

export default connect(
  state => ({
    projectInfo: state.project.info,
    documentationRequestStatus: state.project.documentationRequestStatus,
    canUpdateProject: state.project.info && state.project.info.can_manage,
    loadingInProgress: state.project.fetchStatus === constants.WAITING
  }),
  { updateProjectDocumentation }
)(ProjectDocumentationEdit);
