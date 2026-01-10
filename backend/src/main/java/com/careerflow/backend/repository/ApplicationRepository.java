package com.careerflow.backend.repository;

import com.careerflow.backend.model.Application;
import com.careerflow.backend.model.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findAllByStatus(ApplicationStatus status);
}
