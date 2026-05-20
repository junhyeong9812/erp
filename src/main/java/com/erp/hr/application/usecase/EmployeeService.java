package com.erp.hr.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.hr.application.dto.command.HireEmployeeCommand;
import com.erp.hr.application.port.inbound.EmployeeUseCase;
import com.erp.hr.application.port.outbound.EmployeeRepository;
import com.erp.hr.domain.entity.Employee;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeService implements EmployeeUseCase {

    private final EmployeeRepository repo;
    private final EventBus eventBus;

    public EmployeeService(EmployeeRepository repo, EventBus eventBus) {
        this.repo = repo;
        this.eventBus = eventBus;
    }

    @Override
    public Long hire(HireEmployeeCommand cmd) {
        Employee e = Employee.hire(cmd.employeeNumber(), cmd.name(), cmd.departmentId(),
                cmd.hiredAt(), Money.of(cmd.baseSalary()));
        e.assignId(IdGenerator.next());
        repo.save(e);
        eventBus.publishAll(e.pullEvents());
        return e.getId();
    }

    @Override
    public void transfer(Long employeeId, Long newDepartmentId) {
        Employee e = repo.findById(employeeId).orElseThrow();
        e.transferTo(newDepartmentId);
        repo.save(e);
    }
}