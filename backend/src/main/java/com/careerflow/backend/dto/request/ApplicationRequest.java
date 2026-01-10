package com.careerflow.backend.dto.request;

import com.careerflow.backend.model.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ApplicationRequest {

    @NotBlank(message = "Şirket adı boş olamaz.")
    private String companyName;

    @NotBlank(message = "Pozisyon boş olamaz.")
    private String position;

    @NotNull(message = "Durum boş olamaz.")
    private ApplicationStatus status;

    private String location;
    private String salaryRange;
    private LocalDate appliedDate;
    private String notes;
}
