package com.careerflow.backend.model;

import com.careerflow.backend.model.enums.InterviewRound;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "interviews")
public class Interview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    private InterviewRound round;

    private LocalDateTime interviewDate;

    private String interviewerName;

    private String meetingLink;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private boolean followUpSent;

}
