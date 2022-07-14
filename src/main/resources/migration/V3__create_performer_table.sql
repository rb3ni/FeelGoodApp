CREATE TABLE performer
(
    performer_id            integer NOT NULL AUTO_INCREMENT,
    performer_name          varchar(255) UNIQUE,
    performer_email         varchar(255),
    contact_phone_number    varchar(255),
    performer_genre         varchar(255),
    performer_partner_level varchar(255),
    deleted                 bit,
    deleted_at              datetime,
    PRIMARY KEY (performer_id)
);