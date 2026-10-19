package com.careerflow.backend.repository;

import com.careerflow.backend.model.Deck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {
    Page<Deck> findByUserId(Long userId, Pageable pageable);

    Optional<Deck> findByIdAndUserId(Long id, Long userId);
}
