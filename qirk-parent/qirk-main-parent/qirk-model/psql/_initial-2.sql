
--
-- Name: password_activation_token; Type: TABLE; Schema: public
--

CREATE TABLE password_activation_token (
    id bigint NOT NULL,
    token character varying(23) NOT NULL,
    user_id bigint NOT NULL,
    created_at timestamp with time zone NOT NULL
);




--
-- Name: activation_token_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE activation_token_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: activation_token_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE activation_token_id_seq OWNED BY password_activation_token.id;


--
-- Name: application_status; Type: TABLE; Schema: public
--

CREATE TABLE application_status (
    id bigint NOT NULL,
    name_code character varying(9) NOT NULL
);




--
-- Name: application_status_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE application_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: application_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE application_status_id_seq OWNED BY application_status.id;


--
-- Name: attachment; Type: TABLE; Schema: public
--

CREATE TABLE attachment (
    id bigint NOT NULL,
    filename character varying(511) NOT NULL,
    external_path character varying(511) NOT NULL,
    task_id bigint NOT NULL,
    deleted boolean DEFAULT false NOT NULL
);




--
-- Name: attachment_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE attachment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: attachment_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE attachment_id_seq OWNED BY attachment.id;


--
-- Name: email_activation_token; Type: TABLE; Schema: public
--

CREATE TABLE email_activation_token (
    id bigint NOT NULL,
    email_address character varying(256) NOT NULL,
    password_hash character varying(128) NOT NULL,
    token character varying(23) NOT NULL,
    expires_at bigint NOT NULL
);




--
-- Name: email_activation_token_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE email_activation_token_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: email_activation_token_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE email_activation_token_id_seq OWNED BY email_activation_token.id;


--
-- Name: failed_login_attempt; Type: TABLE; Schema: public
--

CREATE TABLE failed_login_attempt (
    user_id bigint NOT NULL,
    failed_at bigint NOT NULL
);




--
-- Name: granted_permissions_project_invite; Type: TABLE; Schema: public
--

CREATE TABLE granted_permissions_project_invite (
    id bigint NOT NULL,
    sender_user_id bigint NOT NULL,
    user_id bigint,
    project_id bigint NOT NULL,
    text text NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    status_id bigint NOT NULL,
    reported boolean DEFAULT false NOT NULL,
    write_allowed boolean DEFAULT false NOT NULL,
    manager boolean DEFAULT false NOT NULL
);




--
-- Name: granted_permissions_project_invite_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE granted_permissions_project_invite_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: granted_permissions_project_invite_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE granted_permissions_project_invite_id_seq OWNED BY granted_permissions_project_invite.id;


--
-- Name: imported_jira_project; Type: TABLE; Schema: public
--

CREATE TABLE imported_jira_project (
    project_id bigint NOT NULL,
    upload_timestamp bigint NOT NULL,
    jira_project_id bigint NOT NULL,
    jira_project_key character varying(10) NOT NULL,
    updated_at timestamp with time zone,
    jira_project_name character varying(80)
);




--
-- Name: imported_jira_task; Type: TABLE; Schema: public
--

CREATE TABLE imported_jira_task (
    project_id bigint NOT NULL,
    task_id bigint NOT NULL,
    jira_task_id bigint NOT NULL
);




--
-- Name: invite_status; Type: TABLE; Schema: public
--

CREATE TABLE invite_status (
    id bigint NOT NULL,
    name_code character varying(9) NOT NULL
);




--
-- Name: invite_status_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE invite_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: invite_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE invite_status_id_seq OWNED BY invite_status.id;


--
-- Name: issue; Type: TABLE; Schema: public
--

CREATE TABLE issue (
    id bigint NOT NULL,
    summary character varying(255) NOT NULL,
    project_id bigint NOT NULL,
    user_id bigint NOT NULL,
    source_external_id bigint,
    source_url character varying(1023),
    created_at timestamp with time zone NOT NULL,
    task_id bigint,
    description character varying(10000) NOT NULL
);




--
-- Name: issue_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE issue_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: issue_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE issue_id_seq OWNED BY issue.id;


--
-- Name: jira_upload; Type: TABLE; Schema: public
--

CREATE TABLE jira_upload (
    organization_id bigint NOT NULL,
    upload_timestamp bigint NOT NULL,
    archive_filename character varying(511) NOT NULL
);


--
-- Name: login_statistics; Type: TABLE; Schema: public
--

CREATE TABLE login_statistics (
    id bigint NOT NULL,
    internet_address character varying(39) NOT NULL,
    user_id bigint NOT NULL,
    login_at timestamp with time zone NOT NULL
);




--
-- Name: login_statistics_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE login_statistics_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: login_statistics_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE login_statistics_id_seq OWNED BY login_statistics.id;


--
-- Name: memo; Type: TABLE; Schema: public
--

CREATE TABLE memo (
    id bigint NOT NULL,
    project_id bigint NOT NULL,
    author_project_member_id bigint NOT NULL,
    created_at timestamp with time zone NOT NULL,
    body character varying(10000) NOT NULL
);




--
-- Name: memo_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE memo_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: memo_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE memo_id_seq OWNED BY memo.id;


--
-- Name: notification_settings; Type: TABLE; Schema: public
--

CREATE TABLE notification_settings (
    user_id bigint NOT NULL,
    task_created boolean DEFAULT true NOT NULL,
    task_updated boolean DEFAULT true NOT NULL,
    task_commented boolean DEFAULT true NOT NULL
);




--
-- Name: project; Type: TABLE; Schema: public
--

CREATE TABLE project (
    id bigint NOT NULL,
    name character varying(127) NOT NULL,
    ui_id character varying(23) NOT NULL,
    private boolean DEFAULT false NOT NULL,
    record_version bigint NOT NULL,
    documentation_md text NOT NULL,
    documentation_html text NOT NULL,
    task_number_sequence_id bigint NOT NULL,
    description_md character varying(10000) NOT NULL,
    description_html character varying(20000) NOT NULL,
    frozen boolean DEFAULT false NOT NULL,
    key character varying(10) NOT NULL,
    owner_user_id bigint NOT NULL
);




--
-- Name: project_application; Type: TABLE; Schema: public
--

CREATE TABLE project_application (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    project_id bigint NOT NULL,
    text text NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    status_id bigint NOT NULL,
    reported boolean DEFAULT false NOT NULL
);




--
-- Name: project_application_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE project_application_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: project_application_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE project_application_id_seq OWNED BY project_application.id;


--
-- Name: project_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE project_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: project_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE project_id_seq OWNED BY project.id;


--
-- Name: project_invite; Type: TABLE; Schema: public
--

CREATE TABLE project_invite (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    project_id bigint NOT NULL,
    text text NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    status_id bigint NOT NULL,
    reported boolean DEFAULT false NOT NULL,
    sender_user_id bigint NOT NULL
);




--
-- Name: project_invite_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE project_invite_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: project_invite_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE project_invite_id_seq OWNED BY project_invite.id;


--
-- Name: project_invite_token; Type: TABLE; Schema: public
--

CREATE TABLE project_invite_token (
    id bigint NOT NULL,
    invite_id bigint NOT NULL,
    token character varying(23) NOT NULL,
    email_address character varying(256) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    password_hash character varying(128) NOT NULL
);




--
-- Name: project_invite_token_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE project_invite_token_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: project_invite_token_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE project_invite_token_id_seq OWNED BY project_invite_token.id;


--
-- Name: project_member; Type: TABLE; Schema: public
--

CREATE TABLE project_member (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    project_id bigint NOT NULL,
    write_allowed boolean DEFAULT false NOT NULL,
    manager boolean DEFAULT false NOT NULL,
    hired_at timestamp with time zone NOT NULL,
    fired_at timestamp with time zone,
    fired boolean DEFAULT false NOT NULL
);




--
-- Name: project_member_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE project_member_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: project_member_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE project_member_id_seq OWNED BY project_member.id;


--
-- Name: project_task_number_sequence; Type: TABLE; Schema: public
--

CREATE TABLE project_task_number_sequence (
    id bigint NOT NULL,
    next_task_number bigint NOT NULL
);




--
-- Name: project_task_number_sequence_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE project_task_number_sequence_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: project_task_number_sequence_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE project_task_number_sequence_id_seq OWNED BY project_task_number_sequence.id;


--
-- Name: road; Type: TABLE; Schema: public
--

CREATE TABLE road (
    id bigint NOT NULL,
    record_version bigint NOT NULL,
    project_id bigint NOT NULL,
    name character varying(127) NOT NULL,
    previous_id bigint,
    deleted boolean DEFAULT false NOT NULL
);




--
-- Name: road_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE road_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: road_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE road_id_seq OWNED BY road.id;


--
-- Name: task; Type: TABLE; Schema: public
--

CREATE TABLE task (
    id bigint NOT NULL,
    record_version bigint NOT NULL,
    number bigint NOT NULL,
    summary character varying(80) NOT NULL,
    reporter_project_member_id bigint NOT NULL,
    assignee_project_member_id bigint,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    type_id bigint NOT NULL,
    priority_id bigint NOT NULL,
    status_id bigint NOT NULL,
    project_id bigint NOT NULL,
    description_md character varying(10000) NOT NULL,
    description_html character varying(20000) NOT NULL,
    hidden boolean DEFAULT false NOT NULL,
    task_card_id bigint
);




--
-- Name: task_card; Type: TABLE; Schema: public
--

CREATE TABLE task_card (
    id bigint NOT NULL,
    record_version bigint NOT NULL,
    project_id bigint NOT NULL,
    road_id bigint NOT NULL,
    name character varying(127) NOT NULL,
    status character varying(9) NOT NULL,
    active boolean NOT NULL,
    previous_id bigint,
    created_at timestamp with time zone NOT NULL,
    archieved_at timestamp with time zone
);




--
-- Name: task_card_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE task_card_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: task_card_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE task_card_id_seq OWNED BY task_card.id;


--
-- Name: task_hashtag; Type: TABLE; Schema: public
--

CREATE TABLE task_hashtag (
    id bigint NOT NULL,
    project_id bigint NOT NULL,
    name character varying(127) NOT NULL
);




--
-- Name: task_hashtag__task; Type: TABLE; Schema: public
--

CREATE TABLE task_hashtag__task (
    task_hashtag_id bigint NOT NULL,
    task_id bigint NOT NULL
);




--
-- Name: task_hashtag_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE task_hashtag_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: task_hashtag_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE task_hashtag_id_seq OWNED BY task_hashtag.id;


--
-- Name: task_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: task_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE task_id_seq OWNED BY task.id;


--
-- Name: task_link; Type: TABLE; Schema: public
--

CREATE TABLE task_link (
    task1_id bigint NOT NULL,
    task2_id bigint NOT NULL
);




--
-- Name: task_priority; Type: TABLE; Schema: public
--

CREATE TABLE task_priority (
    id bigint NOT NULL,
    name_code character varying(8) NOT NULL,
    importance integer NOT NULL
);




--
-- Name: task_priority_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE task_priority_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: task_priority_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE task_priority_id_seq OWNED BY task_priority.id;


--
-- Name: task_status; Type: TABLE; Schema: public
--

CREATE TABLE task_status (
    id bigint NOT NULL,
    name_code character varying(14) NOT NULL
);




--
-- Name: task_status_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE task_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: task_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE task_status_id_seq OWNED BY task_status.id;


--
-- Name: task_subscriber; Type: TABLE; Schema: public
--

CREATE TABLE task_subscriber (
    task_id bigint NOT NULL,
    user_id bigint NOT NULL
);




--
-- Name: task_type; Type: TABLE; Schema: public
--

CREATE TABLE task_type (
    id bigint NOT NULL,
    name_code character varying(11) NOT NULL
);




--
-- Name: task_type_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE task_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: task_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE task_type_id_seq OWNED BY task_type.id;


--
-- Name: temporary_attachment; Type: TABLE; Schema: public
--

CREATE TABLE temporary_attachment (
    uuid character varying(36) NOT NULL,
    filename character varying(511) NOT NULL,
    path character varying(511) NOT NULL,
    project_id bigint NOT NULL,
    created_at bigint NOT NULL
);




--
-- Name: user_favorite; Type: TABLE; Schema: public
--

CREATE TABLE user_favorite (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    project_id bigint,
    previous_id bigint
);




--
-- Name: user_favorite_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE user_favorite_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: user_favorite_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE user_favorite_id_seq OWNED BY user_favorite.id;


--
-- Name: user_profile; Type: TABLE; Schema: public
--

CREATE TABLE user_profile (
    id bigint NOT NULL,
    email_address character varying(256) NOT NULL,
    password_hash character varying(128) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    username character varying(255) NOT NULL,
    full_name character varying(255) NOT NULL,
    manager boolean DEFAULT false NOT NULL
);




--
-- Name: user_profile_id_seq; Type: SEQUENCE; Schema: public
--

CREATE SEQUENCE user_profile_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: user_profile_id_seq; Type: SEQUENCE OWNED BY; Schema: public
--

ALTER SEQUENCE user_profile_id_seq OWNED BY user_profile.id;


--
-- Name: application_status id; Type: DEFAULT; Schema: public
--

ALTER TABLE application_status ALTER COLUMN id SET DEFAULT nextval('application_status_id_seq'::regclass);


--
-- Name: attachment id; Type: DEFAULT; Schema: public
--

ALTER TABLE attachment ALTER COLUMN id SET DEFAULT nextval('attachment_id_seq'::regclass);


--
-- Name: email_activation_token id; Type: DEFAULT; Schema: public
--

ALTER TABLE email_activation_token ALTER COLUMN id SET DEFAULT nextval('email_activation_token_id_seq'::regclass);


--
-- Name: granted_permissions_project_invite id; Type: DEFAULT; Schema: public
--

ALTER TABLE granted_permissions_project_invite ALTER COLUMN id SET DEFAULT nextval('granted_permissions_project_invite_id_seq'::regclass);


--
-- Name: invite_status id; Type: DEFAULT; Schema: public
--

ALTER TABLE invite_status ALTER COLUMN id SET DEFAULT nextval('invite_status_id_seq'::regclass);


--
-- Name: issue id; Type: DEFAULT; Schema: public
--

ALTER TABLE issue ALTER COLUMN id SET DEFAULT nextval('issue_id_seq'::regclass);


--
-- Name: login_statistics id; Type: DEFAULT; Schema: public
--

ALTER TABLE login_statistics ALTER COLUMN id SET DEFAULT nextval('login_statistics_id_seq'::regclass);


--
-- Name: memo id; Type: DEFAULT; Schema: public
--

ALTER TABLE memo ALTER COLUMN id SET DEFAULT nextval('memo_id_seq'::regclass);


--
-- Name: password_activation_token id; Type: DEFAULT; Schema: public
--

ALTER TABLE password_activation_token ALTER COLUMN id SET DEFAULT nextval('activation_token_id_seq'::regclass);


--
-- Name: project id; Type: DEFAULT; Schema: public
--

ALTER TABLE project ALTER COLUMN id SET DEFAULT nextval('project_id_seq'::regclass);


--
-- Name: project_application id; Type: DEFAULT; Schema: public
--

ALTER TABLE project_application ALTER COLUMN id SET DEFAULT nextval('project_application_id_seq'::regclass);


--
-- Name: project_invite id; Type: DEFAULT; Schema: public
--

ALTER TABLE project_invite ALTER COLUMN id SET DEFAULT nextval('project_invite_id_seq'::regclass);


--
-- Name: project_invite_token id; Type: DEFAULT; Schema: public
--

ALTER TABLE project_invite_token ALTER COLUMN id SET DEFAULT nextval('project_invite_token_id_seq'::regclass);


--
-- Name: project_member id; Type: DEFAULT; Schema: public
--

ALTER TABLE project_member ALTER COLUMN id SET DEFAULT nextval('project_member_id_seq'::regclass);


--
-- Name: project_task_number_sequence id; Type: DEFAULT; Schema: public
--

ALTER TABLE project_task_number_sequence ALTER COLUMN id SET DEFAULT nextval('project_task_number_sequence_id_seq'::regclass);


--
-- Name: road id; Type: DEFAULT; Schema: public
--

ALTER TABLE road ALTER COLUMN id SET DEFAULT nextval('road_id_seq'::regclass);


--
-- Name: task id; Type: DEFAULT; Schema: public
--

ALTER TABLE task ALTER COLUMN id SET DEFAULT nextval('task_id_seq'::regclass);


--
-- Name: task_card id; Type: DEFAULT; Schema: public
--

ALTER TABLE task_card ALTER COLUMN id SET DEFAULT nextval('task_card_id_seq'::regclass);


--
-- Name: task_hashtag id; Type: DEFAULT; Schema: public
--

ALTER TABLE task_hashtag ALTER COLUMN id SET DEFAULT nextval('task_hashtag_id_seq'::regclass);


--
-- Name: task_priority id; Type: DEFAULT; Schema: public
--

ALTER TABLE task_priority ALTER COLUMN id SET DEFAULT nextval('task_priority_id_seq'::regclass);


--
-- Name: task_status id; Type: DEFAULT; Schema: public
--

ALTER TABLE task_status ALTER COLUMN id SET DEFAULT nextval('task_status_id_seq'::regclass);


--
-- Name: task_type id; Type: DEFAULT; Schema: public
--

ALTER TABLE task_type ALTER COLUMN id SET DEFAULT nextval('task_type_id_seq'::regclass);


--
-- Name: user_favorite id; Type: DEFAULT; Schema: public
--

ALTER TABLE user_favorite ALTER COLUMN id SET DEFAULT nextval('user_favorite_id_seq'::regclass);


--
-- Name: user_profile id; Type: DEFAULT; Schema: public
--

ALTER TABLE user_profile ALTER COLUMN id SET DEFAULT nextval('user_profile_id_seq'::regclass);


--
-- Name: password_activation_token activation_token__token_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE password_activation_token
    ADD CONSTRAINT activation_token__token_uniq UNIQUE (token);


--
-- Name: password_activation_token activation_token__user_id_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE password_activation_token
    ADD CONSTRAINT activation_token__user_id_uniq UNIQUE (user_id);


--
-- Name: password_activation_token activation_token_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE password_activation_token
    ADD CONSTRAINT activation_token_pkey PRIMARY KEY (id);


--
-- Name: application_status application_status__name_code_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE application_status
    ADD CONSTRAINT application_status__name_code_uniq UNIQUE (name_code);


--
-- Name: application_status application_status_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE application_status
    ADD CONSTRAINT application_status_pkey PRIMARY KEY (id);


--
-- Name: attachment attachment_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE attachment
    ADD CONSTRAINT attachment_pkey PRIMARY KEY (id);


--
-- Name: email_activation_token email_activation_token__email_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE email_activation_token
    ADD CONSTRAINT email_activation_token__email_uniq UNIQUE (email_address);


--
-- Name: email_activation_token email_activation_token_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE email_activation_token
    ADD CONSTRAINT email_activation_token_pkey PRIMARY KEY (id);


--
-- Name: granted_permissions_project_invite granted_permissions_project_invite_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE granted_permissions_project_invite
    ADD CONSTRAINT granted_permissions_project_invite_pkey PRIMARY KEY (id);


--
-- Name: invite_status invite_status__name_code_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE invite_status
    ADD CONSTRAINT invite_status__name_code_uniq UNIQUE (name_code);


--
-- Name: invite_status invite_status_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE invite_status
    ADD CONSTRAINT invite_status_pkey PRIMARY KEY (id);


--
-- Name: issue issue_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE issue
    ADD CONSTRAINT issue_pkey PRIMARY KEY (id);


--
-- Name: jira_upload jira_upload__organization_id__upload_timestamp_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE jira_upload
    ADD CONSTRAINT jira_upload__organization_id__upload_timestamp_uniq UNIQUE (organization_id, upload_timestamp);


--
-- Name: login_statistics login_statistics_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE login_statistics
    ADD CONSTRAINT login_statistics_pkey PRIMARY KEY (id);


--
-- Name: memo memo_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE memo
    ADD CONSTRAINT memo_pkey PRIMARY KEY (id);


--
-- Name: notification_settings notification_settings__user_id_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE notification_settings
    ADD CONSTRAINT notification_settings__user_id_uniq UNIQUE (user_id);


--
-- Name: project project__task_number_sequence_id__uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE project
    ADD CONSTRAINT project__task_number_sequence_id__uniq UNIQUE (task_number_sequence_id);


--
-- Name: project project__ui_id_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE project
    ADD CONSTRAINT project__ui_id_uniq UNIQUE (ui_id);


--
-- Name: project_application project_application_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE project_application
    ADD CONSTRAINT project_application_pkey PRIMARY KEY (id);


--
-- Name: project_invite project_invite_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE project_invite
    ADD CONSTRAINT project_invite_pkey PRIMARY KEY (id);


--
-- Name: project_invite_token project_invite_token__invite_id_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE project_invite_token
    ADD CONSTRAINT project_invite_token__invite_id_uniq UNIQUE (invite_id);


--
-- Name: project_invite_token project_invite_token__token_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE project_invite_token
    ADD CONSTRAINT project_invite_token__token_uniq UNIQUE (token);


--
-- Name: project_invite_token project_invite_token_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE project_invite_token
    ADD CONSTRAINT project_invite_token_pkey PRIMARY KEY (id);


--
-- Name: project_member project_member_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE project_member
    ADD CONSTRAINT project_member_pkey PRIMARY KEY (id);


--
-- Name: project project_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE project
    ADD CONSTRAINT project_pkey PRIMARY KEY (id);


--
-- Name: project_task_number_sequence project_task_number_sequence_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE project_task_number_sequence
    ADD CONSTRAINT project_task_number_sequence_pkey PRIMARY KEY (id);


--
-- Name: road road_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE road
    ADD CONSTRAINT road_pkey PRIMARY KEY (id);


--
-- Name: task task__project_id_number_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task
    ADD CONSTRAINT task__project_id_number_uniq UNIQUE (number, project_id);


--
-- Name: task_card task_card_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task_card
    ADD CONSTRAINT task_card_pkey PRIMARY KEY (id);


--
-- Name: task_hashtag task_hashtag__name_project_id_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task_hashtag
    ADD CONSTRAINT task_hashtag__name_project_id_uniq UNIQUE (name, project_id);


--
-- Name: task_hashtag__task task_hashtag__task__pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task_hashtag__task
    ADD CONSTRAINT task_hashtag__task__pkey PRIMARY KEY (task_id, task_hashtag_id);


--
-- Name: task_hashtag task_hashtag_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task_hashtag
    ADD CONSTRAINT task_hashtag_pkey PRIMARY KEY (id);


--
-- Name: task task_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task
    ADD CONSTRAINT task_pkey PRIMARY KEY (id);


--
-- Name: task_priority task_priority__importance_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task_priority
    ADD CONSTRAINT task_priority__importance_uniq UNIQUE (importance);


--
-- Name: task_priority task_priority__name_code_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task_priority
    ADD CONSTRAINT task_priority__name_code_uniq UNIQUE (name_code);


--
-- Name: task_priority task_priority_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task_priority
    ADD CONSTRAINT task_priority_pkey PRIMARY KEY (id);


--
-- Name: task_status task_status__name_code_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task_status
    ADD CONSTRAINT task_status__name_code_uniq UNIQUE (name_code);


--
-- Name: task_status task_status_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task_status
    ADD CONSTRAINT task_status_pkey PRIMARY KEY (id);


--
-- Name: task_type task_type__name_code_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task_type
    ADD CONSTRAINT task_type__name_code_uniq UNIQUE (name_code);


--
-- Name: task_type task_type_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE task_type
    ADD CONSTRAINT task_type_pkey PRIMARY KEY (id);


--
-- Name: temporary_attachment temporary_attachment__uuid_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE temporary_attachment
    ADD CONSTRAINT temporary_attachment__uuid_uniq UNIQUE (uuid);


--
-- Name: user_favorite user_favorite_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE user_favorite
    ADD CONSTRAINT user_favorite_pkey PRIMARY KEY (id);


--
-- Name: user_profile user_profile__email_uniq; Type: CONSTRAINT; Schema: public
--

ALTER TABLE user_profile
    ADD CONSTRAINT user_profile__email_uniq UNIQUE (email_address);


--
-- Name: user_profile user_profile_pkey; Type: CONSTRAINT; Schema: public
--

ALTER TABLE user_profile
    ADD CONSTRAINT user_profile_pkey PRIMARY KEY (id);


--
-- Name: attachment__task_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX attachment__task_id_idx ON attachment USING btree (task_id);


--
-- Name: failed_login_attempt__failed_at_idx; Type: INDEX; Schema: public
--

CREATE INDEX failed_login_attempt__failed_at_idx ON failed_login_attempt USING btree (failed_at);


--
-- Name: failed_login_attempt__user_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX failed_login_attempt__user_id_idx ON failed_login_attempt USING btree (user_id);


--
-- Name: granted_perms_project_invite__project_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX granted_perms_project_invite__project_id_idx ON granted_permissions_project_invite USING btree (project_id);


--
-- Name: granted_perms_project_invite__sender_user_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX granted_perms_project_invite__sender_user_id_idx ON granted_permissions_project_invite USING btree (sender_user_id);


--
-- Name: granted_perms_project_invite__status_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX granted_perms_project_invite__status_id_idx ON granted_permissions_project_invite USING btree (status_id);


--
-- Name: granted_perms_project_invite__user_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX granted_perms_project_invite__user_id_idx ON granted_permissions_project_invite USING btree (user_id);


--
-- Name: imported_jira_task__task_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX imported_jira_task__task_id_idx ON imported_jira_task USING btree (task_id);


--
-- Name: issue__project_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX issue__project_id_idx ON issue USING btree (project_id);


--
-- Name: issue__user_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX issue__user_id_idx ON issue USING btree (user_id);


--
-- Name: login_statistics__user_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX login_statistics__user_id_idx ON login_statistics USING btree (user_id);


--
-- Name: memo__author_user_organization_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX memo__author_user_organization_id_idx ON memo USING btree (author_project_member_id);


--
-- Name: memo__project_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX memo__project_id_idx ON memo USING btree (project_id);


--
-- Name: project_application__project_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX project_application__project_id_idx ON project_application USING btree (project_id);


--
-- Name: project_application__status_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX project_application__status_id_idx ON project_application USING btree (status_id);


--
-- Name: project_application__user_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX project_application__user_id_idx ON project_application USING btree (user_id);


--
-- Name: project_invite__project_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX project_invite__project_id_idx ON project_invite USING btree (project_id);


--
-- Name: project_invite__sender_user_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX project_invite__sender_user_id_idx ON project_invite USING btree (sender_user_id);


--
-- Name: project_invite__status_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX project_invite__status_id_idx ON project_invite USING btree (status_id);


--
-- Name: project_invite__user_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX project_invite__user_id_idx ON project_invite USING btree (user_id);


--
-- Name: project_member__project_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX project_member__project_id_idx ON project_member USING btree (project_id);


--
-- Name: project_member__user_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX project_member__user_id_idx ON project_member USING btree (user_id);


--
-- Name: road__project_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX road__project_id_idx ON road USING btree (project_id);


--
-- Name: task__assignee_user_organization_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX task__assignee_user_organization_id_idx ON task USING btree (assignee_project_member_id);


--
-- Name: task__priority_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX task__priority_id_idx ON task USING btree (priority_id);


--
-- Name: task__reporter_user_organization_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX task__reporter_user_organization_id_idx ON task USING btree (reporter_project_member_id);


--
-- Name: task__status_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX task__status_id_idx ON task USING btree (status_id);


--
-- Name: task__type_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX task__type_id_idx ON task USING btree (type_id);


--
-- Name: task_card__archieved_at_idx; Type: INDEX; Schema: public
--

CREATE INDEX task_card__archieved_at_idx ON task_card USING btree (archieved_at);


--
-- Name: task_card__project_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX task_card__project_id_idx ON task_card USING btree (project_id);


--
-- Name: task_card__status_idx; Type: INDEX; Schema: public
--

CREATE INDEX task_card__status_idx ON task_card USING btree (status);


--
-- Name: task_hashtag__project_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX task_hashtag__project_id_idx ON task_hashtag USING btree (project_id);


--
-- Name: task_hashtag_task__task_hashtag_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX task_hashtag_task__task_hashtag_id_idx ON task_hashtag__task USING btree (task_hashtag_id);


--
-- Name: task_link__task1_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX task_link__task1_id_idx ON task_link USING btree (task1_id);


--
-- Name: task_subscriber__task_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX task_subscriber__task_id_idx ON task_subscriber USING btree (task_id);


--
-- Name: temporary_attachment__created_at_idx; Type: INDEX; Schema: public
--

CREATE INDEX temporary_attachment__created_at_idx ON temporary_attachment USING btree (created_at);


--
-- Name: user_favorite__project_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX user_favorite__project_id_idx ON user_favorite USING btree (project_id);


--
-- Name: user_favorite__user_id_idx; Type: INDEX; Schema: public
--

CREATE INDEX user_favorite__user_id_idx ON user_favorite USING btree (user_id);


--
-- Name: password_activation_token activation_token_user_id_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE password_activation_token
    ADD CONSTRAINT activation_token_user_id_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);


--
-- Name: attachment attachment__task_id__task_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE attachment
    ADD CONSTRAINT attachment__task_id__task_fkey FOREIGN KEY (task_id) REFERENCES task(id);


--
-- Name: failed_login_attempt failed_login_attempt__user_id__user_profile_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE failed_login_attempt
    ADD CONSTRAINT failed_login_attempt__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);


--
-- Name: imported_jira_project imported_jira_project__project_id__project_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE imported_jira_project
    ADD CONSTRAINT imported_jira_project__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: imported_jira_task imported_jira_task__project_id__project_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE imported_jira_task
    ADD CONSTRAINT imported_jira_task__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: imported_jira_task imported_jira_task__task_id__task_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE imported_jira_task
    ADD CONSTRAINT imported_jira_task__task_id__task_fkey FOREIGN KEY (task_id) REFERENCES task(id);


--
-- Name: issue issue__project_id__project_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE issue
    ADD CONSTRAINT issue__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: issue issue__user_id__user_profile_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE issue
    ADD CONSTRAINT issue__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);


--
-- Name: login_statistics login_statistics_user_id_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE login_statistics
    ADD CONSTRAINT login_statistics_user_id_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);


--
-- Name: memo memo__author_project_member_id__project_member_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE memo
    ADD CONSTRAINT memo__author_project_member_id__project_member_fkey FOREIGN KEY (author_project_member_id) REFERENCES project_member(id);


--
-- Name: memo memo__project_id__project_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE memo
    ADD CONSTRAINT memo__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: notification_settings notification_settings__user_id__user_profile_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE notification_settings
    ADD CONSTRAINT notification_settings__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);


--
-- Name: project project__owner_user_id__user_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE project
    ADD CONSTRAINT project__owner_user_id__user_fkey FOREIGN KEY (owner_user_id) REFERENCES user_profile(id);


--
-- Name: project project__task_number_sequence_id__task_number_sequence_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE project
    ADD CONSTRAINT project__task_number_sequence_id__task_number_sequence_fkey FOREIGN KEY (task_number_sequence_id) REFERENCES project_task_number_sequence(id);


--
-- Name: project_application project_application__project_id__project_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE project_application
    ADD CONSTRAINT project_application__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: project_application project_application__status_id__application_status_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE project_application
    ADD CONSTRAINT project_application__status_id__application_status_fkey FOREIGN KEY (status_id) REFERENCES application_status(id);


--
-- Name: project_application project_application__user_id__user_profile_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE project_application
    ADD CONSTRAINT project_application__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);


--
-- Name: project_invite project_invite__project_id__project_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE project_invite
    ADD CONSTRAINT project_invite__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: project_invite project_invite__sender_user_id__user_profile_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE project_invite
    ADD CONSTRAINT project_invite__sender_user_id__user_profile_fkey FOREIGN KEY (sender_user_id) REFERENCES user_profile(id);


--
-- Name: project_invite project_invite__status_id__invite_status_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE project_invite
    ADD CONSTRAINT project_invite__status_id__invite_status_fkey FOREIGN KEY (status_id) REFERENCES invite_status(id);


--
-- Name: project_invite project_invite__user_id__user_profile_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE project_invite
    ADD CONSTRAINT project_invite__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);


--
-- Name: project_invite_token project_invite_token__invite_id__granted_perms_proj_invite_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE project_invite_token
    ADD CONSTRAINT project_invite_token__invite_id__granted_perms_proj_invite_fkey FOREIGN KEY (invite_id) REFERENCES granted_permissions_project_invite(id);


--
-- Name: project_member project_member__project_id__project_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE project_member
    ADD CONSTRAINT project_member__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: project_member project_member__user_id__user_profile_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE project_member
    ADD CONSTRAINT project_member__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);


--
-- Name: road road__previous_id__road_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE road
    ADD CONSTRAINT road__previous_id__road_fkey FOREIGN KEY (previous_id) REFERENCES road(id);


--
-- Name: road road__project_id__project_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE road
    ADD CONSTRAINT road__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: task task__assignee_proj_member_id__proj_member_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task
    ADD CONSTRAINT task__assignee_proj_member_id__proj_member_fkey FOREIGN KEY (assignee_project_member_id) REFERENCES project_member(id);


--
-- Name: task task__priority_id__task_priority_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task
    ADD CONSTRAINT task__priority_id__task_priority_fkey FOREIGN KEY (priority_id) REFERENCES task_priority(id);


--
-- Name: task task__project_id__project_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task
    ADD CONSTRAINT task__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: task task__reporter_proj_member_id__proj_member_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task
    ADD CONSTRAINT task__reporter_proj_member_id__proj_member_fkey FOREIGN KEY (reporter_project_member_id) REFERENCES project_member(id);


--
-- Name: task task__status_id__task_status_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task
    ADD CONSTRAINT task__status_id__task_status_fkey FOREIGN KEY (status_id) REFERENCES task_status(id);


--
-- Name: task task__task_card_id__task_card_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task
    ADD CONSTRAINT task__task_card_id__task_card_fkey FOREIGN KEY (task_card_id) REFERENCES task_card(id);


--
-- Name: task task__type_id__task_type_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task
    ADD CONSTRAINT task__type_id__task_type_fkey FOREIGN KEY (type_id) REFERENCES task_type(id);


--
-- Name: task_card task_card__previous_id__task_card_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task_card
    ADD CONSTRAINT task_card__previous_id__task_card_fkey FOREIGN KEY (previous_id) REFERENCES task_card(id);


--
-- Name: task_card task_card__project_id__project_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task_card
    ADD CONSTRAINT task_card__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: task_card task_card__road_id__road_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task_card
    ADD CONSTRAINT task_card__road_id__road_fkey FOREIGN KEY (road_id) REFERENCES road(id);


--
-- Name: task_hashtag task_hashtag__project_id__project_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task_hashtag
    ADD CONSTRAINT task_hashtag__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: task_hashtag__task task_hashtag_task__task_hashtag_id__task_hashtag_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task_hashtag__task
    ADD CONSTRAINT task_hashtag_task__task_hashtag_id__task_hashtag_fkey FOREIGN KEY (task_hashtag_id) REFERENCES task_hashtag(id);


--
-- Name: task_hashtag__task task_hashtag_task__task_id__task_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task_hashtag__task
    ADD CONSTRAINT task_hashtag_task__task_id__task_fkey FOREIGN KEY (task_id) REFERENCES task(id);


--
-- Name: task_link task_link__task1_id__task_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task_link
    ADD CONSTRAINT task_link__task1_id__task_fkey FOREIGN KEY (task1_id) REFERENCES task(id);


--
-- Name: task_link task_link__task2_id__task_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task_link
    ADD CONSTRAINT task_link__task2_id__task_fkey FOREIGN KEY (task2_id) REFERENCES task(id);


--
-- Name: task_subscriber task_subscriber__task_id__task_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task_subscriber
    ADD CONSTRAINT task_subscriber__task_id__task_fkey FOREIGN KEY (task_id) REFERENCES task(id);


--
-- Name: task_subscriber task_subscriber__user_id__user_profile_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE task_subscriber
    ADD CONSTRAINT task_subscriber__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);


--
-- Name: temporary_attachment temporary_attachment__project_id__dropbox_settings_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE temporary_attachment
    ADD CONSTRAINT temporary_attachment__project_id__dropbox_settings_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: user_favorite user_favorite__previous_id__user_favorite_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE user_favorite
    ADD CONSTRAINT user_favorite__previous_id__user_favorite_fkey FOREIGN KEY (previous_id) REFERENCES user_favorite(id);


--
-- Name: user_favorite user_favorite__project_id__project_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE user_favorite
    ADD CONSTRAINT user_favorite__project_id__project_fkey FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: user_favorite user_favorite__user_id__user_profile_fkey; Type: FK CONSTRAINT; Schema: public
--

ALTER TABLE user_favorite
    ADD CONSTRAINT user_favorite__user_id__user_profile_fkey FOREIGN KEY (user_id) REFERENCES user_profile(id);


INSERT INTO task_type(name_code) VALUES('TASK');
INSERT INTO task_type(name_code) VALUES('BUG');
INSERT INTO task_type(name_code) VALUES('IMPROVEMENT');
INSERT INTO task_type(name_code) VALUES('NEW_FEATURE');

INSERT INTO task_priority(name_code, importance) VALUES('TRIVIAL', 10);
INSERT INTO task_priority(name_code, importance) VALUES('MINOR', 20);
INSERT INTO task_priority(name_code, importance) VALUES('MAJOR', 30);
INSERT INTO task_priority(name_code, importance) VALUES('CRITICAL', 40);
INSERT INTO task_priority(name_code, importance) VALUES('BLOCKING', 50);

INSERT INTO task_status(name_code) VALUES('OPEN');
INSERT INTO task_status(name_code) VALUES('REJECTED');
INSERT INTO task_status(name_code) VALUES('IN_DEVELOPMENT');
INSERT INTO task_status(name_code) VALUES('WAITING_FOR_QA');
INSERT INTO task_status(name_code) VALUES('IN_QA_REVIEW');
INSERT INTO task_status(name_code) VALUES('CLOSED');

INSERT INTO invite_status(name_code) VALUES('PENDING');
INSERT INTO invite_status(name_code) VALUES('REJECTED');
INSERT INTO invite_status(name_code) VALUES('ACCEPTED');

INSERT INTO application_status(name_code) VALUES('PENDING');
INSERT INTO application_status(name_code) VALUES('REJECTED');
