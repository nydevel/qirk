CREATE DATABASE clb_stat;
GRANT ALL PRIVILEGES ON clb_stat.* to hibernate@'%' IDENTIFIED BY '121212';
GRANT ALL PRIVILEGES ON clb_stat.* to hibernate@'localhost' IDENTIFIED BY '121212';

\r clb_stat


CREATE TABLE new_user(
	uuid            VARCHAR(36)    NOT NULL,
	visited_at      TIMESTAMP      NOT NULL
) ENGINE = InnoDB;

CREATE TABLE user_registration(
	uuid            VARCHAR(36)    NOT NULL,
	registered_at   TIMESTAMP      NOT NULL,
	user_id         BIGINT         NOT NULL
) ENGINE = InnoDB;

CREATE TABLE new_task(
	task_id         BIGINT         NOT NULL,
	created_at      TIMESTAMP      NOT NULL,
	type            VARCHAR(14)    NOT NULL,
	priority        VARCHAR(14)    NOT NULL,
	status          VARCHAR(14)    NOT NULL
) ENGINE = InnoDB;

CREATE TABLE task_type_update(
	task_id         BIGINT         NOT NULL,
	updated_at      TIMESTAMP      NOT NULL,
	type            VARCHAR(14)    NOT NULL
) ENGINE = InnoDB;

CREATE TABLE task_priority_update(
	task_id         BIGINT         NOT NULL,
	updated_at      TIMESTAMP      NOT NULL,
	priority        VARCHAR(14)    NOT NULL
) ENGINE = InnoDB;

CREATE TABLE task_status_update(
	task_id         BIGINT         NOT NULL,
	updated_at      TIMESTAMP      NOT NULL,
	status          VARCHAR(14)    NOT NULL
) ENGINE = InnoDB;
