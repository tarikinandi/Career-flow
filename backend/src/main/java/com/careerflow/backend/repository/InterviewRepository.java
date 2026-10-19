package com.careerflow.backend.repository;

import com.careerflow.backend.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findAllByApplicationId(Long applicationId);

    /** Ownership comes from the parent application. */
    Optional<Interview> findByIdAndApplicationUserId(Long id, Long userId);
}
