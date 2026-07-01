package com.genc.hrms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "AppraisalRecord")
@Getter
@Setter
public class AppraisalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appraisalId;

    @Column(name = "employeeId", nullable = false)
    private Long employeeId;

    @Column(name = "appraisalCycle", length = 20)
    private String appraisalCycle;

    @Column(name = "goalsAchieved")
    private Integer goalsAchieved;

    @Column(name = "overallRating", precision = 3, scale = 1)
    private BigDecimal overallRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "appraisalStatus")
    private AppraisalStatus appraisalStatus;

    public enum AppraisalStatus {
        DRAFT, SELF_REVIEW, MANAGER_REVIEW, PUBLISHED
    }

    public Long getAppraisalId() {
        return appraisalId;
    }

    public void setAppraisalId(Long appraisalId) {
        this.appraisalId = appraisalId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getAppraisalCycle() {
        return appraisalCycle;
    }

    public void setAppraisalCycle(String appraisalCycle) {
        this.appraisalCycle = appraisalCycle;
    }

    public Integer getGoalsAchieved() {
        return goalsAchieved;
    }

    public void setGoalsAchieved(Integer goalsAchieved) {
        this.goalsAchieved = goalsAchieved;
    }

    public BigDecimal getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(BigDecimal overallRating) {
        this.overallRating = overallRating;
    }

    public AppraisalStatus getAppraisalStatus() {
        return appraisalStatus;
    }

    public void setAppraisalStatus(AppraisalStatus appraisalStatus) {
        this.appraisalStatus = appraisalStatus;
    }
}