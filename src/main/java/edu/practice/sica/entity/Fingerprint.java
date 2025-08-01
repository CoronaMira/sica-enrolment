package edu.practice.sica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "catalogos")
@Data
public class Fingerprint{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "fingerprint_data")
    private byte[] fingerprintData;
    @Column(name = "finger")
    private String finger;
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
    @Column(name = "student_id")
    private Integer studentId;
}
