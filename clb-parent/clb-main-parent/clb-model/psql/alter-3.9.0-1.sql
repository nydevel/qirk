ALTER TABLE organization ADD COLUMN frozen BOOL NOT NULL DEFAULT false;


ALTER TABLE project ALTER COLUMN frozen SET NOT NULL;
ALTER TABLE project ADD COLUMN key VARCHAR(7) NOT NULL DEFAULT '';


CREATE TABLE imported_jira_project(
	organization_id   BIGINT       NOT NULL,
	project_id        BIGINT       NOT NULL,
	upload_timestamp  BIGINT       NOT NULL,
	jira_project_id   BIGINT       NOT NULL,
	jira_project_key  VARCHAR(7)   NOT NULL
);
ALTER TABLE imported_jira_project ADD CONSTRAINT imported_jira_project__organization_id__jira_project_id_uniq UNIQUE (organization_id, jira_project_id);


CREATE TABLE imported_jira_task(
	project_id        BIGINT       NOT NULL,
	task_id           BIGINT       NOT NULL,
	jira_task_id      BIGINT       NOT NULL
);

