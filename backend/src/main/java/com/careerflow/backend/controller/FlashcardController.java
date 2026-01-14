package com.careerflow.backend.controller;

import com.careerflow.backend.dto.global.ApiResponse;
import com.careerflow.backend.dto.request.FlashcardRequest;
import com.careerflow.backend.dto.response.FlashcardResponse;
import com.careerflow.backend.service.FlashcardService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService flashcardService;

    @PostMapping("/flashcards")
    public ResponseEntity<ApiResponse<FlashcardResponse>> createFlashcard(@Valid @RequestBody FlashcardRequest request) {
        return new ResponseEntity<>(ApiResponse.success(flashcardService.createFlashcard(request), "Flashcard created"), HttpStatus.CREATED);
    }

    @GetMapping("/decks/{deckId}/cards")
    public ResponseEntity<ApiResponse<List<FlashcardResponse>>> getCardsByDeck(@PathVariable Long deckId) {
        return ResponseEntity.ok(ApiResponse.success(flashcardService.getCardsByDeckId(deckId), "Cards fetched"));
    }

    @GetMapping("/decks/{deckId}/due-cards")
    public ResponseEntity<ApiResponse<List<FlashcardResponse>>> getDueCards(@PathVariable Long deckId) {
        return ResponseEntity.ok(ApiResponse.success(flashcardService.getDueCards(deckId), "Due cards fetched"));
    }

    @PostMapping("/flashcards/{cardId}/study")
    public ResponseEntity<ApiResponse<FlashcardResponse>> studyCard(
            @PathVariable Long cardId,
            @RequestParam int quality) {
        return ResponseEntity.ok(ApiResponse.success(flashcardService.studyCard(cardId, quality), "Card studied updated"));
    }

    @DeleteMapping("/flashcards/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFlashcard(@PathVariable Long id) {
        flashcardService.deleteFlashcard(id);
        return ResponseEntity.ok(ApiResponse.success("Flashcard deleted"));
    }
}
