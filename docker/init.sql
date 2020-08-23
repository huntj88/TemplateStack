CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users
(
    user_id       uuid         NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name    varchar(255) NOT NULL,
    last_name     varchar(255) NOT NULL,
    created_at    timestamptz  NOT NULL             DEFAULT CURRENT_TIMESTAMP,
    auth_provider varchar(25)  NOT NULL,
    auth_id       varchar(100) NOT NULL
);
