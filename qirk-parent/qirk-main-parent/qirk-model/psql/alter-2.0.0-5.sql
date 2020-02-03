ALTER TABLE user_profile ALTER COLUMN dont_recommend SET DEFAULT true;


UPDATE project SET description_md = '' WHERE description_md IS NULL;
ALTER TABLE project ALTER COLUMN description_md SET NOT NULL;

UPDATE project SET description_html = '' WHERE description_html IS NULL;
ALTER TABLE project ALTER COLUMN description_html SET NOT NULL;

UPDATE project SET documentation_md = '' WHERE documentation_md IS NULL;
ALTER TABLE project ALTER COLUMN documentation_md SET NOT NULL;

UPDATE project SET documentation_html = '' WHERE documentation_html IS NULL;
ALTER TABLE project ALTER COLUMN documentation_html SET NOT NULL;


ALTER TABLE task DROP CONSTRAINT task__project_id_number_uniq;
ALTER TABLE task ADD CONSTRAINT task__project_id_number_uniq UNIQUE (number, project_id);


DROP INDEX project_invite__updated_at__idx;
DROP INDEX project_application__updated_at__idx;


ALTER TABLE activation_token ADD CONSTRAINT activation_token__user_id_uniq UNIQUE(user_id);
ALTER TABLE activation_token ADD CONSTRAINT activation_token__token_uniq UNIQUE(token);


ALTER TABLE organization ADD CONSTRAINT organization__predefined_for_user_id_uniq UNIQUE(predefined_for_user_id);
CREATE INDEX organization__dropbox_settings_id_idx ON organization(dropbox_settings_id);


CREATE INDEX user_organization__organization_id_idx ON user_organization(organization_id);


CREATE INDEX project__organization_id_idx ON project(organization_id);
CREATE INDEX project__dropbox_settings_id_idx ON project(dropbox_settings_id);


CREATE INDEX task__parent_task_id_idx ON task(parent_task_id);
CREATE INDEX task__reporter_user_organization_id_idx ON task(reporter_user_organization_id);
CREATE INDEX task__assignee_user_organization_id_idx ON task(assignee_user_organization_id);
CREATE INDEX task__type_id_idx ON task(type_id);
CREATE INDEX task__priority_id_idx ON task(priority_id);
CREATE INDEX task__status_id_idx ON task(status_id);


CREATE INDEX attachment__task_id_idx ON attachment(task_id);
CREATE INDEX attachment__dropbox_settings_id_idx ON attachment(dropbox_settings_id);


CREATE INDEX user_favorite__user_id_idx ON user_favorite(user_id);
CREATE INDEX user_favorite__project_id_idx ON user_favorite(project_id);


CREATE INDEX memo__project_id_idx ON memo(project_id);
CREATE INDEX memo__author_user_organization_id_idx ON memo(author_user_organization_id);


CREATE INDEX external_repo__type_id_idx ON external_repo(type_id);


CREATE INDEX issue__project_id_idx ON issue(project_id);
CREATE INDEX issue__user_id_idx ON issue(user_id);


CREATE INDEX project_invite__user_id_idx ON project_invite(user_id);
CREATE INDEX project_invite__project_id_idx ON project_invite(project_id);
CREATE INDEX project_invite__status_id_idx ON project_invite(status_id);


CREATE INDEX project_member__user_id_idx ON project_member(user_id);
CREATE INDEX project_member__user_organization_id_idx ON project_member(user_organization_id);
CREATE INDEX project_member__project_id_idx ON project_member(project_id);


CREATE INDEX project_application__user_id_idx ON project_application(user_id);
CREATE INDEX project_application__project_id_idx ON project_application(project_id);
CREATE INDEX project_application__status_id_idx ON project_application(status_id);

