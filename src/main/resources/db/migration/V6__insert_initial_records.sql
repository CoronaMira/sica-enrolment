INSERT INTO escuela.person (id, first_name, last_name, middle_name, type, enrollment, degree, shift, entry_time, departure_time) VALUES (1, 'Hector', 'Corona', 'Osorio', 'student', '234536664', 'Desarrollo de Software', 'Matutino','09:00:00','18:00:00');
INSERT INTO escuela.person (id, first_name, last_name, middle_name, type, enrollment, degree, shift, entry_time, departure_time) VALUES (3, 'Carlos', 'Martinez', 'Baez', 'student', '2322455', 'Desarrollo de Software', 'Vespertino','15:00:00','21:00:00');
INSERT INTO escuela.person (id, first_name, last_name, middle_name, type, enrollment, degree, shift, entry_time, departure_time) VALUES (4, 'Nayeli', 'Martinez', 'Diaz', 'student', '2453677809', 'Desarrollo de Software', 'Vespertino','15:00:00','21:00:00');


INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (4, '2025-07-21 09:02:15', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');

INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (4, '2025-07-22 08:58:41', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');

INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (4, '2025-07-23 09:05:00', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');

INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (4, '2025-07-24 08:55:10', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');

INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (4, '2025-07-25 09:01:33', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');

INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (4, '2025-07-30 09:03:48', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');

INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (4, '2025-07-31 08:59:02', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');



INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (1, '2025-07-21 09:02:15', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');

INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (1, '2025-07-22 08:58:41', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');

INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (1, '2025-07-23 09:05:00', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');

INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (1, '2025-07-24 08:55:10', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');

INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (3, '2025-07-25 09:01:33', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');

INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (3, '2025-07-30 09:03:48', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');

INSERT INTO attendance_records (person_id, record_timestamp, record_type, device_id, gate, status)
VALUES (3, '2025-07-31 08:59:02', 'ENTRY', 'DEV001', 'MAIN_GATE', 'SUCCESS');
