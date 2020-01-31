INSERT INTO notification_settings(user_id)
    SELECT user_profile.id FROM user_profile WHERE user_profile.id NOT IN (
        SELECT notification_settings_inner.user_id FROM notification_settings AS notification_settings_inner
    );


CREATE TABLE failed_login_attempt(
    user_id    BIGINT  NOT NULL,
    failed_at  BIGINT  NOT NULL
);
ALTER TABLE failed_login_attempt ADD CONSTRAINT failed_login_attempt__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);
CREATE INDEX failed_login_attempt__user_id_idx ON failed_login_attempt(user_id);
CREATE INDEX failed_login_attempt__failed_at_idx ON failed_login_attempt(failed_at);

