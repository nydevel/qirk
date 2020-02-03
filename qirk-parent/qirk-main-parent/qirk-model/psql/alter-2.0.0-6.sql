ALTER TABLE user_organization DROP COLUMN role;


ALTER TABLE project_invite ADD COLUMN sender_user_id BIGINT;
ALTER TABLE project_invite ADD CONSTRAINT project_invite__sender_user_id__user_profile_fkey FOREIGN KEY (sender_user_id) REFERENCES user_profile(id);
CREATE INDEX project_invite__sender_user_id_idx ON project_invite(sender_user_id);
UPDATE project_invite SET sender_user_id = (
	SELECT user_profile.id 
	FROM user_profile 
	JOIN user_organization ON user_profile.id = user_organization.user_id 
	JOIN project ON user_organization.organization_id = project.organization_id 
	WHERE project.id = project_invite.project_id 
	ORDER BY user_organization.id ASC
	LIMIT 1
);
ALTER TABLE project_invite ALTER COLUMN sender_user_id SET NOT NULL;


CREATE TABLE granted_permissions_project_invite(
	id                  bigserial                 PRIMARY KEY,
	sender_user_id      BIGINT                    NOT NULL,
	user_id             BIGINT,
	project_id          BIGINT                    NOT NULL,
	text                TEXT                      NOT NULL,
	created_at          TIMESTAMP WITH TIME ZONE  NOT NULL,
	updated_at          TIMESTAMP WITH TIME ZONE  NOT NULL,
	status_id           BIGINT                    NOT NULL,
	reported            BOOL                      NOT NULL DEFAULT false,
	write_allowed       BOOL                      NOT NULL DEFAULT false,
	manager             BOOL                      NOT NULL DEFAULT false
);
ALTER TABLE granted_perms_project_invite ADD CONSTRAINT granted_perms_project_invite__sender_user_id__user_profile_fkey FOREIGN KEY (sender_user_id) REFERENCES user_profile(id);
CREATE INDEX granted_perms_project_invite__sender_user_id_idx ON granted_permissions_project_invite(sender_user_id);
ALTER TABLE granted_perms_project_invite ADD CONSTRAINT granted_perms_project_invite__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);
CREATE INDEX granted_perms_project_invite__user_id_idx ON granted_permissions_project_invite(user_id);
ALTER TABLE granted_perms_project_invite ADD CONSTRAINT granted_perms_project_invite__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);
CREATE INDEX granted_perms_project_invite__project_id_idx ON granted_permissions_project_invite(project_id);
ALTER TABLE granted_perms_project_invite ADD CONSTRAINT granted_perms_project_invite__status_id__invite_status_fkey FOREIGN KEY (status_id) REFERENCES invite_status(id);
CREATE INDEX granted_perms_project_invite__status_id_idx ON granted_permissions_project_invite(status_id);


CREATE TABLE project_invite_token(
	id                  bigserial                 PRIMARY KEY,
	invite_id           BIGINT                    CONSTRAINT project_invite_token__invite_id_uniq UNIQUE NOT NULL,
	token               VARCHAR(23)               CONSTRAINT project_invite_token__token_uniq UNIQUE NOT NULL,
	email_address       VARCHAR(256)              NOT NULL,
	created_at          TIMESTAMP WITH TIME ZONE  NOT NULL
);
ALTER TABLE project_invite_token ADD CONSTRAINT project_invite_token__invite_id__granted_perms_proj_invite_fkey FOREIGN KEY (invite_id) REFERENCES granted_permissions_project_invite(id);

