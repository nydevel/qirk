DELETE FROM new_task;
ALTER TABLE new_task ADD COLUMN project_id BIGINT;
ALTER TABLE new_task ADD COLUMN project_name VARCHAR(127);

DELETE FROM task_update;
ALTER TABLE task_update ADD COLUMN project_id BIGINT;
ALTER TABLE task_update ADD COLUMN project_name VARCHAR(127);

DELETE FROM task_type_update;
ALTER TABLE task_type_update ADD COLUMN project_id BIGINT;
ALTER TABLE task_type_update ADD COLUMN project_name VARCHAR(127);

DELETE FROM task_priority_update;
ALTER TABLE task_priority_update ADD COLUMN project_id BIGINT;
ALTER TABLE task_priority_update ADD COLUMN project_name VARCHAR(127);

DELETE FROM task_status_update;
ALTER TABLE task_status_update ADD COLUMN project_id BIGINT;
ALTER TABLE task_status_update ADD COLUMN project_name VARCHAR(127);
