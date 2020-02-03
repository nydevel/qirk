ALTER TABLE project ADD COLUMN documentation_md TEXT;
ALTER TABLE project ADD COLUMN documentation_html TEXT;


ALTER TABLE project ADD    COLUMN description_html TEXT;
ALTER TABLE project RENAME COLUMN description TO description_md;


ALTER TABLE task ADD    COLUMN description_html TEXT NOT NULL DEFAULT ' ';
ALTER TABLE task ALTER  COLUMN description_html DROP DEFAULT;
ALTER TABLE task RENAME COLUMN description TO description_md;

