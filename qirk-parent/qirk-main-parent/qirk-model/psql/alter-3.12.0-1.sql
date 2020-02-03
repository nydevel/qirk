UPDATE project SET private = true;


CREATE INDEX task_card__status_idx ON task_card(status);


ALTER TABLE imported_jira_project ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE;
UPDATE imported_jira_project SET updated_at = to_timestamp(upload_timestamp / 1000);
ALTER TABLE imported_jira_project ADD COLUMN jira_project_name VARCHAR(80);
UPDATE imported_jira_project SET jira_project_name = jira_project_key;


CREATE TABLE jira_upload(
	organization_id   BIGINT        NOT NULL,
	upload_timestamp  BIGINT        NOT NULL,
	archive_filename  VARCHAR(511)  NOT NULL
);
ALTER TABLE jira_upload ADD CONSTRAINT jira_upload__organization_id__upload_timestamp_uniq UNIQUE (organization_id, upload_timestamp);

