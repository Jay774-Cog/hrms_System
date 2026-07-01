package com.genc.hrms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity
public class Payroll {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="payroll_id")
        private int payrollId;


        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "employee_id", nullable = false)
        @NotNull(message = "Employee cannot be null")
        private Employee employee;

        @NotBlank(message = "Pay period cannot be empty")
        @Size(max = 20, message = "Pay period must not exceed 20 characters")
        @Column(name="pay_period")
        private String payPeriod;

        @NotNull(message = "Gross salary is required")
        @PositiveOrZero(message = "Gross salary cannot be negative")
        @Column(name="gross_salary")
//    private double grossSalary;

        private  Double grossSalary;

        @PositiveOrZero(message = "Total deductions cannot be negative")
        @Column(name="total_deductions")
//    private double totalDeductions;
        private Double totalDeductions;
        @PositiveOrZero(message = "Net salary cannot be negative")
        @Column(name="net_salary")
//    private double netSalary;
        private Double netSalary;

        @NotNull(message = "Status cannot be empty")
        @Enumerated(EnumType.STRING)
        private PayrollStatus payrollStatus;

        public enum PayrollStatus {
            DRAFT, PROCESSED, PAID, ON_HOLD
        }



        public Payroll() {}


        public int getPayrollId() { return payrollId; }
        public void setPayrollId(int payrollId) { this.payrollId = payrollId; }

        public Employee getEmployee() { return employee; }
        public void setEmployee(Employee employee) { this.employee = employee; }

        public String getPayPeriod() { return payPeriod; }
        public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }

//    public double getGrossSalary() { return grossSalary; }
//    public void setGrossSalary(double grossSalary) { this.grossSalary = grossSalary; }
//
//    public double getTotalDeductions() { return totalDeductions; }
//    public void setTotalDeductions(double totalDeductions) { this.totalDeductions = totalDeductions; }
//
//    public double getNetSalary() { return netSalary; }
//    public void setNetSalary(double netSalary) { this.netSalary = netSalary; }

        public Double getGrossSalary() {
            return grossSalary;
        }

        public void setGrossSalary( Double grossSalary) {
            this.grossSalary = grossSalary;
        }

        public @PositiveOrZero(message = "Total deductions cannot be negative") Double getTotalDeductions() {
            return totalDeductions;
        }

        public void setTotalDeductions(@PositiveOrZero(message = "Total deductions cannot be negative") Double totalDeductions) {
            this.totalDeductions = totalDeductions;
        }

        public @PositiveOrZero(message = "Net salary cannot be negative") Double getNetSalary() {
            return netSalary;
        }

        public void setNetSalary(@PositiveOrZero(message = "Net salary cannot be negative") Double netSalary) {
            this.netSalary = netSalary;
        }

    public  PayrollStatus getPayrollStatus() {
        return payrollStatus;
    }

    public void setPayrollStatus(PayrollStatus payrollStatus) {
        this.payrollStatus = payrollStatus;
    }
}
