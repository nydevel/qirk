import React, { useState, useEffect } from "react";
import { connect } from "react-redux";
import axios from "./../../utils/axios";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import WithFetch from "../../components/WithFetch/WithFetch";
import constants from "../../utils/constants";
import { responseIsStatusOk } from "../../utils/variousUtils";
import ImportStepper from "./ImportStepper";
import Loading from "../../components/Loading/Loading";
import endpoints from "../../utils/endpoints";
import paths from "../../routes/paths";
import { toast } from "react-toastify";
import { useTranslation } from "react-i18next";

const JiraImportMappingPage = ({
  orgId,
  orgUiId,
  loadingOrg,
  history,
  match: {
    params: { organization_uiid, timestamp }
  }
}) => {
  const { t } = useTranslation();

  const [projects, setProjects] = useState(null);
  const [loading, setLoading] = useState(false);
  const [selectedProjects, setSelectedProjects] = useState([]);
  const [selectedProjectsData, setSelectedProjectsData] = useState({});
  const [loadingSelectedProjects, setLoadingSelectedProjects] = useState([]);

  const [modelProjects, setModelProjects] = useState([]);
  const [modelMembers, setModelMembers] = useState([]);

  const [importedProjects, setImportedProjects] = useState([]);
  const [importedStatuses, setImportedStatuses] = useState([]);
  const [importedPriorities, setImportedPriorities] = useState([]);
  const [importedTypes, setImportedTypes] = useState([]);
  const [importedUsers, setImportedUsers] = useState([]);

  const [mappedProjects, setMappedProjects] = useState({});
  const [mappedStatuses, setMappedStatuses] = useState({});
  const [mappedPriorities, setMappedPriorities] = useState({});
  const [mappedTypes, setMappedTypes] = useState({});
  const [mappedUsers, setMappedUsers] = useState({});

  const [postingJiraImport, setPostingJiraImport] = useState(false);

  useEffect(() => {
    if (
      selectedProjects &&
      selectedProjects.length > 0 &&
      !selectedProjects.some(pId => {
        const projectNotFound = !selectedProjectsData[pId];
        return projectNotFound;
      })
    ) {
      // all selected project's data found

      const newStatuses = [];
      const newPriorities = [];
      const newTypes = [];
      const newUsers = [];
      const newProjects = [];
      const newModelProjects = [];
      const newModelMembers = [];

      selectedProjects.forEach(pId => {
        const projectData = selectedProjectsData[pId];
        const {
          statuses,
          priorities,
          types,
          users,
          projects,
          qirk: { members: qirk_members, imported_projects: qirk_projects }
        } = projectData;

        // statuses example: [ {id: "10000", name: "To Do"} ]

        projects.forEach(item => {
          const alreadyAdded = newProjects.some(st => st.id === item.id);

          if (!alreadyAdded) {
            newProjects.push(item);
          }
        });

        statuses.forEach(status => {
          const alreadyAdded = newStatuses.some(st => st.id === status.id);

          if (!alreadyAdded) {
            newStatuses.push(status);
          }
        });

        priorities.forEach(item => {
          const alreadyAdded = newPriorities.some(st => st.id === item.id);

          if (!alreadyAdded) {
            newPriorities.push(item);
          }
        });

        types.forEach(item => {
          const alreadyAdded = newTypes.some(st => st.id === item.id);

          if (!alreadyAdded) {
            newTypes.push(item);
          }
        });

        users.forEach(item => {
          const alreadyAdded = newUsers.some(st => st.id === item.id);

          if (!alreadyAdded) {
            newUsers.push(item);
          }
        });

        qirk_members.forEach(item => {
          const alreadyAdded = newModelMembers.some(st => st.id === item.id);

          if (!alreadyAdded) {
            newModelMembers.push(item);
          }
        });

        qirk_projects.forEach(item => {
          const alreadyAdded = newModelProjects.some(st => st.id === item.id);

          if (!alreadyAdded) {
            newModelProjects.push(item);
          }
        });
      });

      setImportedStatuses(newStatuses);
      setImportedPriorities(newPriorities);
      setImportedTypes(newTypes);
      setImportedUsers(newUsers);
      setImportedProjects(newProjects);
      setModelMembers(newModelMembers);
      setModelProjects(newModelProjects);

      setMappedProjects(() =>
        newProjects.reduce((acc, curr) => {
          const similarProject = newModelProjects.find(
            modelP => modelP.name === curr.name
          );

          acc[curr.id] = similarProject ? similarProject.id : -1;

          return acc;
        }, {})
      );
    }
  }, [selectedProjects, selectedProjectsData]);

  useEffect(() => {
    function getProjects() {
      setLoading(true);

      axios
        .get(endpoints.GET_JIRA_IMPORT_LIST_PROJECTS, {
          params: { organization_id: orgId, timestamp }
        })
        .then(response => {
          if (responseIsStatusOk(response)) {
            const projects = response.data.data;

            setSelectedProjects(projects.map(p => p.id));
            setProjects(projects);
          }
        })

        .catch(e => {
          console.error(e);
        })
        .finally(() => {
          setLoading(false);
        });
    }

    if (orgId && timestamp && orgUiId === organization_uiid) {
      getProjects();
    }
  }, [organization_uiid, timestamp, orgId, orgUiId]);

  function getProjectsData(project_id) {
    setLoadingSelectedProjects(old => [...old, project_id]);

    axios
      .get(endpoints.GET_JIRA_IMPORT_LIST_PROJECTS_DATA, {
        params: { organization_id: orgId, timestamp, project_id }
      })
      .then(response => {
        if (
          responseIsStatusOk(response) &&
          response &&
          response.data &&
          response.data.data &&
          response.data.data[0]
        ) {
          setSelectedProjectsData(oldData => ({
            ...oldData,
            [project_id]: response.data.data[0]
          }));
          setLoadingSelectedProjects(oldSelectedProjects =>
            oldSelectedProjects.filter(pId => pId !== project_id)
          );
        }
      })
      .catch(e => {
        console.error(e);
      });
  }

  const loadingAnything = loading || loadingOrg;

  return (
    <PrivatePage>
      <WithFetch fetchList={[constants.FETCH_ORGANIZATION]}>
        {loadingAnything && <Loading />}
        {!loadingAnything && (
          <ImportStepper
            projects={projects}
            selectedProjects={selectedProjects}
            setSelectedProjects={setSelectedProjects}
            getSelectedProjectsData={() =>
              selectedProjects.forEach(pId => getProjectsData(pId))
            }
            loadingSelectedProjects={loadingSelectedProjects.length > 0}
            importedStatuses={importedStatuses}
            mappedStatuses={mappedStatuses}
            setMappedStatuses={setMappedStatuses}
            importedPriorities={importedPriorities}
            mappedPriorities={mappedPriorities}
            setMappedPriorities={setMappedPriorities}
            importedTypes={importedTypes}
            mappedTypes={mappedTypes}
            setMappedTypes={setMappedTypes}
            modelMembers={modelMembers}
            importedUsers={importedUsers}
            mappedUsers={mappedUsers}
            setMappedUsers={setMappedUsers}
            importedProjects={importedProjects}
            mappedProjects={mappedProjects}
            modelProjects={modelProjects}
            setMappedProjects={setMappedProjects}
            postingJiraImport={postingJiraImport}
            postJiraImport={async () => {
              try {
                setPostingJiraImport(true);

                const response = await axios.post(endpoints.POST_JIRA_IMPORT, {
                  organization: orgId,
                  timestamp: timestamp,
                  projects: selectedProjects,
                  override_description: true,
                  override_types: true,
                  override_priorities: true,
                  override_statuses: true,
                  override_assignees: true,
                  types: mappedTypes,
                  statuses: mappedStatuses,
                  priorities: mappedPriorities,
                  members: mappedUsers,
                  project_mapping: Object.keys(mappedProjects)
                    .map(jiraPId => {
                      const qirkPId = mappedProjects[jiraPId];
                      if (!qirkPId || qirkPId === -1) {
                        return false;
                      }
                      return { key: jiraPId, value: qirkPId };
                    })
                    .filter(Boolean)
                    .reduce((acc, curr) => {
                      acc[curr.key] = curr.value;
                      return acc;
                    }, {})
                });

                if (responseIsStatusOk(response)) {
                  history.push(
                    paths.JiraImport.toPath({ organization_uiid: orgUiId })
                  );
                }
              } catch (e) {
                console.error(e);
                toast.error(t("Errors.Error"));
              } finally {
                setPostingJiraImport(false);
              }
            }}
          />
        )}
      </WithFetch>
    </PrivatePage>
  );
};

export default connect(state => ({}))(JiraImportMappingPage);
