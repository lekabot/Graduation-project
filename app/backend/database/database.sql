CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(250),
    password VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS log_actions (
    id SERIAL PRIMARY KEY,
    title VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS agreement_types (
    id SERIAL PRIMARY KEY,
    title VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS countries (
    id SERIAL PRIMARY KEY,
    title VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS  manufacturers (
    id SERIAL PRIMARY KEY,
    title VARCHAR(250),
    business_form VARCHAR(50),
    country_id INT,
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

CREATE TABLE IF NOT EXISTS  equipment_documents (
    id SERIAL PRIMARY KEY,
    agreement_type int,
    contract_number int,
    begin_date date,
    FOREIGN KEY (agreement_type) REFERENCES agreement_types(id)
);

CREATE TABLE IF NOT EXISTS  equipments (
    id SERIAL PRIMARY KEY,
    measured_parameters varchar(250),
    title VARCHAR(250),
    manufacturer_id INT,
    year_of_commissioning DATE,
    measurement_range VARCHAR(250),
    accuracy_class VARCHAR(250),
    property_rights_info_id INT,
    installation_location VARCHAR(250),
    FOREIGN KEY (property_rights_info_id) REFERENCES equipment_documents(id),
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(id)
);

CREATE TABLE IF NOT EXISTS  attestations (
    id SERIAL PRIMARY KEY,
    equipment INT,
    validity_period DATE,
    event_duration DATE,
    FOREIGN KEY (equipment) REFERENCES equipments(id)
);

CREATE TABLE IF NOT EXISTS  checks (
    id SERIAL PRIMARY KEY,
    equipment INT,
    last_check DATE,
    validity_period DATE,
    planned_inspection DATE,
    FOREIGN KEY (equipment) REFERENCES equipments(id)
);

CREATE TABLE IF NOT EXISTS  user_log (
    id SERIAL PRIMARY KEY,
    "user" int,
    equipment int,
    action int,
    log_date date,
    FOREIGN KEY ("user") REFERENCES users(id),
    FOREIGN KEY (equipment) REFERENCES equipments(id),
    FOREIGN KEY (action) REFERENCES log_actions(id)
);

-- CREATE INDEX idx_equipments_title ON equipments (title);
-- CREATE INDEX idx_equipments_manufacturer_id ON equipments (manufacturer_id);
-- CREATE INDEX idx_equipments_year_of_commissioning ON equipments (year_of_commissioning);

INSERT INTO users (username, password) VALUES
('user1', '1'),
('user2', '2'),
('user3', '3');

INSERT INTO log_actions (title) VALUES
('Action 1'),
('Action 2'),
('Action 3');

INSERT INTO agreement_types (title) VALUES
('Type 1'),
('Type 2'),
('Type 3');

INSERT INTO countries (title) VALUES
('Country 1'),
('Country 2'),
('Country 3');

INSERT INTO manufacturers (title, business_form, country_id) VALUES
('Manufacturer 1', 'Form 1', 1),
('Manufacturer 2', 'Form 2', 2),
('Manufacturer 3', 'Form 3', 3);

INSERT INTO equipment_documents (agreement_type, contract_number, begin_date) VALUES
(1, 1001, '2023-01-01'),
(2, 1002, '2023-02-01'),
(3, 1003, '2023-03-01');

INSERT INTO equipments (measured_parameters, title, manufacturer_id, year_of_commissioning, measurement_range, accuracy_class, property_rights_info_id, installation_location) VALUES
('Parameter 1', 'Equipment 1', 1, '2023-01-01', 'Range 1', 'Class 1', 1, 'Location 1'),
('Parameter 2', 'Equipment 2', 2, '2023-02-01', 'Range 2', 'Class 2', 2, 'Location 2'),
('Parameter 3', 'Equipment 3', 3, '2023-03-01', 'Range 3', 'Class 3', 3, 'Location 3');

INSERT INTO attestations (equipment, validity_period, event_duration) VALUES
(1, '2024-01-01', '2024-01-15'),
(2, '2024-02-01', '2024-02-15'),
(3, '2024-03-01', '2024-03-15');

INSERT INTO checks (equipment, last_check, validity_period, planned_inspection) VALUES
(1, '2023-12-01', '2024-01-01', '2024-01-15'),
(2, '2024-01-01', '2024-02-01', '2024-02-15'),
(3, '2024-02-01', '2024-03-01', '2024-03-15');

INSERT INTO user_log ("user", equipment, action, log_date) VALUES
(1, 1, 1, '2024-01-01'),
(2, 2, 2, '2024-02-01'),
(3, 3, 3, '2024-03-01');

