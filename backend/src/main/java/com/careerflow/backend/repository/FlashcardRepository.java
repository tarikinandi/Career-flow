package com.careerflow.backend.repository;

import com.careerflow.backend.model.FlashCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FlashcardRepository extends JpaRepository<FlashCard, Long> {

    List<FlashCard> findAllByDeckId(Long deckId);

    List<FlashCard> findAllByDeckIdAndNextReviewDateBefore(Long deckId, LocalDateTime date);

}
