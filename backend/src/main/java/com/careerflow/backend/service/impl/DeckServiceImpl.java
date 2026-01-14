package com.careerflow.backend.service.impl;

import com.careerflow.backend.dto.request.DeckRequest;
import com.careerflow.backend.dto.response.DeckResponse;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.mapper.DeckMapper;
import com.careerflow.backend.model.Deck;
import com.careerflow.backend.repository.DeckRepository;
import com.careerflow.backend.service.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;
    private final DeckMapper deckMapper;


    @Override
    @Transactional
    public DeckResponse createDeck(DeckRequest request) {
        Deck deck = deckMapper.toEntity(request);
        Deck savedDeck = deckRepository.save(deck);
        return deckMapper.toResponse(savedDeck);
    }

    @Override
    public List<DeckResponse> getAllDecks() {
        return deckRepository.findAll().stream()
                .map(deckMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDeck(Long id) {
        if (!deckRepository.existsById(id)) {
            throw new ResourceNotFoundException("Deck not found for this id : " + id);
        }
        deckRepository.deleteById(id);
    }
}
