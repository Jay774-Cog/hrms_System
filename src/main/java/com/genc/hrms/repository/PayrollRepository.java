package com.genc.hrms.repository;

import com.genc.hrms.model.Payroll;

import java.util.List;

public interface PayrollRepository {

    Payroll findTopByEmployeeIdOrderByPayrollIdDesc(Long employeeId);

    List<Payroll> findByPayPeriod(String payPeriod);


    boolean existsByEmployee_IdAndPayPeriod(Long employeeId, String payPeriod);


}
