CREATE TABLE memo(
	id                           bigserial                PRIMARY KEY,
	body                         TEXT                     NOT NULL,
	project_id                   BIGINT                   NOT NULL,
	author_user_organization_id  BIGINT                   NOT NULL,
	created_at                   TIMESTAMP WITH TIME ZONE NOT NULL
);
ALTER TABLE memo ADD CONSTRAINT memo__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE memo ADD CONSTRAINT memo__author_user_organization_id__user_organization_fkey FOREIGN KEY (author_user_organization_id) REFERENCES user_organization(id);


CREATE TABLE external_repo_type(
	id                  bigserial      PRIMARY KEY,
	name_code           VARCHAR(23)    CONSTRAINT external_repo_type__name_code_uniq UNIQUE NOT NULL
);
INSERT INTO external_repo_type(name_code) VALUES('GitHub');
INSERT INTO external_repo_type(name_code) VALUES('BitBucket');


CREATE TABLE external_repo(
	id                  bigserial      PRIMARY KEY,
	type_id             BIGINT         NOT NULL,
	token               VARCHAR(127)   NOT NULL,
	repo_owner_name     VARCHAR(127)   NOT NULL,
	repo_name           VARCHAR(127)   NOT NULL,
	repo_url            VARCHAR(1023)  NOT NULL
);
ALTER TABLE external_repo ADD CONSTRAINT external_repo__type_id__external_repo_type_fkey FOREIGN KEY (type_id) REFERENCES external_repo_type(id);


ALTER TABLE project ADD COLUMN external_repo_id BIGINT CONSTRAINT project__external_repo_id_uniq UNIQUE;
ALTER TABLE project ADD CONSTRAINT project__external_repo_id__external_repo_fkey FOREIGN KEY (external_repo_id) REFERENCES external_repo(id);


CREATE TABLE issue(
	id                  bigserial                PRIMARY KEY,
	summary             VARCHAR(255)             NOT NULL,
	description         TEXT                     NOT NULL,
	project_id          BIGINT                   NOT NULL,
	user_id             BIGINT                   NOT NULL,
	source_external_id  BIGINT,
	source_url          VARCHAR(1023)            NOT NULL,
	created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
	task_id             BIGINT
);
ALTER TABLE issue ADD CONSTRAINT issue__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE issue ADD CONSTRAINT issue__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);

