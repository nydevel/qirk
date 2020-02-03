INSERT INTO languages(id, name_code) VALUES(1, 'EN_US');
INSERT INTO languages(id, name_code) VALUES(2, 'RU_RU');

INSERT INTO task_type(id, name_code) VALUES(1, 'TASK');
INSERT INTO task_type(id, name_code) VALUES(2, 'BUG');
INSERT INTO task_type(id, name_code) VALUES(3, 'IMPROVEMENT');
INSERT INTO task_type(id, name_code) VALUES(4, 'NEW_FEATURE');

INSERT INTO task_priority(name_code, importance) VALUES('TRIVIAL', 10);
INSERT INTO task_priority(name_code, importance) VALUES('MINOR', 20);
INSERT INTO task_priority(name_code, importance) VALUES('MAJOR', 30);
INSERT INTO task_priority(name_code, importance) VALUES('CRITICAL', 40);
INSERT INTO task_priority(name_code, importance) VALUES('BLOCKING', 50);

INSERT INTO task_status(id, name_code) VALUES(1, 'OPEN');
INSERT INTO task_status(id, name_code) VALUES(2, 'REJECTED');
INSERT INTO task_status(id, name_code) VALUES(3, 'IN_DEVELOPMENT');
INSERT INTO task_status(id, name_code) VALUES(4, 'WAITING_FOR_QA');
INSERT INTO task_status(id, name_code) VALUES(5, 'IN_QA_REVIEW');
INSERT INTO task_status(id, name_code) VALUES(6, 'CLOSED');

INSERT INTO invite_status(name_code) VALUES('PENDING');
INSERT INTO invite_status(name_code) VALUES('REJECTED');
INSERT INTO invite_status(name_code) VALUES('ACCEPTED');

INSERT INTO application_status(name_code) VALUES('PENDING');
INSERT INTO application_status(name_code) VALUES('REJECTED');
