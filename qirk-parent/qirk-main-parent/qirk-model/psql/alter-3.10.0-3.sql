ALTER TABLE organization ADD CONSTRAINT organization__owner_user_id__user_fkey FOREIGN KEY (owner_user_id) REFERENCES user_profile(id);

