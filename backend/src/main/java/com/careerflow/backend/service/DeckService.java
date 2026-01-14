package com.careerflow.backend.service;

import com.careerflow.backend.dto.request.DeckRequest;
import com.careerflow.backend.dto.response.DeckResponse;

import java.util.List;

public interface DeckService {
    DeckResponse createDeck(DeckRequest request);
    List<DeckResponse> getAllDecks();
    void deleteDeck(Long id);
}
