ALTER TABLE user_profile ADD COLUMN license_accepted BOOL NOT NULL DEFAULT true;


CREATE TABLE notification_settings(
	user_id         BIGINT  CONSTRAINT notification_settings__user_id_uniq UNIQUE NOT NULL,
	task_created    BOOL    NOT NULL DEFAULT true,
	task_updated    BOOL    NOT NULL DEFAULT true,
	task_commented  BOOL    NOT NULL DEFAULT true
);
ALTER TABLE notification_settings ADD CONSTRAINT notification_settings__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);


ALTER TABLE user_profile DROP COLUMN interface_language_id;
ALTER TABLE user_profile DROP COLUMN alias;
ALTER TABLE user_profile DROP COLUMN first_name;
ALTER TABLE user_profile DROP COLUMN last_name;
ALTER TABLE user_profile ALTER COLUMN full_name DROP DEFAULT;


CREATE TABLE road(
	id               bigserial     PRIMARY KEY,
	record_version   BIGINT        NOT NULL,
	organization_id  BIGINT        NOT NULL,
	project_id       BIGINT        NOT NULL,
	name             VARCHAR(127)  NOT NULL,
	previous_id      BIGINT
);
ALTER TABLE road ADD CONSTRAINT road__organization_id__organization_fkey FOREIGN KEY (organization_id) REFERENCES organization(id);
ALTER TABLE road ADD CONSTRAINT road__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
CREATE INDEX road__project_id_idx ON road(project_id);
ALTER TABLE road ADD CONSTRAINT road__previous_id__road_fkey FOREIGN KEY (previous_id) REFERENCES road(id);


CREATE TABLE task_card(
	id               bigserial                 PRIMARY KEY,
	record_version   BIGINT                    NOT NULL,
	organization_id  BIGINT                    NOT NULL,
	project_id       BIGINT                    NOT NULL,
	road_id          BIGINT                    NOT NULL,
	name             VARCHAR(127)              NOT NULL,
	status           VARCHAR(9)                NOT NULL,
	active           BOOL                      NOT NULL,
	previous_id      BIGINT,
	created_at       TIMESTAMP WITH TIME ZONE  NOT NULL,
	archieved_at     TIMESTAMP WITH TIME ZONE
);
ALTER TABLE task_card ADD CONSTRAINT task_card__organization_id__organization_fkey FOREIGN KEY (organization_id) REFERENCES organization(id);
ALTER TABLE task_card ADD CONSTRAINT task_card__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
CREATE INDEX task_card__project_id_idx ON task_card(project_id);
ALTER TABLE task_card ADD CONSTRAINT task_card__road_id__road_fkey FOREIGN KEY (road_id) REFERENCES road(id);
ALTER TABLE task_card ADD CONSTRAINT task_card__previous_id__task_card_fkey FOREIGN KEY (previous_id) REFERENCES task_card(id);
CREATE INDEX task_card__archieved_at_idx ON task_card(archieved_at);


ALTER TABLE task ADD COLUMN hidden BOOL NOT NULL DEFAULT false;
ALTER TABLE task ADD COLUMN task_card_id BIGINT;
ALTER TABLE task ADD CONSTRAINT task__task_card_id__task_card_fkey FOREIGN KEY (task_card_id) REFERENCES task_card(id);

