ALTER TABLE project_task_number_sequence DROP COLUMN record_version;


CREATE TABLE task_subscriber(
	task_id  BIGINT  NOT NULL,
	user_id  BIGINT  NOT NULL
);
ALTER TABLE task_subscriber ADD CONSTRAINT task_subscriber__task_id__task_fkey FOREIGN KEY (task_id) REFERENCES task(id);
CREATE INDEX task_subscriber__task_id_idx ON task_subscriber(task_id);
ALTER TABLE task_subscriber ADD CONSTRAINT task_subscriber__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);

