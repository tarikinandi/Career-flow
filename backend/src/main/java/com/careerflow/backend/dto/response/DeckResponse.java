package com.careerflow.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeckResponse {

    private Long id;
    private String title;
    private String description;
    private int totalCards;
}
