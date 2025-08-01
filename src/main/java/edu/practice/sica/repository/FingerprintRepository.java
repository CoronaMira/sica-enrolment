package edu.practice.sica.repository;

import edu.practice.sica.entity.Fingerprint;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
@Log4j2
public class FingerprintRepository {

    private final JdbcTemplate jdbcTemplate;

    public FingerprintRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Fingerprint save(Fingerprint fingerprint) {
        String sql = "INSERT INTO fingerprints (fingerprint_data, student_id, registration_date, finger) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            if (fingerprint.getFingerprintData() != null) {
                ps.setBytes(1, fingerprint.getFingerprintData());
            } else {
                log.error("Fingerprint template is null");
                throw new IllegalArgumentException("Fingerprint template cannot be null.");
            }

            // Verificación para el ID del estudiante
            if (fingerprint.getStudentId() != null) {
                ps.setInt(2, fingerprint.getStudentId());
            } else {
                log.error("Student ID is null");
                throw new IllegalArgumentException("Student ID cannot be null.");
            }

            // Verificación para la fecha de registro
            if (fingerprint.getRegistrationDate() != null) {
                ps.setTimestamp(3, Timestamp.valueOf(fingerprint.getRegistrationDate()));
            } else {
                log.error("Registration Date is null");
                ps.setNull(3, Types.TIMESTAMP);
            }

            if (fingerprint.getFinger() != null) {
                ps.setString(4, fingerprint.getFinger().toUpperCase());
            } else {
                log.error("Finger is null");
                ps.setString(4, "thumb" );
            }

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            fingerprint.setId(keyHolder.getKey().intValue());
        }
        return fingerprint;
    }

    public List<Fingerprint> findByStudentId(Integer studentId) {
        String sql = "SELECT * FROM fingerprints WHERE student_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Fingerprint.class), studentId);
    }

    public Optional<Fingerprint> findById(Integer id) {
        String sql = "SELECT * FROM fingerprints WHERE id = ?";
        List<Fingerprint> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Fingerprint.class), id);
        return result.stream().findFirst();
    }

    public List<Fingerprint> findAll() {
        String sql = "SELECT * FROM fingerprints";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Fingerprint.class));
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM fingerprints WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}