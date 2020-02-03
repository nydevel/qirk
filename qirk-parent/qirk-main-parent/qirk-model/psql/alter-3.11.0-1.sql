ALTER TABLE project ADD COLUMN key2 VARCHAR(10) NOT NULL DEFAULT '';
UPDATE project SET key2 = key;
ALTER TABLE project RENAME COLUMN key TO key1;
ALTER TABLE project RENAME COLUMN key2 TO key;
ALTER TABLE project DROP COLUMN key1;
ALTER TABLE project ALTER COLUMN key DROP DEFAULT;


ALTER TABLE imported_jira_project ADD COLUMN jira_project_key2 VARCHAR(10) NOT NULL DEFAULT '';
UPDATE imported_jira_project SET jira_project_key2 = jira_project_key;
ALTER TABLE imported_jira_project RENAME COLUMN jira_project_key TO jira_project_key1;
ALTER TABLE imported_jira_project RENAME COLUMN jira_project_key2 TO jira_project_key;
ALTER TABLE imported_jira_project DROP COLUMN jira_project_key1;
ALTER TABLE imported_jira_project ALTER COLUMN jira_project_key DROP DEFAULT;

