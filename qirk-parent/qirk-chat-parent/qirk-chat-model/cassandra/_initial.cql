CREATE KEYSPACE clb_chat WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1} AND DURABLE_WRITES = true;

USE clb_chat;


CREATE TABLE task_message(
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
