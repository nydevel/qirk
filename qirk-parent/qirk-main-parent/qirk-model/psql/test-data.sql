INSERT INTO languages(name_code) VALUES('EN_US');
INSERT INTO languages(name_code) VALUES('RU_RU');

INSERT INTO user_profile(username, email_address, password_hash, full_name, created_at, about) VALUES('a', 'a@a.com', 'c45a0a774bd38ffd4634ca52b568934686286911216a22d8b85df262e8acea07f190e9d4470fde92e67ccef462a849f24713b310c58ac992fba4bbae9b0b3a86', 'a', NOW(), '');
INSERT INTO user_profile(username, email_address, password_hash, full_name, created_at, about) VALUES('b', 'b@b.com', 'c45a0a774bd38ffd4634ca52b568934686286911216a22d8b85df262e8acea07f190e9d4470fde92e67ccef462a849f24713b310c58ac992fba4bbae9b0b3a86', 'b', NOW(), '');
INSERT INTO user_profile(username, email_address, password_hash, full_name, created_at, about) VALUES('c', 'c@c.com', 'c45a0a774bd38ffd4634ca52b568934686286911216a22d8b85df262e8acea07f190e9d4470fde92e67ccef462a849f24713b310c58ac992fba4bbae9b0b3a86', 'c', NOW(), '');
UPDATE user_profile SET license_accepted = true;

INSERT INTO organization (name, ui_id, private, record_version, predefined_for_user, owner_user_id) VALUES ('a', 'a', false, 1, true, 1);
INSERT INTO organization (name, ui_id, private, record_version, predefined_for_user, owner_user_id) VALUES ('b', 'b', false, 1, true, 2);
INSERT INTO organization (name, ui_id, private, record_version, predefined_for_user, owner_user_id) VALUES ('c', 'c', false, 1, true, 3);

INSERT INTO user_organization (user_id, organization_id, enabled, manager, record_version) VALUES (1, 1, true, true, 1);
INSERT INTO user_organization (user_id, organization_id, enabled, manager, record_version) VALUES (2, 2, true, true, 1);
INSERT INTO user_organization (user_id, organization_id, enabled, manager, record_version) VALUES (3, 3, true, true, 1);

INSERT INTO project_task_number_sequence (next_task_number) VALUES (1); 
INSERT INTO project (name, ui_id, organization_id, private, record_version, documentation_md, documentation_html, task_number_sequence_id, description_md, description_html, key) VALUES ('a', 'a', 1, false, 1, '', '', 1, '', '', 'A');
