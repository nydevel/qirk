CREATE TABLE project_task_number_sequence(
	id                  bigserial      PRIMARY KEY,
	record_version      BIGINT         NOT NULL,
	project_id          BIGINT         CONSTRAINT project_task_number_sequence__project_id_uniq UNIQUE NOT NULL,
	next_task_number    BIGINT         NOT NULL
);
ALTER TABLE project_task_number_sequence ADD CONSTRAINT project_task_number_sequence__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
INSERT INTO project_task_number_sequence(record_version, project_id, next_task_number)
	SELECT 1, project.id, COALESCE(SUM(version.next_task_number)-COUNT(version.next_task_number)+1, 0) FROM project LEFT JOIN version ON project.id = version.project_id GROUP BY project.id;


ALTER TABLE project ADD COLUMN task_number_sequence_id BIGINT;
UPDATE project SET task_number_sequence_id = (
	SELECT id FROM project_task_number_sequence WHERE project_task_number_sequence.project_id = project.id
);
ALTER TABLE project ALTER COLUMN task_number_sequence_id SET NOT NULL;
ALTER TABLE project ADD CONSTRAINT project__task_number_sequence_id__task_number_sequence_fkey FOREIGN KEY (task_number_sequence_id) REFERENCES project_task_number_sequence(id);
ALTER TABLE project ADD CONSTRAINT project__task_number_sequence_id__uniq UNIQUE (task_number_sequence_id);


ALTER TABLE project_task_number_sequence DROP COLUMN project_id;


ALTER TABLE task ADD COLUMN project_id BIGINT;
ALTER TABLE task ADD CONSTRAINT task__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
UPDATE task SET project_id = (
	SELECT project_id FROM version WHERE task.version_id = version.id
);
ALTER TABLE task ALTER COLUMN project_id SET NOT NULL;

ALTER TABLE task DROP CONSTRAINT task__version_id_number_uniq;
UPDATE task SET number = (
	SELECT COUNT(task2.id) FROM task as task2 WHERE task.project_id = task2.project_id AND task.id >= task2.id
);
ALTER TABLE task ADD CONSTRAINT task__project_id_number_uniq UNIQUE (project_id, number);

ALTER TABLE task ALTER COLUMN version_id DROP NOT NULL;
UPDATE task SET version_id = null;


ALTER TABLE version DROP COLUMN next_task_number;
ALTER TABLE version DROP COLUMN closed;

