ALTER TABLE project ADD COLUMN frozen BOOL DEFAULT false;


CREATE TABLE temporary_attachment(
	uuid                 VARCHAR(36)    CONSTRAINT temporary_attachment__uuid_uniq UNIQUE NOT NULL,
	filename             VARCHAR(511)   NOT NULL,
	path                 VARCHAR(511)   NOT NULL,
	project_id           BIGINT         NOT NULL,
	dropbox_settings_id  BIGINT         NOT NULL,
	created_at           BIGINT         NOT NULL
);
ALTER TABLE temporary_attachment ADD CONSTRAINT temporary_attachment__project_id__dropbox_settings_fkey FOREIGN KEY (project_id) REFERENCES dropbox_settings(id);
ALTER TABLE temporary_attachment ADD CONSTRAINT temporary_attachment__dropbox_settings_id__dropbox_settings_fk FOREIGN KEY (dropbox_settings_id) REFERENCES project(id);
CREATE INDEX temporary_attachment__created_at_idx ON temporary_attachment(created_at);

