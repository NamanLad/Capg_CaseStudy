package com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.InterviewDAO;
import com.dto.RatingFeedbackDTO;
import com.exceptions.NoSuchInterviewFoundException;
import com.model.Interview;

@Service
public class InterviewService {

  @Autowired
  InterviewDAO interviewDAO;
  
  public Interview getInterviewById(int id) throws NoSuchInterviewFoundException {
    try {
      Interview i = interviewDAO.getById(id);
      return i;
    } catch (Exception e) {
      throw new NoSuchInterviewFoundException(id);
    }
  }
  
  public void provideEmployerFeedback(Interview interview, RatingFeedbackDTO dto) {
    interview.setEmployerRating(dto.getRating());
    interview.setEmployerFeedback(dto.getFeedback());
    interviewDAO.save(interview);
  }
  
  
  public void provideCandidateFeedback(Interview interview, RatingFeedbackDTO dto) {
    interview.setCandidateRating(dto.getRating());
    interview.setCandidateFeedback(dto.getFeedback());
    interviewDAO.save(interview);
  }
  
  public List<Interview> getAllInterviews() {
    return interviewDAO.findAll();
  }
  
}
