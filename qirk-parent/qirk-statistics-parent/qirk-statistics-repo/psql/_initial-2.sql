CREATE TABLE new_task(
	task_id         BIGINT                    NOT NULL,
	created_at      TIMESTAMP WITH TIME ZONE  NOT NULL,
	type            VARCHAR(14)               NOT NULL,
	priority        VARCHAR(14)               NOT NULL,
	status          VARCHAR(14)               NOT NULL,
	project_id      BIGINT                    NOT NULL,
	project_name    VARCHAR(127)              NOT NULL
);

CREATE TABLE task_type_update(
	task_id         BIGINT                    NOT NULL,
	updated_at      TIMESTAMP WITH TIME ZONE  NOT NULL,
	type            VARCHAR(14)               NOT NULL,
	project_id      BIGINT                    NOT NULL,
	project_name    VARCHAR(127)              NOT NULL
);

CREATE TABLE task_priority_update(
	task_id         BIGINT                    NOT NULL,
	updated_at      TIMESTAMP WITH TIME ZONE  NOT NULL,
	priority        VARCHAR(14)               NOT NULL,
	project_id      BIGINT                    NOT NULL,
	project_name    VARCHAR(127)              NOT NULL
);

CREATE TABLE task_status_update(
	task_id         BIGINT                    NOT NULL,
	updated_at      TIMESTAMP WITH TIME ZONE  NOT NULL,
	status          VARCHAR(14)               NOT NULL,
	project_id      BIGINT                    NOT NULL,
	project_name    VARCHAR(127)              NOT NULL
);

CREATE TABLE task_update(
	task_id         BIGINT                    NOT NULL,
	updated_at      TIMESTAMP WITH TIME ZONE  NOT NULL,
	project_id      BIGINT                    NOT NULL,
	project_name    VARCHAR(127)              NOT NULL
);

CREATE TABLE new_message(
	owner_type      VARCHAR(8)                NOT NULL,
	owner_id        BIGINT                    NOT NULL,
	created_at      TIMESTAMP WITH TIME ZONE  NOT NULL
);

CREATE TABLE new_memo(
	author_user_id  BIGINT                    NOT NULL,
	created_at      TIMESTAMP WITH TIME ZONE  NOT NULL
);

CREATE TABLE project_doc_update(
	updated_by_user_id  BIGINT                    NOT NULL,
	updated_at          TIMESTAMP WITH TIME ZONE  NOT NULL
);
