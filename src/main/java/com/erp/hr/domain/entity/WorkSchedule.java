package com.erp.hr.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalTime;

/** 직원의 소정 근무 기준. 09:00 하드코딩을 대체한다. */
@Entity
@Table(name = "hr_work_schedule")
public class WorkSchedule extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private LocalTime scheduledStart;   // 소정 시업 (예: 09:00, 교대조면 22:00)
    private LocalTime scheduledEnd;     // 소정 종업
    private int dailyContractMinutes;   // 1일 소정근로 (예: 480 = 8h)

    protected WorkSchedule() {}

    public static WorkSchedule of(Long employeeId, LocalTime start, LocalTime end, int dailyMinutes) {
        WorkSchedule s = new WorkSchedule();
        s.employeeId = employeeId; s.scheduledStart = start;
        s.scheduledEnd = end; s.dailyContractMinutes = dailyMinutes;
        return s;
    }

    public boolean isLate(java.time.LocalDateTime actualCheckIn) {
        return actualCheckIn.toLocalTime().isAfter(scheduledStart);
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public int getDailyContractMinutes() { return dailyContractMinutes; }
}