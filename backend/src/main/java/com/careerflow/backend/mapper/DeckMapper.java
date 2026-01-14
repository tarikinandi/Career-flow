package com.careerflow.backend.mapper;

import com.careerflow.backend.dto.request.DeckRequest;
import com.careerflow.backend.dto.response.DeckResponse;
import com.careerflow.backend.model.Deck;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeckMapper {

    @Mapping(target = "totalCards", source = "flashCards", qualifiedByName = "mapTotalCards")
    DeckResponse toResponse(Deck entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "flashCards" , ignore = true)
    @Mapping(target = "createdAt" , ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Deck toEntity(DeckRequest request);

    @Named("mapTotalCards")
    default int mapTotalCards(List<?> flashcards) {
        return flashcards == null ? 0 : flashcards.size();
    }
}
