package com.zenith.backend.repository;

import com.zenith.backend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<Conversation> findAllByOrderByCreatedAtDesc();
    Optional<Conversation> findFirstByOrderByCreatedAtDesc();
}
