package com.erp.hr.domain.entity;

import com.erp.common.domain.BaseEntity;
import com.erp.hr.domain.vo.WorkPeriod;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hr_attendance")
public class Attendance extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    // LocalDate workDate + LocalTime → LocalDateTime 으로 승격.
    // 자정 넘김은 checkOut 의 날짜가 checkIn 보다 큰 것으로 자연 표현된다.
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;  // 퇴근 전 nullable

    protected Attendance() {}

    public static Attendance checkIn(Long employeeId, LocalDateTime at) {
        Attendance a = new Attendance();
        a.employeeId = employeeId;
        a.checkIn = at;
        return a;
    }

    public void checkOut(LocalDateTime at) {
        if (at.isBefore(checkIn)) throw new IllegalArgumentException("퇴근이 출근보다 빠를 수 없음");
        this.checkOut = at;
    }

    /** 근로시간 계산용 VO 로 변환. 영속되지 않는 파생 객체. checkOut 전이면 빈 값. */
    public java.util.Optional<WorkPeriod> toWorkPeriod() {
        return checkOut == null ? java.util.Optional.empty()
                : java.util.Optional.of(new WorkPeriod(checkIn, checkOut));
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public LocalDateTime getCheckIn() { return checkIn; }
    public LocalDateTime getCheckOut() { return checkOut; }
}