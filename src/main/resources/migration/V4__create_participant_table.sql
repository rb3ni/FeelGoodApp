CREATE TABLE participant
(
    participant_id    integer NOT NULL AUTO_INCREMENT,
    participant_name  varchar(255),
    participant_email varchar(255),
    event_id          integer,
    PRIMARY KEY (participant_id)
);

ALTER TABLE participant ADD CONSTRAINT fk_participant_event FOREIGN KEY (event_id) REFERENCES event (event_id);