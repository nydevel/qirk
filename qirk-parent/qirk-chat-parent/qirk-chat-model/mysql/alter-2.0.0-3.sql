CREATE TABLE dialog_message(
	uuid            VARCHAR(36)    NOT NULL,
	user1_id        BIGINT         NOT NULL,
	user2_id        BIGINT         NOT NULL,
	sender_id       BIGINT         NOT NULL,
	timestamp       BIGINT         NOT NULL,
	message         VARCHAR(1023)  NOT NULL,
	deleted         BOOLEAN,
	updated_at      BIGINT,
	in_response_to  BIGINT,
	CONSTRAINT task_message_pk PRIMARY KEY (user1_id, user2_id, timestamp)
) ENGINE = InnoDB CHARACTER SET = 'utf8mb4';
