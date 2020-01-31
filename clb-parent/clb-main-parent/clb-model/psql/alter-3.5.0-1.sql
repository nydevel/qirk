DELETE FROM project_invite_token WHERE password_hash IS NULL;
ALTER TABLE project_invite_token ALTER COLUMN password_hash SET NOT NULL;

DELETE FROM granted_permissions_project_invite WHERE user_id IS NULL AND id NOT IN (SELECT invite_id FROM project_invite_token);

