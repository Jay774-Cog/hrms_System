package com.genc.hrms.repository;

import com.genc.hrms.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance,Long> {

    Attendance save(Attendance attendance);

    //    employee
    List<Attendance> findByEmployee_EmployeeId(long id);

    //  for manager
    List<Attendance> findByStatus(Attendance.LeaveStatus status);

    List<Attendance> findByLeaveTypeAndStatusAndEmployee_EmployeeId(Attendance.Leave leaveType, Attendance.LeaveStatus status, long id);


    List<Attendance> findByEmployeeIdAndType(long employeeId, Attendance.LeaveStatus type);


}

