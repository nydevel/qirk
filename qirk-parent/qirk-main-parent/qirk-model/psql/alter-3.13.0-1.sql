DROP INDEX task_card__status_idx;


UPDATE imported_jira_project SET updated_at = to_timestamp(upload_timestamp / 1000) WHERE updated_at IS NULL;
UPDATE imported_jira_project ALTER COLUMN updated_at SET NOT NULL;

UPDATE imported_jira_project SET jira_project_name = jira_project_key WHERE jira_project_name IS NULL;
UPDATE imported_jira_project ALTER COLUMN jira_project_name SET NOT NULL;

ALTER TABLE imported_jira_project DROP CONSTRAINT imported_jira_project__organization_id__project_id_uniq;
CREATE INDEX imported_jira_project__organization_id_idx ON imported_jira_project(organization_id);

