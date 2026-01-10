package com.careerflow.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "flashcards")
public class FlashCard extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String frontContent;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String backContent;

    @Builder.Default
    private double easinessFactor = 2.5;

    @Builder.Default
    private int repetitions = 0;

    @Builder.Default
    private int intervalDays = 0;

    private LocalDateTime nextReviewDate;
}
