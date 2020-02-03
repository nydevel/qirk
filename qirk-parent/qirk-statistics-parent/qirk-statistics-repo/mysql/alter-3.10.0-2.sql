ALTER TABLE new_task CHARACTER SET = 'utf8mb4';
ALTER TABLE new_task MODIFY project_name VARCHAR(127) CHARACTER SET utf8mb4 NOT NULL;

ALTER TABLE task_type_update CHARACTER SET = 'utf8mb4';
ALTER TABLE task_type_update MODIFY project_name VARCHAR(127) CHARACTER SET utf8mb4 NOT NULL;

ALTER TABLE task_priority_update CHARACTER SET = 'utf8mb4';
ALTER TABLE task_priority_update MODIFY project_name VARCHAR(127) CHARACTER SET utf8mb4 NOT NULL;

ALTER TABLE task_status_update CHARACTER SET = 'utf8mb4';
ALTER TABLE task_status_update MODIFY project_name VARCHAR(127) CHARACTER SET utf8mb4 NOT NULL;
