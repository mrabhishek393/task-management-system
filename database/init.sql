-- Create the user
CREATE USER taskuser WITH PASSWORD '123456789';

-- Create the database owned by taskuser
CREATE DATABASE taskmanager OWNER taskuser;

-- Grant all privileges on the database to taskuser
GRANT ALL PRIVILEGES ON DATABASE taskmanager TO taskuser;

-- \c taskmanager

-- After switching to the taskmanager database, start creating tables

CREATE TABLE users (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tasks (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status_id BIGINT
);

CREATE TABLE task_statuses (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    task_id BIGINT NOT NULL,
    status_name VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT task_statuses_priority_check CHECK (priority IN ('Low', 'Medium', 'High'))
);

CREATE TABLE IF NOT EXISTS outbox (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    aggregate_id VARCHAR(255),
    event_type VARCHAR(255),
    payload TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    published BOOLEAN DEFAULT FALSE
);

-- Foreign keys
ALTER TABLE tasks
ADD CONSTRAINT fk_status_id FOREIGN KEY (status_id) REFERENCES task_statuses(id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE task_statuses
ADD CONSTRAINT fk_task_id FOREIGN KEY (task_id) REFERENCES tasks(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- Changing ownership of the tables (this ensures taskuser owns the tables)
ALTER TABLE users OWNER TO taskuser;
ALTER TABLE tasks OWNER TO taskuser;
ALTER TABLE task_statuses OWNER TO taskuser;
ALTER TABLE outbox OWNER TO taskuser;

-- Grant all privileges on each table to taskuser (just to be sure)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO taskuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO taskuser;
