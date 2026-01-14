package com.careerflow.backend.controller;

import com.careerflow.backend.dto.global.ApiResponse;
import com.careerflow.backend.dto.request.DeckRequest;
import com.careerflow.backend.dto.response.DeckResponse;
import com.careerflow.backend.service.DeckService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    @PostMapping
    public ResponseEntity<ApiResponse<DeckResponse>> createDeck(@Valid @RequestBody DeckRequest request) {
        DeckResponse response = deckService.createDeck(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Deck created successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeckResponse>>> getAllDecks() {
        return ResponseEntity.ok(ApiResponse.success(deckService.getAllDecks(), "Decks fetched successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDeck(@PathVariable Long id) {
        deckService.deleteDeck(id);
        return ResponseEntity.ok(ApiResponse.success("Deck deleted successfully"));
    }
}
