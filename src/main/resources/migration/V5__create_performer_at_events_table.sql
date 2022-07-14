CREATE TABLE performer_at_event
(
    performer_at_event_id integer NOT NULL AUTO_INCREMENT,
    event_id              integer,
    performer_id          integer,
    headliner             bit,
    registered_at         datetime,
    PRIMARY KEY (performer_at_event_id)
);

ALTER TABLE performer_at_event ADD CONSTRAINT fk_performer_at_event_event FOREIGN KEY (event_id) REFERENCES event (event_id);
ALTER TABLE performer_at_event ADD CONSTRAINT fk_performer_at_event_performer FOREIGN KEY (performer_id) REFERENCES performer (performer_id);