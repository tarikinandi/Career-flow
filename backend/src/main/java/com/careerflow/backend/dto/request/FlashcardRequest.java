package com.careerflow.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlashcardRequest {

    @NotNull(message = "Soru ID'si gerekiyor.")
    private Long deckId;

    @NotBlank(message = "Soru kısmı boş olamaz.")
    private String frontContent;

    @NotBlank(message = "Cevap kısmı boş olamaz.")
    private String backContent;
}
