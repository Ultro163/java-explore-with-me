-- DROP TABLE IF EXISTS requests;
-- DROP TABLE IF EXISTS event_likes;
-- DROP TABLE IF EXISTS compilation_event;
-- DROP TABLE IF EXISTS events;
-- DROP TABLE IF EXISTS users;
-- DROP TABLE IF EXISTS categories;
-- DROP TABLE IF EXISTS locations;
-- DROP TABLE IF EXISTS compilation;

CREATE TABLE IF NOT EXISTS users
(
    id    bigint generated by default as identity,
    name  varchar(250)        not null,
    email varchar(254) unique not null,
    CONSTRAINT users_pk PRIMARY KEY (id),
    CONSTRAINT name_min_length CHECK (char_length(name) >= 2),
    CONSTRAINT email_min_length CHECK (char_length(email) >= 6)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   bigint generated by default as identity,
    name varchar(50) unique not null,
    CONSTRAINT categories_pk primary key (id)
);

CREATE table if not exists locations
(
    id  bigint generated by default as identity,
    lat float,
    lon float,
    CONSTRAINT locations_pk primary key (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 bigint generated by default as identity,
    annotation         varchar                     not null,
    category_id        bigint                      not null,
    confirmed_requests integer,
    created_on         timestamp without time zone,
    description        varchar,
    event_date         timestamp without time zone not null,
    initiator_id       bigint                      not null,
    location_id        bigint                      not null,
    paid               boolean                     not null,
    participant_limit  integer,
    published_on       timestamp without time zone,
    request_moderation boolean,
    state              varchar,
    title              varchar                     not null,
    CONSTRAINT events_pk primary key (id),
    CONSTRAINT events_initiator_fk foreign key (initiator_id) REFERENCES users (id),
    CONSTRAINT events_location_fk foreign key (location_id) REFERENCES locations (id),
    CONSTRAINT events_category_fk foreign key (category_id) REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           bigint generated by default as identity,
    event_id     bigint,
    requester_id bigint,
    status       varchar,
    created      timestamp without time zone,
    CONSTRAINT requests_pk primary key (id),
    CONSTRAINT requests_event_fk foreign key (event_id) references events (id),
    CONSTRAINT requests_requester_fk foreign key (requester_id) references users (id)
);

CREATE TABLE IF NOT EXISTS compilation
(
    id     bigint generated by default as identity,
    pinned boolean,
    title  varchar(50),
    CONSTRAINT compilation_pk primary key (id)
);

CREATE TABLE IF NOT EXISTS compilation_event
(
    compilation_id bigint,
    event_id       bigint,
    CONSTRAINT compilation_event_compilation_id_fk foreign key (compilation_id) references compilation (id),
    CONSTRAINT compilation_event_event_id_fk foreign key (event_id) references events (id)
);

CREATE TABLE IF NOT EXISTS event_likes
(
    id       bigint generated by default as identity,
    event_id bigint,
    user_id  bigint,
    reaction varchar,
    CONSTRAINT event_likes_pk primary key (id),
    CONSTRAINT event_likes_event_fk foreign key (event_id) references events (id),
    CONSTRAINT event_likes_user_fk foreign key (user_id) references users (id),
    CONSTRAINT event_likes_unique unique (event_id, user_id, reaction)
);