CREATE SCHEMA IF NOT EXISTS tech_schema;

CREATE TABLE IF NOT EXISTS tech_schema.technology (
    technology_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(90) NOT NULL
);

CREATE TABLE IF NOT EXISTS tech_schema.technology_capacity (
    id BIGSERIAL PRIMARY KEY,
    technology_id BIGINT NOT NULL,
    capacity_id BIGINT NOT NULL,
    UNIQUE(technology_id, capacity_id),
    FOREIGN KEY (technology_id) REFERENCES tech_schema.technology(technology_id) ON DELETE CASCADE
);

CREATE INDEX idx_technology_name ON tech_schema.technology(name);
CREATE INDEX idx_technology_capacity_capacity_id ON tech_schema.technology_capacity(capacity_id);