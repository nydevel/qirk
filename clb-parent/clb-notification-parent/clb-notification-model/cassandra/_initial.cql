CREATE KEYSPACE clb_notif WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1} AND DURABLE_WRITES = true;

USE clb_notif;


CREATE TABLE notif(
	subscriber_id       BIGINT,
	timestamp           BIGINT,
	notification_type   TEXT,
	json                TEXT,
	PRIMARY KEY (subscriber_id, timestamp)
);

CREATE TABLE notif_last_check(
	subscriber_id         BIGINT,
	last_check_timestamp  BIGINT,
	PRIMARY KEY (subscriber_id)
);
