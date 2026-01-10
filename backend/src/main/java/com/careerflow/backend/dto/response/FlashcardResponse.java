package com.careerflow.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FlashcardResponse {

    private Long id;
    private Long deckId;
    private String frontContent;
    private String backContent;
    private LocalDateTime nextReviewDate;
}
