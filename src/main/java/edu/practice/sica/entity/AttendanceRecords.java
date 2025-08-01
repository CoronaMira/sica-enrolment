package edu.practice.sica.entity;

import edu.practice.sica.entity.enums.RecordType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Entity
@Table(name = "AttendanceRecord")
@Data
public class AttendanceRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Column(name = "record_timestamp", nullable = false, updatable = false, insertable = false)
    private LocalDateTime recordTimestamp;

    @Column(name = "record_type", nullable = false, length = 20)
    private RecordType recordType;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "gate", nullable = false, length = 20)
    private String gate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

}
