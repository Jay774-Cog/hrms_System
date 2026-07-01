package com.genc.hrms.repository;

import com.genc.hrms.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    Payroll findTopByEmployeeIdOrderByPayrollIdDesc(Long employeeId);

    List<Payroll> findByPayPeriod(String payPeriod);


    // Change to use the correct field name
    boolean existsByEmployeeEmployeeIdAndPayPeriod(Long employeeId, String payPeriod);

}
