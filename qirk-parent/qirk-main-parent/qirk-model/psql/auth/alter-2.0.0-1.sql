CREATE TABLE remember_me_token(
	id          bigserial                 PRIMARY KEY,
	token       VARCHAR(72)               CONSTRAINT remember_me_token__token_uniq UNIQUE NOT NULL,
	user_id     BIGINT                    NOT NULL,
	created_at  TIMESTAMP WITH TIME ZONE  NOT NULL,
	updated_at  TIMESTAMP WITH TIME ZONE  NOT NULL
);

