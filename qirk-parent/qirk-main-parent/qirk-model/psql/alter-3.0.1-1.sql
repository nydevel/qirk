CREATE INDEX login_statistics__user_id_idx ON login_statistics(user_id);


ALTER TABLE organization DROP COLUMN predefined_for_user_id;


CREATE TABLE task_hashtag(
	id               bigserial     PRIMARY KEY,
	project_id       BIGINT        NOT NULL,
	name             VARCHAR(127)  NOT NULL
);
ALTER TABLE task_hashtag ADD CONSTRAINT task_hashtag__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
CREATE INDEX task_hashtag__project_id_idx ON task_hashtag(project_id);
ALTER TABLE task_hashtag ADD CONSTRAINT task_hashtag__name_project_id_uniq UNIQUE (name, project_id);


CREATE TABLE task_hashtag__task(
	task_hashtag_id  BIGINT        NOT NULL,
	task_id          BIGINT        NOT NULL,
	CONSTRAINT task_hashtag__task__pkey PRIMARY KEY (task_id, task_hashtag_id)
);
ALTER TABLE task_hashtag__task ADD CONSTRAINT task_hashtag_task__task_id__task_fkey FOREIGN KEY (task_id) REFERENCES task(id);
ALTER TABLE task_hashtag__task ADD CONSTRAINT task_hashtag_task__task_hashtag_id__task_hashtag_fkey FOREIGN KEY (task_hashtag_id) REFERENCES task_hashtag(id);
CREATE INDEX task_hashtag_task__task_hashtag_id_idx ON task_hashtag__task(task_hashtag_id);

