CREATE SCHEMA IF NOT EXISTS tech_schema;

CREATE TABLE IF NOT EXISTS tech_schema.technology (
    technology_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(90) NOT NULL
);

CREATE INDEX idx_technology_name ON tech_schema.technology(name);