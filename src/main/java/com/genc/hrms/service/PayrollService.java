package com.genc.hrms.service;

import com.genc.hrms.model.Attendance;
import com.genc.hrms.model.Employee;
import com.genc.hrms.model.Payroll;
import com.genc.hrms.repository.AttendanceRepository;
import com.genc.hrms.repository.EmployeeRepository;
import com.genc.hrms.repository.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PayrollService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    public Payroll runPayroll(Payroll payroll, long id) {
        boolean alreadyExists = payrollRepository.existsByEmployee_IdAndPayPeriod(id, payroll.getPayPeriod());

        if (alreadyExists) {
            throw new IllegalStateException("Payroll already generated for this Employee for " + payroll.getPayPeriod());
        }

        double totalDeductions = deductions(payroll, id);
        payroll.setTotalDeductions(totalDeductions);

        Employee employee = employeeRepository.findByEmployeeId(id);
        if (employee == null) {
            throw new IllegalArgumentException("Invalid employee ID: " + id);
        }

        double gross = employee.getSalary();
        payroll.setGrossSalary(gross);
        payroll.setNetSalary(gross - totalDeductions);

        return payrollRepository.save(payroll);
    }

    public double deductions(Payroll payroll, long id) {
        Employee employee = employeeRepository.findByEmployeeId(id);
        if (employee == null) {
            throw new IllegalArgumentException("Invalid employee ID: " + id);
        }

        double gross = employee.getSalary();
        double totalDaysInMonth = 30.0;
        double dailyWage = gross / totalDaysInMonth;

        List<Attendance> acceptedLeaves = attendanceRepository.findByEmployeeIdAndType(id, Attendance.LeaveStatus.APPROVED);

        long unpaidDays = 0;

        for (Attendance leave : acceptedLeaves) {
            if (Attendance.Leave.CASUAL == leave.getLeaveType() || Attendance.Leave.SICK == leave.getLeaveType()) {
                long daysAbsent = ChronoUnit.DAYS.between(leave.getFromDate(), leave.getToDate()) + 1;
                unpaidDays += daysAbsent;
            }
        }

        double leaveDeduction = unpaidDays * dailyWage;

        double pf = gross * 0.12;
        double tds = gross * 0.05;
        double esi = gross * 0.03;

        return pf + tds + esi + leaveDeduction;
    }

    public List<Payroll> getData() {
        return payrollRepository.findAll();
    }

    public Payroll getLatestPayrollByEmployeeId(Long employeeId) {
        return payrollRepository.findTopByEmployeeIdOrderByPayrollIdDesc(employeeId);
    }

    public void savePayroll(Payroll payroll) {
        payrollRepository.save(payroll);

    }
    public List<Payroll> getPayrollsByMonth(String payPeriod) {
        return payrollRepository.findByPayPeriod(payPeriod);
    }
}

