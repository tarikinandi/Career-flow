package com.careerflow.backend.service.impl;

import com.careerflow.backend.dto.request.FlashcardRequest;
import com.careerflow.backend.dto.response.FlashcardResponse;
import com.careerflow.backend.exception.BusinessException;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.mapper.FlashcardMapper;
import com.careerflow.backend.model.Deck;
import com.careerflow.backend.model.FlashCard;
import com.careerflow.backend.repository.DeckRepository;
import com.careerflow.backend.repository.FlashcardRepository;
import com.careerflow.backend.service.FlashcardService;
import com.careerflow.backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final DeckRepository deckRepository;
    private final FlashcardMapper flashcardMapper;

    @Override
    @Transactional
    public FlashcardResponse createFlashcard(FlashcardRequest request) {
        Deck deck = findOwnedDeck(request.getDeckId());

        FlashCard flashCard = flashcardMapper.toEntity(request);
        flashCard.setDeck(deck);
        FlashCard savedFlashCard = flashcardRepository.save(flashCard);
        return flashcardMapper.toResponse(savedFlashCard);
    }

    @Override
    public List<FlashcardResponse> getCardsByDeckId(Long deckId) {
        findOwnedDeck(deckId);
        return flashcardRepository.findAllByDeckId(deckId).stream()
                .map(flashcardMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlashcardResponse> getDueCards(Long deckId) {
        findOwnedDeck(deckId);
        return flashcardRepository.findAllByDeckIdAndNextReviewDateBefore(deckId, LocalDateTime.now())
                .stream()
                .map(flashcardMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FlashcardResponse studyCard(Long cardId, int qualityScore) {
        if (qualityScore < 0 || qualityScore > 5) {
            throw new BusinessException("Quality score should be between 0 and 5");
        }

        FlashCard card = findOwnedCard(cardId);

        if (qualityScore < 3){
            card.setRepetitions(0);
            card.setIntervalDays(1);
        }else {
            card.setRepetitions(card.getRepetitions() + 1);

            if (card.getRepetitions() == 1){
                card.setIntervalDays(1);
            }else if (card.getRepetitions() == 2){
                card.setIntervalDays(6);
            }else {
                card.setIntervalDays((int) Math.round(card.getIntervalDays() * card.getEasinessFactor()));
            }
        }

        double newEf = card.getEasinessFactor() + (0.1 - (5 - qualityScore) * (0.08 + (5 - qualityScore) * 0.02));

        if (newEf < 1.3){
            newEf = 1.3;
        }

        card.setEasinessFactor(newEf);

        card.setNextReviewDate(LocalDateTime.now().plusDays(card.getIntervalDays()));

        FlashCard updatedCard = flashcardRepository.save(card);
        return flashcardMapper.toResponse(updatedCard);
    }

    @Override
    @Transactional
    public void deleteFlashcard(Long id) {
        flashcardRepository.delete(findOwnedCard(id));
    }

    /** Foreign decks are reported as missing (404), so their existence is not revealed. */
    private Deck findOwnedDeck(Long deckId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return deckRepository.findByIdAndUserId(deckId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found for this id : " + deckId));
    }

    /** Cards are owned through their deck. */
    private FlashCard findOwnedCard(Long cardId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return flashcardRepository.findByIdAndDeckUserId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard not found for this id : " + cardId));
    }
}
