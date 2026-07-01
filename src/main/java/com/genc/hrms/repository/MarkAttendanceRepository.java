package com.genc.hrms.repository;

import com.genc.hrms.model.MarkAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MarkAttendanceRepository extends JpaRepository<MarkAttendance,Long> {

    boolean existsByEmployee_EmployeeIdAndPresentDate(long employeeId, LocalDate date);


    MarkAttendance findByEmployee_EmployeeIdAndPresentDate(long employeeId, LocalDate date);

    List<MarkAttendance> findByEmployee_EmployeeId(long employeeId);
}
