INSERT INTO thing (title) VALUES
('Thing 1'),
('Thing 2'),
('Thing 3');

INSERT INTO parameter (key, value) VALUES
('param1', 'value1'),
('param2', 'value2'),
('param3', 'value3');

INSERT INTO thing_parameter (thing_id, parameter_id) VALUES
(1, 1),
(1, 2),
(2, 2),
(2, 3),
(3, 1);

INSERT INTO user (email, username, hashed_password) VALUES
('john@example.com', 'johndoe', 'hashed_password1'),
('jane@example.com', 'janedoe', 'hashed_password2'),
('admin@example.com', 'admin', 'hashed_password3');
