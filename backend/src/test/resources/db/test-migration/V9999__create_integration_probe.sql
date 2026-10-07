CREATE TABLE integration_probe (
    id BIGINT PRIMARY KEY,
    description VARCHAR(50) NOT NULL
);

INSERT INTO integration_probe (id, description)
VALUES (1, 'CRT');