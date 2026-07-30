package com.zenith.backend.repository;

import com.zenith.backend.model.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    List<InterviewQuestion> findByInterviewSessionIdOrderByQuestionOrderAsc(Long interviewSessionId);
}
