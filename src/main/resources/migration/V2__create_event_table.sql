CREATE TABLE event
(
    event_id               integer NOT NULL AUTO_INCREMENT,
    event_name             varchar(255),
    event_date             datetime,
    available_for_public   bit,
    event_price            double,
    number_of_sold_tickets integer,
    music_venue_id         integer,
    deleted                bit,
    deleted_at             datetime,
    PRIMARY KEY (event_id)
);

ALTER TABLE event ADD CONSTRAINT fk_event_music_venue FOREIGN KEY (music_venue_id) REFERENCES music_venue (music_venue_id);