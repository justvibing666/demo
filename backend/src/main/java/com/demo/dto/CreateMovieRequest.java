package com.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateMovieRequest {

    @NotBlank
    private String title;

    private LocalDate productionDate;

    private Integer durationMinutes;

    private String thumbnailUrl;

    private String description;

    private List<Long> genreIds;

    private List<Long> streamingServiceIds;

    private List<String> directorNames;

    private List<String> writerNames;

    private List<String> tags;
}
