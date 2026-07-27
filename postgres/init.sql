-- Create database if not exists (already created by POSTGRES_DB env var)
-- Additional initialization if needed

-- Create custom schema if needed
-- CREATE SCHEMA IF NOT EXISTS app_schema;

-- Set timezone
SET TIMEZONE = 'UTC';

DROP TABLE IF EXISTS USERS; 

CREATE TABLE USERS (
    id BIGSERIAL PRIMARY KEY, 
    name VARCHAR(255) NOT NULL,
    role VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
);