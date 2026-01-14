package com.careerflow.backend.service;

import com.careerflow.backend.dto.request.FlashcardRequest;
import com.careerflow.backend.dto.response.FlashcardResponse;

import java.util.List;

public interface FlashcardService {
    FlashcardResponse createFlashcard(FlashcardRequest request);
    List<FlashcardResponse> getCardsByDeckId(Long deckId);
    List<FlashcardResponse> getDueCards(Long deckId);
    FlashcardResponse studyCard(Long cardId, int qualityScore);
    void deleteFlashcard(Long id);
}
