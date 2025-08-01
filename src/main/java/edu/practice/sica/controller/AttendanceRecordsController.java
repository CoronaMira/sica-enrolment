package edu.practice.sica.controller;

import edu.practice.sica.entity.AttendanceRecords;
import edu.practice.sica.service.AttendanceRecordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/attendance-records")
@RequiredArgsConstructor
public class AttendanceRecordsController {
    private final AttendanceRecordsService attendanceRecordService;


    @PostMapping
    public ResponseEntity<AttendanceRecords> create(@RequestBody AttendanceRecords record) {
        AttendanceRecords createdRecord = attendanceRecordService.create(record);
        return ResponseEntity.created(URI.create("/api/attendance-records/" + ((AttendanceRecords) createdRecord).getId()))
                .body(createdRecord);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceRecords> update(@PathVariable Long id, @RequestBody AttendanceRecords record) {
        try {
            AttendanceRecords updatedRecord = attendanceRecordService.update(id, record);
            return ResponseEntity.ok(updatedRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceRecords> getById(@PathVariable Long id) {
        return attendanceRecordService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<List<AttendanceRecords>> getAll() {
        List<AttendanceRecords> records = attendanceRecordService.findAll();
        return ResponseEntity.ok(records);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            attendanceRecordService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
