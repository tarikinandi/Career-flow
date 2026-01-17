package com.careerflow.backend.repository;

import com.careerflow.backend.model.Application;
import com.careerflow.backend.model.Deck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {
    Page<Deck> findByUserId(Long userId, Pageable pageable);
}
