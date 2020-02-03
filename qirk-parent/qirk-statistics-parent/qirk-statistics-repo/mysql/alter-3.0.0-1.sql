CREATE TABLE feedback(
	sender_user_id  BIGINT         NOT NULL,
	feedback        VARCHAR(1023)  NOT NULL,
	created_at      TIMESTAMP      NOT NULL
) ENGINE = InnoDB;
