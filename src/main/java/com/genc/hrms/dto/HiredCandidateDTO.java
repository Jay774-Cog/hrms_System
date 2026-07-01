package com.genc.hrms.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO to transfer hired candidate data from Recruitment module to Employee module.
 * This is sent when a candidate's offer status becomes ACCEPTED.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HiredCandidateDTO {

    // Candidate Information
    private Integer candidateId;
    private String fullName;
    private String appliedRole;
    private Integer experienceYears;

    // Offer Information
    private Integer offerId;
    private String positionOffered;
    private String department;
    private BigDecimal salaryOffered;
    private LocalDate offerDate;
    private LocalDate joiningDate;
    private String additionalBenefits;
    private String remarks;
}


