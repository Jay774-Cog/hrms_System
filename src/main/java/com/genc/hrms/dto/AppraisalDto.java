package com.hrms.performance.dto;

import java.math.BigDecimal;
import lombok.Data; // Ensure you have Lombok dependency, or manually generate Getters and Setters

@Data
public class AppraisalDto {
    private Long employeeId;
    private String appraisalCycle;
    private Integer goalsAchieved;
    private BigDecimal overallRating;
}