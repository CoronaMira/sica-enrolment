package edu.practice.sica.repository;

import edu.practice.sica.entity.AttendanceRecords;
import edu.practice.sica.entity.enums.RecordType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class AttendanceRecordsRepository {
    private final JdbcTemplate jdbcTemplate;

    public AttendanceRecordsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AttendanceRecords> rowMapper = (rs, rowNum) -> {
        AttendanceRecords record = new AttendanceRecords();
        record.setId(rs.getLong("id"));
        record.setPersonId(rs.getLong("person_id"));
        record.setRecordTimestamp(rs.getTimestamp("record_timestamp").toLocalDateTime());
        record.setRecordType(RecordType.valueOf(rs.getString("record_type")));
        record.setDeviceId(rs.getString("device_id"));
        record.setGate(rs.getString("gate"));
        record.setStatus(rs.getString("status"));
        return record;
    };


    public List<AttendanceRecords> findAll() {
        String sql = "SELECT id, person_id, record_timestamp, record_type, device_id, gate, status FROM attendance_records";
        return jdbcTemplate.query(sql, rowMapper);
    }


    public Optional<AttendanceRecords> findById(Long id) {
        String sql = "SELECT id, person_id, record_timestamp, record_type, device_id FROM attendance_records WHERE id = ?";
        try {
            AttendanceRecords record = jdbcTemplate.queryForObject(sql, new Object[]{id}, rowMapper);
            return Optional.ofNullable(record);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    public AttendanceRecords save(AttendanceRecords record) {
        String sql = "INSERT INTO attendance_records (person_id, record_type, device_id, gate, status) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, record.getPersonId());
            ps.setString(2, record.getRecordType().name());
            ps.setString(3, record.getDeviceId());
            ps.setString(4, record.getGate());
            ps.setString(5, record.getStatus());

            return ps;
        }, keyHolder);

        long newId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        record.setId(newId);
        return record;
    }
    public AttendanceRecords justifAbsence(AttendanceRecords record) {
        String sql = "INSERT INTO attendance_records (person_id, record_type, device_id, gate, status, record_timestamp ) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, record.getPersonId());
            ps.setString(2, record.getRecordType().name());
            ps.setString(3, record.getDeviceId());
            ps.setString(4, record.getGate());
            ps.setString(5, record.getStatus());
            ps.setString(6, record.getRecordTimestamp().toString());

            return ps;
        }, keyHolder);

        long newId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        record.setId(newId);
        return record;
    }


    public int update(AttendanceRecords record) {
        String sql = "UPDATE attendance_records SET person_id = ?, record_type = ?, device_id = ? WHERE id = ?";
        return jdbcTemplate.update(sql, record.getPersonId(), record.getRecordType(), record.getDeviceId(), record.getId());
    }


    public int deleteById(Long id) {
        String sql = "DELETE FROM attendance_records WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }


    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM attendance_records WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class);
        return count != null && count > 0;
    }



}
