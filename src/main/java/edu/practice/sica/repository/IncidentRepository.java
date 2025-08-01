package edu.practice.sica.repository;

import edu.practice.sica.entity.AbsenceDto;
import edu.practice.sica.entity.TardinessDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class IncidentRepository {
    private final JdbcTemplate jdbcTemplate;

    public IncidentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AbsenceDto> findAbsencesByPersonAndDateRange(Long personId, LocalDate startDate, LocalDate endDate) {
        String sql = """
            WITH RECURSIVE dates (date_col) AS (
              -- 1. Genera una serie de fechas desde startDate hasta endDate.
              SELECT ? -- Parámetro para startDate
              UNION ALL
              SELECT date_col + INTERVAL 1 DAY FROM dates WHERE date_col < ? -- Parámetro para endDate
            )
            SELECT
              d.date_col AS date,
              ? AS person_id, -- Parámetro para personId
              -- 2. Evalúa si existe un registro de asistencia para el día.
              --    Si el conteo de registros es mayor a 0, se marca como ATTENDANCE.
              --    De lo contrario, se marca como ABSENCE.
              CASE WHEN COUNT(ar.id) > 0 THEN 'ATTENDANCE' ELSE 'ABSENCE' END AS event_type
            FROM
              dates d
            LEFT JOIN
              -- 3. Se une con la tabla de registros de asistencia por fecha y persona.
              attendance_records ar ON DATE(ar.record_timestamp) = d.date_col AND ar.person_id = ? -- Parámetro para personId
            -- 4. Se agrupa por fecha para asegurar un único resultado por día.
            GROUP BY
              d.date_col
            ORDER BY
              d.date_col;
            """;

        // Los parámetros se pasan en el orden en que aparecen los '?' en la consulta.
        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(AbsenceDto.class),
                startDate,
                endDate,
                personId,
                personId
        );
    }

    public List<TardinessDto> findTardinessByPersonAndDateRange(Long personId, LocalDate startDate, LocalDate endDate) {
        // The SQL query now joins with the 'person' table and adds a 10-minute interval.
        String sql = """
            SELECT
                ar.person_id,
                MIN(ar.record_timestamp) AS arrival_time
            FROM
                attendance_records ar
            JOIN
                person p ON ar.person_id = p.id
            WHERE
                ar.person_id = ?
                AND ar.record_type = 'ENTRY' -- Assuming 'ENTRY' is the check-in type
                AND ar.record_timestamp >= ?  -- Start of the date range
                AND ar.record_timestamp < ?   -- End of the date range
            GROUP BY
                ar.person_id, DATE(ar.record_timestamp), p.entry_time
            HAVING
                TIMEDIFF(TIME(MIN(ar.record_timestamp)), p.entry_time) > '00:10:00'
            ORDER BY
                arrival_time;
            """;

        LocalDateTime startRange = startDate.atStartOfDay();
        // The end of the range is the start of the next day to include the full end date.
        LocalDateTime endRange = endDate.plusDays(1).atStartOfDay();

        // The lateThresholdTime parameter is no longer needed.
        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(TardinessDto.class),
                personId,
                startRange,
                endRange
        );
    }
}
