package com.zenith.backend.repository;

import com.zenith.backend.model.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
    List<InterviewSession> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    Optional<InterviewSession> findFirstByStudentIdAndActiveTrueOrderByCreatedAtDesc(Long studentId);
    Optional<InterviewSession> findFirstByActiveTrueOrderByCreatedAtDesc();
}
