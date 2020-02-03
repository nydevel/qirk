CREATE TABLE user_favorite(
	id                     bigserial      PRIMARY KEY,
	user_id                BIGINT         NOT NULL,
	project_id             BIGINT,
	previous_id            BIGINT         CONSTRAINT user_favorite__previous_id_uniq UNIQUE
);
ALTER TABLE user_favorite ADD CONSTRAINT user_favorite__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);
ALTER TABLE user_favorite ADD CONSTRAINT user_favorite__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE user_favorite ADD CONSTRAINT user_favorite__previous_id__user_favorite_fkey FOREIGN KEY (previous_id) REFERENCES user_favorite(id);

