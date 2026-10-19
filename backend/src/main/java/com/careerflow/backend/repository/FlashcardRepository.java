package com.careerflow.backend.repository;

import com.careerflow.backend.model.FlashCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlashcardRepository extends JpaRepository<FlashCard, Long> {

    List<FlashCard> findAllByDeckId(Long deckId);

    /** Ownership comes from the parent deck. */
    Optional<FlashCard> findByIdAndDeckUserId(Long id, Long userId);

    List<FlashCard> findAllByDeckIdAndNextReviewDateBefore(Long deckId, LocalDateTime date);

}
