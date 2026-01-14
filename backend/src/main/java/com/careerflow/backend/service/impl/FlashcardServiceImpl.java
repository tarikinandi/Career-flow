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
        Deck deck = deckRepository.findById(request.getDeckId())
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found for this id : " + request.getDeckId()));

        FlashCard flashCard = flashcardMapper.toEntity(request);
        flashCard.setDeck(deck);
        FlashCard savedFlashCard = flashcardRepository.save(flashCard);
        return flashcardMapper.toResponse(savedFlashCard);
    }

    @Override
    public List<FlashcardResponse> getCardsByDeckId(Long deckId) {
        if (!deckRepository.existsById(deckId)) {
            throw new ResourceNotFoundException("Deck not found for this id : " + deckId);
        }
        return flashcardRepository.findAllByDeckId(deckId).stream()
                .map(flashcardMapper::toResponse)
                .collect(Collectors.toList());

    }

    @Override
    public List<FlashcardResponse> getDueCards(Long deckId) {
        if (!deckRepository.existsById(deckId)) {
            throw new ResourceNotFoundException("Deck not found for this id : " + deckId);
        }
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

        FlashCard card = flashcardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard not found for this id : " + cardId));

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
        if (!flashcardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Flashcard not found with id: " + id);
        }
        flashcardRepository.deleteById(id);
    }
}
