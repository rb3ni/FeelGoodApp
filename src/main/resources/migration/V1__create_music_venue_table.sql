CREATE TABLE music_venue
(
    music_venue_id       integer NOT NULL AUTO_INCREMENT,
    music_venue_name     varchar(255) UNIQUE,
    contact_phone_number varchar(255),
    music_venue_address  varchar(255),
    music_venue_capacity integer,
    music_venue_type     varchar(255),
    deleted              bit,
    deleted_at           datetime,
    PRIMARY KEY (music_venue_id)
);