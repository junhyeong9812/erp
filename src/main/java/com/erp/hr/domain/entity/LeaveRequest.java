package com.erp.hr.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "hr_leave_request")
public class LeaveRequest extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected LeaveRequest() {}

    public static LeaveRequest request(Long employeeId, LocalDate start, LocalDate end, String reason) {
        LeaveRequest r = new LeaveRequest();
        r.employeeId = employeeId; r.startDate = start; r.endDate = end;
        r.reason = reason; r.status = Status.PENDING;
        return r;
    }

    public void approve() { this.status = Status.APPROVED; }
    public void reject()  { this.status = Status.REJECTED; }

    public int days() { return (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1); }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public Status getStatus() { return status; }

    public enum Status { PENDING, APPROVED, REJECTED }
}