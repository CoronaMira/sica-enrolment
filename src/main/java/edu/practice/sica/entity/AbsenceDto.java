package edu.practice.sica.entity;

import edu.practice.sica.entity.enums.EventTypeEnum;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AbsenceDto {
    private LocalDate date;
    private Long personId;
    private EventTypeEnum eventType;
}
