INSERT INTO music_venue(music_venue_name, contact_phone_number, music_venue_address, music_venue_capacity, music_venue_type, deleted)
VALUES ('Budapest Park', '06-30-211-3221', 'Budapest, Soroksári út 60, 1095', 4000, 'BANDSTAND', 0);

INSERT INTO music_venue(music_venue_name, contact_phone_number, music_venue_address, music_venue_capacity, music_venue_type, deleted)
VALUES ('A38', '06-30-555-5555', 'Budapest, Petőfi híd, 1117', 700, 'CLUB', 0);

INSERT INTO music_venue(music_venue_name, contact_phone_number, music_venue_address, music_venue_capacity, music_venue_type, deleted)
VALUES ('Dürer Kert', '06-20-785-6655', 'Budapest, Öböl utca 1, 1117', 700, 'CLUB', 0);

INSERT INTO music_venue(music_venue_name, contact_phone_number, music_venue_address, music_venue_capacity, music_venue_type, deleted)
VALUES ('Budapest aréna', '06-70-474-4658', 'Budapest, Stefánia út 2, 1143', 12500, 'ARENA', 0);

INSERT INTO performer(performer_name, performer_email, contact_phone_number, performer_genre, performer_partner_level, deleted)
VALUES ('Képzelt Város', 'kepzelt.varos@gmail.com', '06-30-211-5555', 'POST_ROCK', 'TIER_1', 0);

INSERT INTO performer(performer_name, performer_email, contact_phone_number, performer_genre, performer_partner_level, deleted)
VALUES ('Billy Talent', 'bt@gmail.com', '06-70-455-4455', 'PUNK', 'TIER_3', 0);

INSERT INTO performer(performer_name, performer_email, contact_phone_number, performer_genre, performer_partner_level, deleted)
VALUES ('Elefánt', 'elefant@gmail.com', '06-20-345-4455', 'ROCK', 'TIER_2', 0);

INSERT INTO performer(performer_name, performer_email, contact_phone_number, performer_genre, performer_partner_level, deleted)
VALUES ('RHCP', 'rhcp@gmail.com', '06-70-455-4889', 'ROCK', 'TIER_5', 0);

INSERT INTO performer(performer_name, performer_email, contact_phone_number, performer_genre, performer_partner_level, deleted)
VALUES ('Yara', 'yara@gmail.com', '06-30-455-2544', 'POST_ROCK', 'TIER_1', 0);

INSERT INTO event(event_name, event_date, available_for_public, event_price, number_of_sold_tickets, music_venue_id, deleted)
VALUES ('Elefánt - Budapest Park', '2022-12-04 17:00:00', 1, 5000.0, 3998, 1, 0);

INSERT INTO event(event_name, event_date, available_for_public, event_price, number_of_sold_tickets, music_venue_id, deleted)
VALUES ('No main performer yet - Budapest Park', '2022-05-04 17:00:00', 0, 0.0, 200, 1, 0);

INSERT INTO performer_at_event(event_id, performer_id, headliner, registered_at)
VALUES (1, 3, 1, '2022-06-19 17:00:00');

INSERT INTO performer_at_event(event_id, performer_id, headliner, registered_at)
VALUES (1, 1, 0, '2022-06-19 17:00:00');

INSERT INTO performer_at_event(event_id, performer_id, headliner, registered_at)
VALUES (1, 5, 0, '2022-06-19 17:00:00');

INSERT INTO participant(participant_name, participant_email, event_id)
VALUES ('Szendrői Csaba', 'szcs@gmail.com', 1);

INSERT INTO participant(participant_name, participant_email, event_id)
VALUES ('Csendrői Szabolcs', 'cssz@gmail.com', 1);