CREATE TABLE fingerprints
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    person_id         BIGINT       NULL,
    student_id        INT          NULL,
    fingerprint_data  LONGBLOB     NOT NULL,
    finger            VARCHAR(50)  NOT NULL,
    active            TINYINT(1) DEFAULT 1,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE attendance_records
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    person_id        BIGINT       NOT NULL,
    record_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    record_type      VARCHAR(20)  NOT NULL,
    device_id        VARCHAR(100) NULL,
    gate             VARCHAR(50)  NOT NULL,
    status           VARCHAR(50)  NOT NULL
);
