package com.careerflow.backend.mapper;

import com.careerflow.backend.dto.request.FlashcardRequest;
import com.careerflow.backend.dto.response.FlashcardResponse;
import com.careerflow.backend.model.FlashCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FlashcardMapper {

    @Mapping(source = "deck.id", target = "deckId")
    FlashcardResponse toResponse(FlashCard entity);

    @Mapping(target = "deck", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "easinessFactor", ignore = true)
    @Mapping(target = "repetitions", ignore = true)
    @Mapping(target = "intervalDays", ignore = true)
    @Mapping(target = "nextReviewDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FlashCard toEntity(FlashcardRequest request);
}
