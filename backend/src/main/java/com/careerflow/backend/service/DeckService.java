package com.careerflow.backend.service;

import com.careerflow.backend.dto.request.DeckRequest;
import com.careerflow.backend.dto.response.DeckResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeckService {
    DeckResponse createDeck(DeckRequest request);
    Page<DeckResponse> getAllApplications(Pageable pageable);
    void deleteDeck(Long id);
}
