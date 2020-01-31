CREATE TABLE languages(
	id bigserial PRIMARY KEY,
	name_code VARCHAR(50) CONSTRAINT languages_name_code_uniq UNIQUE NOT NULL
);
INSERT INTO languages(name_code) VALUES('EN_US');
INSERT INTO languages(name_code) VALUES('RU_RU');


CREATE TABLE user_profile(
	id bigserial PRIMARY KEY,
	username VARCHAR(256) CONSTRAINT user_profile__username_uniq UNIQUE NOT NULL,
	email_address VARCHAR(256) CONSTRAINT user_profile__email_uniq UNIQUE NOT NULL,
    enabled BOOL NOT NULL DEFAULT false,
	password_hash VARCHAR(128) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
	alias VARCHAR(255) NOT NULL,
    first_name VARCHAR(127),
    last_name VARCHAR(127),
    dont_recommend BOOL NOT NULL DEFAULT false,
    interface_language_id BIGINT NOT NULL
);
ALTER TABLE user_profile ADD FOREIGN KEY (interface_language_id) REFERENCES languages(id);


CREATE TABLE user_language(
    user_id BIGINT REFERENCES user_profile(id) ON UPDATE CASCADE,
    language_id BIGINT REFERENCES languages(id) ON UPDATE CASCADE,
    CONSTRAINT user_language_pkey PRIMARY KEY (user_id, language_id)
);


CREATE TABLE activation_token(
	id bigserial PRIMARY KEY,
	token VARCHAR(23) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
ALTER TABLE activation_token ADD FOREIGN KEY (user_id) REFERENCES user_profile(id);


CREATE TABLE login_statistics(
	id bigserial PRIMARY KEY,
	internet_address VARCHAR(39) NOT NULL,
    user_id BIGINT NOT NULL,
    login_at TIMESTAMP WITH TIME ZONE NOT NULL
);
ALTER TABLE login_statistics ADD FOREIGN KEY (user_id) REFERENCES user_profile(id);


CREATE TABLE tag(
	id bigserial PRIMARY KEY,
	name VARCHAR(127) CONSTRAINT tag__name_uniq UNIQUE NOT NULL
);


CREATE TABLE user_tag(
    user_id BIGINT REFERENCES user_profile(id) ON UPDATE CASCADE,
    tag_id BIGINT REFERENCES tag(id) ON UPDATE CASCADE,
    CONSTRAINT user_tag_pkey PRIMARY KEY (user_id, tag_id)
);


CREATE TABLE user_social_auth(
    id bigserial PRIMARY KEY,
    uid VARCHAR(256) NOT NULL,
    provider VARCHAR(127) NOT NULL,
    user_id BIGINT REFERENCES user_profile(id) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
ALTER TABLE user_social_auth ADD FOREIGN KEY (user_id) REFERENCES user_profile(id);
ALTER TABLE user_social_auth ADD CONSTRAINT user_social_auth__uid_provider_uniq UNIQUE (uid, provider);


CREATE TABLE dropbox_settings(
	id                     bigserial      PRIMARY KEY,
	purge_on_delete        BOOL           NOT NULL DEFAULT false,
	token                  VARCHAR(127)   NOT NULL
);


CREATE TABLE organization(
	id                      bigserial       PRIMARY KEY,
	name                    VARCHAR(127)    NOT NULL,
    ui_id                   VARCHAR(23)     CONSTRAINT organization__ui_id_uniq UNIQUE NOT NULL,
    private                 BOOL            NOT NULL DEFAULT false,
	predefined_for_user_id  BIGINT          NULL,
	dropbox_settings_id     BIGINT,
	record_version          BIGINT          NOT NULL
);
ALTER TABLE organization ADD CONSTRAINT organization__predefined_for_user_id__user_profile_fkey FOREIGN KEY (predefined_for_user_id) REFERENCES user_profile(id);
ALTER TABLE organization ADD CONSTRAINT organization__dropbox_settings_id__dropbox_settings_fkey FOREIGN KEY (dropbox_settings_id) REFERENCES dropbox_settings(id);


CREATE TABLE user_organization(
	id                      bigserial       PRIMARY KEY,
	user_id                 BIGINT          NOT NULL,
    organization_id         BIGINT          NOT NULL,
    role                    VARCHAR(255)    NULL,
    enabled                 BOOL            NOT NULL        DEFAULT false,
    manager                 BOOL            NOT NULL        DEFAULT false,
    record_version          BIGINT          NOT NULL
);
ALTER TABLE user_organization ADD FOREIGN KEY (user_id) REFERENCES user_profile(id);
ALTER TABLE user_organization ADD CONSTRAINT user_organization__organization_fk FOREIGN KEY (organization_id) REFERENCES organization(id);
ALTER TABLE user_organization ADD CONSTRAINT user_organization__user_id_organization_id_uniq UNIQUE (user_id, organization_id);


CREATE TABLE organization_language(
    organization_id BIGINT REFERENCES organization(id) ON UPDATE CASCADE,
    language_id BIGINT REFERENCES languages(id) ON UPDATE CASCADE,
    CONSTRAINT organization_language_pkey PRIMARY KEY (organization_id, language_id)
);


CREATE TABLE project(
	id bigserial PRIMARY KEY,
	name VARCHAR(127) NOT NULL,
    ui_id VARCHAR(23) CONSTRAINT project__ui_id_uniq UNIQUE NOT NULL,
    organization_id BIGINT NOT NULL,
    private BOOL NOT NULL DEFAULT false,
    description TEXT,
    dropbox_settings_id BIGINT,
	record_version BIGINT NOT NULL
);
ALTER TABLE project ADD CONSTRAINT project__organization_fk FOREIGN KEY (organization_id) REFERENCES organization(id);
ALTER TABLE project ADD CONSTRAINT project__dropbox_settings_id__dropbox_settings_fkey FOREIGN KEY (dropbox_settings_id) REFERENCES dropbox_settings(id);


CREATE TABLE project_language(
    project_id BIGINT REFERENCES project(id) ON UPDATE CASCADE,
    language_id BIGINT REFERENCES languages(id) ON UPDATE CASCADE,
    CONSTRAINT project_language_pkey PRIMARY KEY (project_id, language_id)
);


CREATE TABLE project_tag(
    project_id BIGINT REFERENCES project(id) ON UPDATE CASCADE,
    tag_id BIGINT REFERENCES tag(id) ON UPDATE CASCADE,
    CONSTRAINT project_tag_pkey PRIMARY KEY (project_id, tag_id)
);


CREATE TABLE user_organization_group(
	id               bigserial      PRIMARY KEY,
	name             VARCHAR(127)   NOT NULL,
	organization_id  BIGINT         NOT NULL,
	record_version   BIGINT         NOT NULL
);
ALTER TABLE user_organization_group ADD CONSTRAINT user_organization_group__organization_fk FOREIGN KEY (organization_id) REFERENCES organization(id);


CREATE TABLE user_group_project_permissions(
	id                            bigserial  PRIMARY KEY,
	user_organization_group_id    BIGINT     NOT NULL,
	project_id                    BIGINT     NOT NULL,
	read_project_allowed          BOOL       NOT NULL DEFAULT false,
	update_project_allowed        BOOL       NOT NULL DEFAULT false,
	create_task_allowed           BOOL       NOT NULL DEFAULT false,
	update_other_tasks_allowed    BOOL       NOT NULL DEFAULT false
);
ALTER TABLE user_group_project_permissions ADD CONSTRAINT user_group_project_permissions__user_organization_group_fk FOREIGN KEY (user_organization_group_id) REFERENCES user_organization_group(id);
ALTER TABLE user_group_project_permissions ADD CONSTRAINT user_group_project_permissions__project_fk FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE user_group_project_permissions ADD CONSTRAINT user_group_proj_permissions__user_org_group_id_proj_id_uniq UNIQUE (user_organization_group_id, project_id);


CREATE TABLE user_organization_group__user_organization(
	user_organization_group_id    BIGINT     NOT NULL,
	user_organization_id          BIGINT     NOT NULL
);
ALTER TABLE user_organization_group__user_organization ADD CONSTRAINT user_org_group__user_org___user_org_group_fk FOREIGN KEY (user_organization_group_id) REFERENCES user_organization_group(id);
ALTER TABLE user_organization_group__user_organization ADD CONSTRAINT user_org_group__user_org___user_org_fk FOREIGN KEY (user_organization_id) REFERENCES user_organization(id);


CREATE TABLE version(
	id                  bigserial      PRIMARY KEY,
	name                VARCHAR(23)    NOT NULL,
	project_id          BIGINT         NOT NULL,
	previous_version_id BIGINT,
	next_task_number    BIGINT         NOT NULL
);
ALTER TABLE version ADD CONSTRAINT version__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE version ADD CONSTRAINT version__previous_version_id__version_fkey FOREIGN KEY (previous_version_id) REFERENCES version(id);
ALTER TABLE version ADD CONSTRAINT version__project_id_name_uniq UNIQUE (project_id, name);


CREATE TABLE task_type(
	id                  bigserial      PRIMARY KEY,
	name_code           VARCHAR(11)    CONSTRAINT task_type__name_code_uniq UNIQUE NOT NULL
);
INSERT INTO task_type(name_code) VALUES('TASK');
INSERT INTO task_type(name_code) VALUES('BUG');
INSERT INTO task_type(name_code) VALUES('IMPROVEMENT');
INSERT INTO task_type(name_code) VALUES('NEW_FEATURE');


CREATE TABLE task_priority(
	id                  bigserial      PRIMARY KEY,
	name_code           VARCHAR(8)     CONSTRAINT task_priority__name_code_uniq UNIQUE NOT NULL,
	importance          INTEGER        CONSTRAINT task_priority__importance_uniq UNIQUE NOT NULL
);
INSERT INTO task_priority(name_code, importance) VALUES('TRIVIAL', 10);
INSERT INTO task_priority(name_code, importance) VALUES('MINOR', 20);
INSERT INTO task_priority(name_code, importance) VALUES('MAJOR', 30);
INSERT INTO task_priority(name_code, importance) VALUES('CRITICAL', 40);
INSERT INTO task_priority(name_code, importance) VALUES('BLOCKING', 50);


CREATE TABLE task_status(
	id                  bigserial      PRIMARY KEY,
	name_code           VARCHAR(14)    CONSTRAINT task_status__name_code_uniq UNIQUE NOT NULL
);
INSERT INTO task_status(name_code) VALUES('OPEN');
INSERT INTO task_status(name_code) VALUES('REJECTED');
INSERT INTO task_status(name_code) VALUES('IN_DEVELOPMENT');
INSERT INTO task_status(name_code) VALUES('WAITING_FOR_QA');
INSERT INTO task_status(name_code) VALUES('IN_QA_REVIEW');
INSERT INTO task_status(name_code) VALUES('CLOSED');


CREATE TABLE task(
	id                             bigserial                 PRIMARY KEY,
	record_version                 BIGINT                    NOT NULL,
	number                         BIGINT                    NOT NULL,
	description                    TEXT                      NOT NULL,
	summary                        VARCHAR(80)               NOT NULL,
	version_id                     BIGINT                    NOT NULL,
	parent_task_id                 BIGINT,
	reporter_user_organization_id  BIGINT                    NOT NULL,
	assignee_user_organization_id  BIGINT,
	created_at                     TIMESTAMP WITH TIME ZONE  NOT NULL,
	updated_at                     TIMESTAMP WITH TIME ZONE  NOT NULL,
	type_id                        BIGINT                    NOT NULL,
	priority_id                    BIGINT                    NOT NULL,
	status_id                      BIGINT                    NOT NULL
);
ALTER TABLE task ADD CONSTRAINT task__version_id__version_fkey FOREIGN KEY (version_id) REFERENCES version(id);
ALTER TABLE task ADD CONSTRAINT task__parent_task_id__task_fkey FOREIGN KEY (parent_task_id) REFERENCES task(id);
ALTER TABLE task ADD CONSTRAINT task__reporter_user_organization_id__user_organization_fkey FOREIGN KEY (reporter_user_organization_id) REFERENCES user_organization(id);
ALTER TABLE task ADD CONSTRAINT task__assignee_user_organization_id__user_organization_fkey FOREIGN KEY (assignee_user_organization_id) REFERENCES user_organization(id);
ALTER TABLE task ADD CONSTRAINT task__type_id__task_type_fkey FOREIGN KEY (type_id) REFERENCES task_type(id);
ALTER TABLE task ADD CONSTRAINT task__priority_id__task_priority_fkey FOREIGN KEY (priority_id) REFERENCES task_priority(id);
ALTER TABLE task ADD CONSTRAINT task__status_id__task_status_fkey FOREIGN KEY (status_id) REFERENCES task_status(id);
ALTER TABLE task ADD CONSTRAINT task__version_id_number_uniq UNIQUE (version_id, number);


CREATE TABLE attachment(
	id                  bigserial      PRIMARY KEY,
	filename            VARCHAR(511)   NOT NULL,
	path                VARCHAR(511)   NOT NULL,
	task_id             BIGINT         NOT NULL,
	dropbox_settings_id BIGINT         NOT NULL,
	deleted             BOOL           NOT NULL DEFAULT false
);
ALTER TABLE attachment ADD CONSTRAINT attachment__task_id__task_fkey FOREIGN KEY (task_id) REFERENCES task(id);
ALTER TABLE attachment ADD CONSTRAINT attachment__dropbox_settings_id__dropbox_settings_fkey FOREIGN KEY (dropbox_settings_id) REFERENCES dropbox_settings(id);

