package com.genc.hrms.service;

import com.genc.hrms.model.JobRequisition;
import com.genc.hrms.repository.JobRequisitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobRequisitionService {

    private static final Logger logger = LoggerFactory.getLogger(JobRequisitionService.class);

    @Autowired
    private JobRequisitionRepository jobRequisitionRepository;

    public JobRequisition createJobRequisition(JobRequisition jobRequisition) {
        logger.info("Creating job requisition: {}", jobRequisition.getJobTitle());
        JobRequisition saved = jobRequisitionRepository.save(jobRequisition);
        logger.debug("Job requisition created with this ID: {}", saved.getRequisitionId());
        return saved;
    }

    public List<JobRequisition> getAllJobRequisitions() {
        logger.debug("Retrieving all job requisitions");
        return jobRequisitionRepository.findAll();
    }

    public JobRequisition getJobRequisitionById(Integer id) {
        logger.debug("Retrieving job requisition by ID: {}", id);
        return jobRequisitionRepository.findById(id).orElse(null);
    }

    public JobRequisition updateJobRequisition(JobRequisition jobRequisition) {
        logger.info("Updating job requisition with ID: {}", jobRequisition.getRequisitionId());
        return jobRequisitionRepository.save(jobRequisition);
    }

    public void deleteJobRequisition(Integer id) {
        logger.info("Deleting job requisition with ID: {}", id);
        jobRequisitionRepository.deleteById(id);
    }

    public long getRequisitionCount() {
        logger.debug("Getting requisition count");
        return jobRequisitionRepository.count();
    }
}

