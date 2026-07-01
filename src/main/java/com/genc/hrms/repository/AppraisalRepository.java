package com.genc.hrms.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppraisalRepository extends JpaRepository<com.genc.hrms.model.AppraisalRecord, Long> {
    Optional<com.genc.hrms.model.AppraisalRecord> findByEmployeeIdAndAppraisalCycle(Long employeeId, String appraisalCycle);
}
