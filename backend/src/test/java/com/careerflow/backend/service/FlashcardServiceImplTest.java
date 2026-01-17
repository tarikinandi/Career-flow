package com.careerflow.backend.service;

import com.careerflow.backend.dto.request.FlashcardRequest;
import com.careerflow.backend.dto.response.FlashcardResponse;
import com.careerflow.backend.exception.BusinessException;
import com.careerflow.backend.mapper.FlashcardMapper;
import com.careerflow.backend.model.Deck;
import com.careerflow.backend.model.FlashCard;
import com.careerflow.backend.repository.DeckRepository;
import com.careerflow.backend.repository.FlashcardRepository;
import com.careerflow.backend.service.impl.FlashcardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlashcardServiceImplTest {

    @Mock
    private FlashcardRepository flashcardRepository;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private FlashcardMapper flashcardMapper;

    @InjectMocks
    private FlashcardServiceImpl flashcardService;

    @Test
    void createFlashcard_ShouldInitializeValuesCorrectly() {
        FlashcardRequest request = new FlashcardRequest();
        request.setDeckId(1L);

        Deck deck = new Deck();
        deck.setId(1L);

        FlashCard flashCard = FlashCard.builder().id(100L).build();
        FlashcardResponse response = new FlashcardResponse();
        response.setId(100L);

        when(deckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(flashcardMapper.toEntity(request)).thenReturn(flashCard);
        when(flashcardRepository.save(any(FlashCard.class))).thenReturn(flashCard);
        when(flashcardMapper.toResponse(flashCard)).thenReturn(response);

        FlashcardResponse result = flashcardService.createFlashcard(request);

        assertNotNull(result);
        verify(flashcardRepository).save(any(FlashCard.class));
    }

    @Test
    void studyCard_ShouldResetProgress_WhenQualityIsLow() {
        FlashCard card = FlashCard.builder()
                .id(1L)
                .repetitions(5)
                .intervalDays(10)
                .easinessFactor(2.5)
                .build();

        when(flashcardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(flashcardRepository.save(any(FlashCard.class))).thenAnswer(i -> i.getArguments()[0]);
        when(flashcardMapper.toResponse(any(FlashCard.class))).thenReturn(new FlashcardResponse());

        flashcardService.studyCard(1L, 0);

        assertEquals(0, card.getRepetitions());
        assertEquals(1, card.getIntervalDays());
    }

    @Test
    void studyCard_ShouldIncreaseInterval_WhenQualityIsHigh() {
        FlashCard card = FlashCard.builder()
                .id(1L)
                .repetitions(2)
                .intervalDays(6)
                .easinessFactor(2.5)
                .build();

        when(flashcardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(flashcardRepository.save(any(FlashCard.class))).thenAnswer(i -> i.getArguments()[0]);
        when(flashcardMapper.toResponse(any(FlashCard.class))).thenReturn(new FlashcardResponse());

        flashcardService.studyCard(1L, 5);

        assertEquals(3, card.getRepetitions());
        assertTrue(card.getIntervalDays() > 6);
        assertTrue(card.getEasinessFactor() > 2.5);
    }

    @Test
    void studyCard_ShouldThrowException_WhenQualityIsInvalid() {
        assertThrows(BusinessException.class, () -> flashcardService.studyCard(1L, 6));
        assertThrows(BusinessException.class, () -> flashcardService.studyCard(1L, -1));
    }
}