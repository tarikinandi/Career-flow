package com.careerflow.backend.repository;

import com.careerflow.backend.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findAllByApplicationId(Long applicationId);
}
