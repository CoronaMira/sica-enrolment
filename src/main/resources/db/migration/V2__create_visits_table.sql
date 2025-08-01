-- Definición de la tabla para registrar las visitas a la universidad
CREATE TABLE visits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    visitor_name VARCHAR(255) NOT NULL,
    visit_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    person_visited VARCHAR(255) NOT NULL,
    authorized_by VARCHAR(255),
    visitor_photo_url TEXT NULL,
    qr_folio VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
