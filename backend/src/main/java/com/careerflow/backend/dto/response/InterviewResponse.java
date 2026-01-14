package com.careerflow.backend.dto.response;

import com.careerflow.backend.model.enums.InterviewRound;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InterviewResponse {

    private Long id;
    private Long applicationId;
    private InterviewRound round;
    private LocalDateTime interviewDate;
    private String interviewerName;
    private String meetingLink;
    private String notes;
    private boolean followUpSent;

}
