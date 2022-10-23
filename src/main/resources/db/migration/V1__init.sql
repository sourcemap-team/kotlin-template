create table if not exists backend_kotlin_template.role
(
    id                serial primary key,
    created_at        timestamp not null,
    last_modified_at  timestamp not null,
    role_name         varchar   not null unique,
    role_display_name varchar   not null,
    invite            boolean   not null,
    provide           boolean   not null
);

create table if not exists backend_kotlin_template.users
(
    id                  serial primary key,
    username            varchar not null unique,
    password            varchar,
    created_at          timestamp not null,
    last_modified_at    timestamp not null,
    profile_picture_url varchar,
    role_id             integer not null constraint fk_users_role references backend_kotlin_template.role
);

create table if not exists backend_kotlin_template.invitation_token
(
    token           varchar not null primary key,
    used            boolean,
    creator_id      integer not null constraint fk_invitation_token_users references backend_kotlin_template.users,
    ttl_sec         integer,
    created_at      timestamp not null,
    used_by_user_id integer constraint fk_invitation_token_used_by_user references backend_kotlin_template.users,
    used_at         timestamp
);

create table if not exists backend_kotlin_template.contact
(
    id               serial primary key,
    created_at       timestamp not null,
    last_modified_at timestamp not null,
    user_id          integer constraint fk_user references backend_kotlin_template.users,
    contact_value    varchar not null unique,
    contact_type     varchar not null
);

create table if not exists backend_kotlin_template.user_provider_relationship
(
    id               serial primary key,
    created_at       timestamp not null,
    last_modified_at timestamp not null,
    provider_id      integer not null constraint fk_user_provider_relationship_users_provider_id references backend_kotlin_template.users,
    user_id          integer not null unique constraint fk_user_provider_relationship_users_user_id references backend_kotlin_template.users,
    comission_rate   numeric(13, 8) not null,
    unique (provider_id, user_id),
    constraint user_provider_relationship_check check (provider_id <> user_id)
);