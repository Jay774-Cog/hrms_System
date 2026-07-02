package com.genc.hrms.service;

import com.genc.hrms.dto.LeaveDto;
import com.genc.hrms.model.Attendance;
import com.genc.hrms.model.Employee;
import com.genc.hrms.model.MarkAttendance;
import com.genc.hrms.repository.AttendanceRepository;
import com.genc.hrms.repository.MarkAttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private MarkAttendanceRepository markAttendanceRepository;

    long empId=12;

    // for employee saving leave requests
    public void saveLeaveRequest(Attendance attendance)
    {
        long totalDays = ChronoUnit.DAYS.between(attendance.getFromDate(), attendance.getToDate()) + 1;
        LocalDate fromDate = attendance.getFromDate();
        LocalDate toDate = attendance.getToDate();
        if(toDate.isBefore( fromDate)) {
            throw new IllegalArgumentException("To date cannot be before from date.");

        }
        attendance.setTotalDays(totalDays);
        attendance.setStatus(Attendance.LeaveStatus.APPLIED);
        Employee emp = new Employee();
        emp.setEmployeeId(empId);
        attendance.setEmployee(emp);
        attendanceRepository.save(attendance);

    }

//    for marking attendance

    public void saveAttendance() {
        LocalDate today = LocalDate.now();
        boolean alreadyCheckedIn = markAttendanceRepository.existsByEmployee_EmployeeIdAndPresentDate(empId, today);

        if (!alreadyCheckedIn) {
            MarkAttendance markAttendance = new MarkAttendance();

            Employee emp = new Employee();
            emp.setEmployeeId(empId);
            markAttendance.setEmployee(emp);

            // 2. Safely populate clock-in variables
            markAttendance.setInTime(LocalTime.now());
            markAttendance.setPresentDate(today);
            markAttendance.setAttendanceStatus(MarkAttendance.AttendanceStatus.PENDING);

            markAttendanceRepository.save(markAttendance);
        }
    }


    public void saveTimeSheet() {
        LocalDate today = LocalDate.now();
        MarkAttendance existingAttendance = markAttendanceRepository.findByEmployee_EmployeeIdAndPresentDate(empId, today);

        if (existingAttendance == null) {
            throw new IllegalStateException("You cannot clock out because you haven't checked in today!");
        }
        LocalTime alreadyCheckedOut = existingAttendance.getOutTime();
        if(alreadyCheckedOut == null) {

            existingAttendance.setOutTime(LocalTime.now());
            long totalHours = ChronoUnit.HOURS.between(existingAttendance.getInTime(), existingAttendance.getOutTime());
            existingAttendance.setTotalHours(totalHours);
            if (totalHours >= 9) {
                existingAttendance.setAttendanceStatus(MarkAttendance.AttendanceStatus.PRESENT);
            } else {
                existingAttendance.setAttendanceStatus(MarkAttendance.AttendanceStatus.ABSENT);
            }

            markAttendanceRepository.save(existingAttendance);
        }
        else {
            throw new IllegalStateException("You have already clocked out today!");
        }
    }

    // Helper method to supply the UI tracking card with today's live active session info
    public MarkAttendance getTodayAttendance() {
        LocalDate today = LocalDate.now();
        return markAttendanceRepository.findByEmployee_EmployeeIdAndPresentDate(empId, today);
    }

    public LeaveDto leaves() {// Or extract from your secure session context

        int casual = attendanceRepository.findByLeaveTypeAndStatusAndEmployee_EmployeeId(
                Attendance.Leave.CASUAL, Attendance.LeaveStatus.APPROVED, empId).size();

        int sick = attendanceRepository.findByLeaveTypeAndStatusAndEmployee_EmployeeId(
                Attendance.Leave.SICK, Attendance.LeaveStatus.APPROVED, empId).size();

        int earned = attendanceRepository.findByLeaveTypeAndStatusAndEmployee_EmployeeId(
                Attendance.Leave.EARNED, Attendance.LeaveStatus.APPROVED, empId).size();

        return new LeaveDto(sick, casual, earned);
    }

    public List<Attendance> getLeaveHistory(){
        return attendanceRepository.findByEmployee_EmployeeId(empId);
    }

    public List<MarkAttendance> getTimeSheetHistory(){
        return markAttendanceRepository.findByEmployee_EmployeeId(empId);
    }

}
