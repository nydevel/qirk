CREATE TABLE task_update(
	task_id         BIGINT         NOT NULL,
	updated_at      TIMESTAMP      NOT NULL
) ENGINE = InnoDB;
INSERT INTO task_update(task_id, updated_at)
	SELECT task_id, updated_at FROM task_type_update;
INSERT INTO task_update(task_id, updated_at)
	SELECT task_id, updated_at FROM task_priority_update;
INSERT INTO task_update(task_id, updated_at)
	SELECT task_id, updated_at FROM task_status_update;
