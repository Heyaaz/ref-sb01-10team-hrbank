-- files
CREATE TABLE binary_contents
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size         BIGINT       NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL
);

-- backups
CREATE TABLE backups
(
    id                SERIAL PRIMARY KEY,
    created_at        TIMESTAMPTZ  NOT NULL,
    binary_content_id INTEGER,
    started_at        TIMESTAMPTZ,
    ended_at          TIMESTAMPTZ,
    worker_ip_address VARCHAR(255) NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    CONSTRAINT fk_binary_contents FOREIGN KEY (binary_content_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);

-- departments
CREATE TABLE departments
(
    id               SERIAL PRIMARY KEY,
    created_at       TIMESTAMPTZ  NOT NULL,
    updated_at       TIMESTAMPTZ,
    name             VARCHAR(50)  NOT NULL,
    description      VARCHAR(255) NOT NULL,
    established_date TIMESTAMPTZ  NOT NULL,
    CONSTRAINT departments_name_unique UNIQUE (name)
);

-- employees
CREATE TABLE employees
(
    id               SERIAL PRIMARY KEY,
    created_at       TIMESTAMPTZ  NOT NULL,
    updated_at       TIMESTAMPTZ,
    department_id    INTEGER,
    profile_image_id INTEGER,
    name             VARCHAR(50)  NOT NULL,
    email            VARCHAR(100) NOT NULL,
    employee_number  VARCHAR(255) NOT NULL,
    position         VARCHAR(255),
    hire_date        TIMESTAMPTZ  NOT NULL,
    status           VARCHAR(20)  NOT NULL,

    CONSTRAINT employees_email_unique UNIQUE (email),
    CONSTRAINT employees_employee_number_unique UNIQUE (employee_number),

    CONSTRAINT fk_employees_department FOREIGN KEY (department_id)
        REFERENCES departments (id) ON DELETE RESTRICT,

    CONSTRAINT fk_profiles_employee FOREIGN KEY (profile_image_id)
        REFERENCES binary_contents (id) ON DELETE CASCADE
);

CREATE TABLE employee_histories
(
    id              SERIAL PRIMARY KEY,
    employee_number VARCHAR(50) NOT NULL,
    type            VARCHAR(20) NOT NULL,
    memo            TEXT,
    logged_at       TIMESTAMPTZ NOT NULL,
    modified_at     TIMESTAMPTZ NOT NULL,
    ip_address      VARCHAR(50) NOT NULL,
    changed_fields  jsonb       NOT NULL
);






