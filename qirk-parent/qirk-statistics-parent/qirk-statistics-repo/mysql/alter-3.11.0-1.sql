ALTER TABLE feedback MODIFY sender_user_id BIGINT;
ALTER TABLE feedback ADD COLUMN sender_user_email VARCHAR(256);
