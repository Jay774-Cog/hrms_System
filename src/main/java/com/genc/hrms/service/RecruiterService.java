package com.genc.hrms.service;

import com.genc.hrms.model.Recruiter;
import com.genc.hrms.repository.RecruiterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecruiterService {

    private static final Logger logger = LoggerFactory.getLogger(RecruiterService.class);

    @Autowired
    private RecruiterRepository candidateRepository;

    public Recruiter saveData(Recruiter candidate){
        logger.info("Saving candidate data: {}", candidate.getFullName());
        Recruiter saved = candidateRepository.save(candidate);
        logger.debug("Candidate saved with ID: {}", saved.getCandidateId());
        return saved;
    }

    public List<Recruiter> getAllCandidates() {
        logger.debug("Retrieving all candidates");
        return candidateRepository.findAll();
    }

    public Recruiter getCandidateById(Integer id) {
        logger.debug("Retrieving candidate by ID: {}", id);
        return candidateRepository.findById(id).orElse(null);
    }

    public Recruiter updateCandidate(Recruiter candidate) {
        logger.info("Updating candidate with ID: {}", candidate.getCandidateId());
        return candidateRepository.save(candidate);
    }

    public void deleteCandidate(Integer id) {
        logger.info("Deleting candidate with ID: {}", id);
        candidateRepository.deleteById(id);
    }

    public long getCandidateCount() {
        logger.debug("Getting candidate count");
        return candidateRepository.count();
    }
}

