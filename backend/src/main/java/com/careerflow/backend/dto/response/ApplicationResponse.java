package com.careerflow.backend.dto.response;

import com.careerflow.backend.model.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationResponse {
    private Long id;
    private String companyName;
    private String position;
    private ApplicationStatus status;
    private String location;
    private String salaryRange;
    private LocalDate appliedDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
