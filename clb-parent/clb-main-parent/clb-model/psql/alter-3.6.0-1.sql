CREATE TABLE task_link(
	task1_id  BIGINT  NOT NULL,
	task2_id  BIGINT  NOT NULL
);
ALTER TABLE task_link ADD CONSTRAINT task_link__task1_id__task_fkey FOREIGN KEY (task1_id) REFERENCES task(id);
ALTER TABLE task_link ADD CONSTRAINT task_link__task2_id__task_fkey FOREIGN KEY (task2_id) REFERENCES task(id);
CREATE INDEX task_link__task1_id_idx ON task_link(task1_id);

