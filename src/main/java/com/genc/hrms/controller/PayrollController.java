package com.genc.hrms.controller;

import com.genc.hrms.model.Employee;
import com.genc.hrms.model.Payroll;
import com.genc.hrms.repository.EmployeeRepository;
import com.genc.hrms.repository.PayrollRepository;
import com.genc.hrms.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@CrossOrigin("*")
@RequestMapping("/api/payroll")
public class PayrollController {

        @Autowired
        private PayrollService payrollService;

        @Autowired
        private EmployeeRepository employeeRepository;

        @Autowired
        private PayrollRepository payrollRepository;

        // 1. Provide data needed to populate the frontend form (Dropdowns, etc.)
        @GetMapping("/form-data")
        public ResponseEntity<Map<String, Object>> getFormData() {
            Map<String, Object> response = new HashMap<>();
            response.put("statuses", Payroll.PayrollStatus.values());
            response.put("employees", employeeRepository.findAll());
            return ResponseEntity.ok(response);
        }

        // 2. Run Payroll (Expects JSON in the request body)
        @PostMapping("/run")
        public ResponseEntity<?> runPayroll(@RequestBody Payroll payroll) {
            try {
                // Delegate all heavy lifting to the optimized service method
                payrollService.processAndSavePayroll(payroll);
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Payroll executed successfully"));
            } catch (IllegalStateException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
            } catch (Exception e) {
                // Tip: Add a logger here in production (e.g., log.error("Payroll failed", e);)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
            }
        }

        // 3. Compute Deductions and return the math
        @PostMapping("/deductions")
        // REMOVED @Valid here because it's just a preview, we don't need strict DB rules like "Status"
        public ResponseEntity<Map<String, Double>> computeStatutoryDeductions(@RequestBody Payroll payroll) {

            // Fetch the real employee from the DB
            Employee realEmployee = employeeRepository.findById(payroll.getEmployee().getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            // Do the math using the REAL database salary
            double deductions = payrollService.deductions(payroll, realEmployee.getEmployeeId());
            double grossSalary = realEmployee.getSalary();
            double netSalary = grossSalary - deductions;

            Map<String, Double> response = new HashMap<>();
            response.put("deduct", deductions);
            response.put("grossSalary", grossSalary);
            response.put("netSalary", netSalary);

            return ResponseEntity.ok(response);
        }

        // 4. Get Payrolls list
        @GetMapping("/payrolls")
        public ResponseEntity<List<Payroll>> getPayrolls(@RequestParam(value = "month", required = false) String month) {
            List<Payroll> payrolls;
            if (month != null && !month.isEmpty()) {
                payrolls = payrollService.getPayrollsByMonth(month);
            } else {
                payrolls = payrollService.getData();
            }
            return ResponseEntity.ok(payrolls);
        }

        // 5. Get a specific payslip
        @GetMapping("/payslip")
        public ResponseEntity<Payroll> generatePayslip(@RequestParam("employeeId") Long employeeId) {
            Payroll payroll = payrollService.getLatestPayrollByEmployeeId(employeeId);
            if (payroll != null && payroll.getPayrollStatus() == Payroll.PayrollStatus.PAID) {
                return ResponseEntity.ok(payroll);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 6. Update status to PAID (Using PUT or PATCH is more RESTful than GET for state changes)
        @PutMapping("/payslip/markAsPaid/{employeeId}")
        public ResponseEntity<?> markAsPaid(@PathVariable("employeeId") Long employeeId) {
            Payroll payroll = payrollService.getLatestPayrollByEmployeeId(employeeId);
            if (payroll != null) {
                payroll.setPayrollStatus(Payroll.PayrollStatus.PAID);
                payrollService.savePayroll(payroll);
                return ResponseEntity.ok(Map.of("message", "Status updated to PAID"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Payroll not found"));
        }
        // 7. Get Dashboard Data
        @GetMapping("/dashboard")
        public ResponseEntity<Map<String, Object>> getDashboard(@RequestParam(value = "month", required = false) String month) {

            List<Payroll> payrolls;

            // 1. If month is null, empty, or "ALL", fetch EVERYTHING from the database
            if (month == null || month.trim().isEmpty() || "ALL".equalsIgnoreCase(month)) {
                payrolls = payrollRepository.findAll();
            }
            // 2. Otherwise, fetch only the selected month
            else {
                payrolls = payrollRepository.findByPayPeriod(month);
            }

            double totalGross = 0;
            double totalNetPay = 0;
            int paidCount = 0;
            int pendingCount = 0;

            for (Payroll p : payrolls) {
                // Handle potential nulls safely
                if (p.getGrossSalary() != null) totalGross += p.getGrossSalary();
                if (p.getNetSalary() != null) totalNetPay += p.getNetSalary();

                if (p.getPayrollStatus() != null && "PAID".equalsIgnoreCase(p.getPayrollStatus().name())) {
                    paidCount++;
                } else {
                    pendingCount++;
                }
            }
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("selectedMonth", month == null || month.isEmpty() ? "ALL" : month);
            dashboardData.put("totalGross", totalGross);
            dashboardData.put("totalNetPay", totalNetPay);
            dashboardData.put("paidCount", paidCount);
            dashboardData.put("pendingCount", pendingCount);
            dashboardData.put("payrolls", payrolls);

            return ResponseEntity.ok(dashboardData);
        }
    }


