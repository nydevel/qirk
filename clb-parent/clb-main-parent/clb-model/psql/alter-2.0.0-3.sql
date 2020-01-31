UPDATE user_profile SET first_name = '' WHERE first_name IS NULL;
ALTER TABLE user_profile ALTER COLUMN first_name SET NOT NULL;

UPDATE user_profile SET last_name = '' WHERE last_name IS NULL;
ALTER TABLE user_profile ALTER COLUMN last_name SET NOT NULL;

ALTER TABLE user_profile ADD COLUMN about TEXT NOT NULL DEFAULT '';
ALTER TABLE user_profile ALTER COLUMN about DROP DEFAULT;


ALTER TABLE user_favorite DROP CONSTRAINT user_favorite__previous_id_uniq;


CREATE TABLE invite_status(
    id                  bigserial      PRIMARY KEY,
	name_code           VARCHAR(9)     CONSTRAINT invite_status__name_code_uniq UNIQUE NOT NULL
);
INSERT INTO invite_status(name_code) VALUES('PENDING');
INSERT INTO invite_status(name_code) VALUES('REJECTED');
INSERT INTO invite_status(name_code) VALUES('ACCEPTED');


CREATE TABLE project_invite(
	id                  bigserial                 PRIMARY KEY,
	user_id             BIGINT                    NOT NULL,
	project_id          BIGINT                    NOT NULL,
	text                TEXT                      NOT NULL,
	created_at          TIMESTAMP WITH TIME ZONE  NOT NULL,
	updated_at          TIMESTAMP WITH TIME ZONE  NOT NULL,
	status_id           BIGINT                    NOT NULL,
	reported            BOOL                      NOT NULL DEFAULT false
);
ALTER TABLE project_invite ADD CONSTRAINT project_invite__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);
ALTER TABLE project_invite ADD CONSTRAINT project_invite__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE project_invite ADD CONSTRAINT project_invite__status_id__invite_status_fkey FOREIGN KEY (status_id) REFERENCES invite_status(id);


ALTER TABLE user_organization ALTER COLUMN fired SET NOT NULL;


CREATE TABLE project_member(
	id                    bigserial                 PRIMARY KEY,
	user_id               BIGINT                    NOT NULL,
	user_organization_id  BIGINT                    NOT NULL,
	project_id            BIGINT                    NOT NULL,
	write_allowed         BOOL                      NOT NULL DEFAULT false,
	manager               BOOL                      NOT NULL DEFAULT false,
	hired_at              TIMESTAMP WITH TIME ZONE  NOT NULL,
	fired_at              TIMESTAMP WITH TIME ZONE,
	fired                 BOOL                      NOT NULL DEFAULT false
);
ALTER TABLE project_member ADD CONSTRAINT project_member__user_organization_id__user_organization_fkey FOREIGN KEY (user_organization_id) REFERENCES user_organization(id);
ALTER TABLE project_member ADD CONSTRAINT project_member__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);
ALTER TABLE project_member ADD CONSTRAINT project_member__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


ALTER TABLE task DROP CONSTRAINT task__version_id__version_fkey;
DELETE FROM version;


ALTER TABLE user_organization_group__user_organization DROP CONSTRAINT user_org_group__user_org___user_org_fk;
ALTER TABLE user_organization_group__user_organization DROP CONSTRAINT user_org_group__user_org___user_org_group_fk;

ALTER TABLE user_group_project_permissions DROP CONSTRAINT user_group_project_permissions__project_fk;
ALTER TABLE user_group_project_permissions DROP CONSTRAINT user_group_project_permissions__user_organization_group_fk;

ALTER TABLE user_organization_group DROP CONSTRAINT user_organization_group__organization_fk;

DELETE FROM user_organization_group__user_organization;
DELETE FROM user_group_project_permissions;
DELETE FROM user_organization_group;


CREATE TABLE application_status(
    id                  bigserial      PRIMARY KEY,
	name_code           VARCHAR(9)     CONSTRAINT application_status__name_code_uniq UNIQUE NOT NULL
);
INSERT INTO application_status(name_code) VALUES('PENDING');
INSERT INTO application_status(name_code) VALUES('REJECTED');


CREATE TABLE project_application(
	id                  bigserial                 PRIMARY KEY,
	user_id             BIGINT                    NOT NULL,
	project_id          BIGINT                    NOT NULL,
	text                TEXT                      NOT NULL,
	created_at          TIMESTAMP WITH TIME ZONE  NOT NULL,
	updated_at          TIMESTAMP WITH TIME ZONE  NOT NULL,
	status_id           BIGINT                    NOT NULL,
	reported            BOOL                      NOT NULL DEFAULT false
);
ALTER TABLE project_application ADD CONSTRAINT project_application__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);
ALTER TABLE project_application ADD CONSTRAINT project_application__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE project_application ADD CONSTRAINT project_application__status_id__application_status_fkey FOREIGN KEY (status_id) REFERENCES application_status(id);

