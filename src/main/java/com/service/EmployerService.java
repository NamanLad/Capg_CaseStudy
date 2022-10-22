package com.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.EmployerDAO;
import com.dao.InterviewDAO;
import com.dto.NewEmployerDTO;
import com.dto.RatingFeedbackDTO;
import com.enums.InterviewStatus;
import com.enums.JobPostStatus;
import com.exceptions.AllInterviewsNotCompletedException;
import com.exceptions.JobAlreadyClosedWithCandidateSelectedException;
import com.exceptions.NoEmployersException;
import com.exceptions.NoSuchEmployerFoundException;
import com.model.Employer;
import com.model.Interview;
import com.model.Job;

@Service
public class EmployerService {

  @Autowired
  EmployerDAO employerDAO;
  
  @Autowired
  InterviewDAO interviewDAO;
  
  public void addEmployer(NewEmployerDTO dto) {
    Employer employer = new Employer();
    employer.setEmployerName(dto.getEmployerName());
    employer.setLocation(dto.getLocation());
    employerDAO.save(employer);
  }
  
  public List<Employer> findAllEmployers() throws NoEmployersException {
    List<Employer> employerList = employerDAO.findAll();
    if (employerList.size() == 0) throw new NoEmployersException();
    return employerList;
  }
  
  public Employer getEmployerById(int id) throws NoSuchEmployerFoundException {
    try {      
      Employer employer = employerDAO.getById(id);
      return employer;
    } catch (Exception e) {
      throw new NoSuchEmployerFoundException(id);
    }
  }
  
  public void selectCandidateForJobAfterInterview(Interview interview) throws JobAlreadyClosedWithCandidateSelectedException, AllInterviewsNotCompletedException {
    Job j = interview.getJob();
    if (j.getJobPostStatus().equals(JobPostStatus.CLOSED)) {
      throw new JobAlreadyClosedWithCandidateSelectedException();
    }
    List<Interview> pendingInterviewsForJob = interviewDAO.findByJobAndInterviewStatus(j, InterviewStatus.INVALID);
    if (pendingInterviewsForJob.size() != 0) {
      List<String> candidateList = pendingInterviewsForJob.stream().map((i) -> i.getCandidate()).collect(Collectors.toList());
      throw new AllInterviewsNotCompletedException(j.getJobId(), candidateList);
    }

    // set the interview status for this interview as 'selected'
    interview.setInterviewStatus(InterviewStatus.SELECTED);
    interviewDAO.save(interview);
    
    // set the interview status for the remaining interviews for this job as 'rejected'
    List<Interview> waitingCandidatesAfterInterview = interviewDAO.findByJobAndInterviewStatus(j, InterviewStatus.WAITING);
    for (Interview i: waitingCandidatesAfterInterview) {
      i.setInterviewStatus(InterviewStatus.REJECTED);
      interviewDAO.save(i);
    }      
  }
}
