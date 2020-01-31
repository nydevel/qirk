CREATE TABLE notif(
	subscriber_id       BIGINT       NOT NULL,
	timestamp           BIGINT       NOT NULL,
	notification_type   VARCHAR(63)  NOT NULL,
	json                TEXT         NOT NULL,
	PRIMARY KEY (subscriber_id, timestamp)
);
ALTER TABLE notif ADD CONSTRAINT notif__subscriber_id__timestamp_uniq UNIQUE (subscriber_id, timestamp);

CREATE TABLE notif_last_check(
	subscriber_id         BIGINT  PRIMARY KEY,
	last_check_timestamp  BIGINT  NOT NULL
);
