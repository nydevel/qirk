CREATE DATABASE clb_chat;
SET storage_engine=InnoDB;
CREATE USER hibernate IDENTIFIED BY '121212';
GRANT ALL PRIVILEGES ON clb_chat.* to hibernate@'%' IDENTIFIED BY '121212';
GRANT ALL PRIVILEGES ON clb_chat.* to hibernate@'localhost' IDENTIFIED BY '121212';

\r clb_chat


CREATE TABLE task_message(
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
