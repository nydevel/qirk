ALTER TABLE user_profile ALTER COLUMN interface_language_id DROP NOT NULL;

UPDATE user_profile SET username = lower(split_part(username, '@', 1));
ALTER TABLE user_profile ADD COLUMN full_name VARCHAR(255) NOT NULL DEFAULT '';
UPDATE user_profile SET full_name = alias;
ALTER TABLE user_profile ALTER COLUMN alias DROP NOT NULL;
ALTER TABLE user_profile ALTER COLUMN first_name DROP NOT NULL;
ALTER TABLE user_profile ALTER COLUMN last_name DROP NOT NULL;

ALTER TABLE user_profile ADD COLUMN about2 VARCHAR(10000) NOT NULL DEFAULT '';
UPDATE user_profile SET about2 = about;
ALTER TABLE user_profile RENAME COLUMN about TO about1;
ALTER TABLE user_profile RENAME COLUMN about2 TO about;
ALTER TABLE user_profile DROP COLUMN about1;
ALTER TABLE user_profile ALTER COLUMN about DROP DEFAULT;


ALTER TABLE project ADD COLUMN description_md2 VARCHAR(10000) NOT NULL DEFAULT '';
UPDATE project SET description_md2 = description_md;
ALTER TABLE project RENAME COLUMN description_md TO description_md1;
ALTER TABLE project RENAME COLUMN description_md2 TO description_md;
ALTER TABLE project DROP COLUMN description_md1;
ALTER TABLE project ALTER COLUMN description_md DROP DEFAULT;

ALTER TABLE project ADD COLUMN description_html2 VARCHAR(20000) NOT NULL DEFAULT '';
UPDATE project SET description_html2 = description_html;
ALTER TABLE project RENAME COLUMN description_html TO description_html1;
ALTER TABLE project RENAME COLUMN description_html2 TO description_html;
ALTER TABLE project DROP COLUMN description_html1;
ALTER TABLE project ALTER COLUMN description_html DROP DEFAULT;


ALTER TABLE task ADD COLUMN description_md2 VARCHAR(10000) NOT NULL DEFAULT '';
UPDATE task SET description_md2 = description_md;
ALTER TABLE task RENAME COLUMN description_md TO description_md1;
ALTER TABLE task RENAME COLUMN description_md2 TO description_md;
ALTER TABLE task DROP COLUMN description_md1;
ALTER TABLE task ALTER COLUMN description_md DROP DEFAULT;

ALTER TABLE task ADD COLUMN description_html2 VARCHAR(20000) NOT NULL DEFAULT '';
UPDATE task SET description_html2 = description_html;
ALTER TABLE task RENAME COLUMN description_html TO description_html1;
ALTER TABLE task RENAME COLUMN description_html2 TO description_html;
ALTER TABLE task DROP COLUMN description_html1;
ALTER TABLE task ALTER COLUMN description_html DROP DEFAULT;


ALTER TABLE memo ADD COLUMN body2 VARCHAR(10000) NOT NULL DEFAULT '';
UPDATE memo SET body2 = body;
ALTER TABLE memo RENAME COLUMN body TO body1;
ALTER TABLE memo RENAME COLUMN body2 TO body;
ALTER TABLE memo DROP COLUMN body1;
ALTER TABLE memo ALTER COLUMN body DROP DEFAULT;


ALTER TABLE issue ADD COLUMN description2 VARCHAR(10000) NOT NULL DEFAULT '';
UPDATE issue SET description2 = description;
ALTER TABLE issue RENAME COLUMN description TO description1;
ALTER TABLE issue RENAME COLUMN description2 TO description;
ALTER TABLE issue DROP COLUMN description1;
ALTER TABLE issue ALTER COLUMN description DROP DEFAULT;


CREATE TABLE email_activation_token(
	id             bigserial     PRIMARY KEY,
	email_address  VARCHAR(256)  CONSTRAINT email_activation_token__email_uniq UNIQUE NOT NULL,
	password_hash  VARCHAR(128)  NOT NULL,
	token          VARCHAR(23)   NOT NULL,
	expires_at     BIGINT        NOT NULL
);

