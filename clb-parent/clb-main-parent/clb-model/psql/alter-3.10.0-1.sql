ALTER TABLE attachment ALTER COLUMN dropbox_settings_id DROP NOT NULL;
ALTER TABLE temporary_attachment ALTER COLUMN dropbox_settings_id DROP NOT NULL;

