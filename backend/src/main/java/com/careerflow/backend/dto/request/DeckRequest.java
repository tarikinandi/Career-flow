package com.careerflow.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeckRequest {

    @NotBlank(message = "Title cannot be empty")
    private String title;
    private String description;

}
