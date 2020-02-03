CREATE TABLE dialog_message(
	uuid            UUID,
	user1_id        BIGINT,
	user2_id        BIGINT,
	sender_id       BIGINT,
	timestamp       BIGINT,
	message         TEXT,
	deleted         BOOLEAN,
	updated_at      BIGINT,
	in_response_to  BIGINT,
	PRIMARY KEY ((user1_id, user2_id), timestamp)
);
