package com.careerflow.backend.service.impl;

import com.careerflow.backend.dto.request.DeckRequest;
import com.careerflow.backend.dto.response.DeckResponse;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.mapper.DeckMapper;
import com.careerflow.backend.model.Deck;
import com.careerflow.backend.model.User;
import com.careerflow.backend.repository.DeckRepository;
import com.careerflow.backend.service.DeckService;
import com.careerflow.backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;
    private final DeckMapper deckMapper;


    @Override
    @Transactional
    public DeckResponse createDeck(DeckRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();

        Deck deck = deckMapper.toEntity(request);

        deck.setUser(currentUser);

        Deck savedDeck = deckRepository.save(deck);
        return deckMapper.toResponse(savedDeck);
    }

    @Override
    public Page<DeckResponse> getAllApplications(Pageable pageable) {
        User currentUser = SecurityUtils.getCurrentUser();

        return deckRepository.findByUserId(currentUser.getId(), pageable)
                .map(deckMapper::toResponse);
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
