package edu.practice.sica.service;

import edu.practice.sica.entity.AttendanceRecords;
import edu.practice.sica.repository.AttendanceRecordsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceRecordsService {
    private final AttendanceRecordsRepository attendanceRecordRepository;

    public AttendanceRecordsService(AttendanceRecordsRepository attendanceRecordRepository) {
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    public List<AttendanceRecords> findAll() {
        return attendanceRecordRepository.findAll();
    }

    public Optional<AttendanceRecords> findById(Long id) {
        return attendanceRecordRepository.findById(id);
    }

    public AttendanceRecords create(AttendanceRecords record) {

        return (record.getRecordTimestamp() != null) ?  attendanceRecordRepository.justifAbsence(record) : attendanceRecordRepository.save(record);
    }

    public AttendanceRecords update(Long id, AttendanceRecords updatedRecord) {
        if (!attendanceRecordRepository.existsById(id)) {
            throw new RuntimeException("Attendance record not found with id: " + id);
        }
        updatedRecord.setId(id);
        attendanceRecordRepository.update(updatedRecord);
        return updatedRecord;
    }

    public void deleteById(Long id) {
        if (!attendanceRecordRepository.existsById(id)) {
            throw new RuntimeException("Attendance record not found with id: " + id);
        }
        attendanceRecordRepository.deleteById(id);
    }

}
