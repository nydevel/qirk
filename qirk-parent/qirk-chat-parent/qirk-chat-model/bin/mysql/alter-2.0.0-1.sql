CREATE TABLE issue_message(
	uuid            VARCHAR(36)    NOT NULL,
	chat_id         BIGINT         NOT NULL,
	sender_id       BIGINT         NOT NULL,
	timestamp       BIGINT         NOT NULL,
	message         VARCHAR(1023)  NOT NULL,
	deleted         BOOLEAN,
	updated_at      BIGINT,
	in_response_to  BIGINT,
	CONSTRAINT task_message_pk PRIMARY KEY (chat_id, timestamp)
) ENGINE = InnoDB;


CREATE TABLE project_message(
	uuid            VARCHAR(36)    NOT NULL,
	chat_id         BIGINT         NOT NULL,
	sender_id       BIGINT         NOT NULL,
	timestamp       BIGINT         NOT NULL,
	message         VARCHAR(1023)  NOT NULL,
	deleted         BOOLEAN,
	updated_at      BIGINT,
	in_response_to  BIGINT,
	CONSTRAINT task_message_pk PRIMARY KEY (chat_id, timestamp)
) ENGINE = InnoDB;
