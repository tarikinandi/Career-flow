package com.careerflow.backend.dto.request;

import com.careerflow.backend.model.enums.InterviewRound;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InterviewRequest {

    @NotNull(message = "Interview round is required")
    private InterviewRound round;

    private LocalDateTime interviewDate;

    private String interviewerName;

    private String meetingLink;

    private String notes;

}
