CREATE TABLE notification_unsubscription(
	user_id             BIGINT         NOT NULL,
	notification_type   VARCHAR(127)   NOT NULL
) ENGINE = InnoDB;
