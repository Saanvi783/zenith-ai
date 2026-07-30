package com.zenith.backend.repository;

import com.zenith.backend.model.PlacementNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlacementNoteRepository extends JpaRepository<PlacementNote, Long> {

    List<PlacementNote> findByCategory(String category);

    @Query("SELECT p FROM PlacementNote p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PlacementNote> searchByKeyword(@Param("keyword") String keyword);
}
