ALTER TABLE imported_jira_project ADD CONSTRAINT imported_jira_project__organization_id__organization_fkey FOREIGN KEY (organization_id) REFERENCES organization(id);
ALTER TABLE imported_jira_project ADD CONSTRAINT imported_jira_project__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


ALTER TABLE imported_jira_task ADD CONSTRAINT imported_jira_task__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE imported_jira_task ADD CONSTRAINT imported_jira_task__task_id__task_fkey FOREIGN KEY (task_id) REFERENCES task(id);
CREATE INDEX imported_jira_task__task_id_idx ON imported_jira_task(task_id);

