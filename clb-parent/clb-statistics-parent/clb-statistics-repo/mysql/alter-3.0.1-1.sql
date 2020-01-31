CREATE TABLE first_login(
	user_id             BIGINT         NOT NULL,
	login_at            TIMESTAMP      NOT NULL
) ENGINE = InnoDB;

CREATE TABLE new_message(
	owner_type          VARCHAR(8)     NOT NULL,
	owner_id            BIGINT         NOT NULL,
	created_at          TIMESTAMP      NOT NULL
) ENGINE = InnoDB;

CREATE TABLE new_memo(
	author_user_id      BIGINT         NOT NULL,
	created_at          TIMESTAMP      NOT NULL
) ENGINE = InnoDB;

CREATE TABLE project_doc_update(
	updated_by_user_id  BIGINT         NOT NULL,
	updated_at          TIMESTAMP      NOT NULL
) ENGINE = InnoDB;
