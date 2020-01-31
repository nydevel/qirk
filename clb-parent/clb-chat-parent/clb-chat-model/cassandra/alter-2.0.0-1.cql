CREATE TABLE issue_message(
	uuid            UUID,
	chat_id         BIGINT,
	sender_id       BIGINT,
	timestamp       BIGINT,
	message         TEXT,
	deleted         BOOLEAN,
	updated_at      BIGINT,
	in_response_to  BIGINT,
	PRIMARY KEY (chat_id, timestamp)
);


CREATE TABLE project_message(
	uuid            UUID,
	chat_id         BIGINT,
	sender_id       BIGINT,
	timestamp       BIGINT,
	message         TEXT,
	deleted         BOOLEAN,
	updated_at      BIGINT,
	in_response_to  BIGINT,
	PRIMARY KEY (chat_id, timestamp)
);
