package com.demo.service;

import com.demo.dto.CreateMovieRequest;
import com.demo.dto.MovieSummaryDto;
import com.demo.model.*;
import com.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final StreamingServiceRepository streamingServiceRepository;
    private final PersonRepository personRepository;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<MovieSummaryDto> findAll() {
        log.debug("Pobieranie listy filmów");
        return movieRepository.findAll().stream()
                .map(m -> new MovieSummaryDto(
                        m.getId(),
                        m.getTitle(),
                        m.getProductionDate() != null ? m.getProductionDate().getYear() : null,
                        m.getThumbnailUrl()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public MovieSummaryDto create(CreateMovieRequest req) {
        log.info("Tworzenie nowego filmu: {}", req.getTitle());

        Movie movie = Movie.builder()
                .title(req.getTitle())
                .productionDate(req.getProductionDate())
                .durationMinutes(req.getDurationMinutes())
                .thumbnailUrl(req.getThumbnailUrl())
                .description(req.getDescription())
                .build();

        if (req.getGenreIds() != null && !req.getGenreIds().isEmpty()) {
            movie.setGenres(new HashSet<>(genreRepository.findAllById(req.getGenreIds())));
        }

        if (req.getStreamingServiceIds() != null && !req.getStreamingServiceIds().isEmpty()) {
            movie.setStreamingServices(new HashSet<>(streamingServiceRepository.findAllById(req.getStreamingServiceIds())));
        }

        if (req.getDirectorNames() != null) {
            Set<Person> directors = req.getDirectorNames().stream()
                    .filter(n -> n != null && !n.isBlank())
                    .map(this::findOrCreatePerson)
                    .collect(Collectors.toSet());
            movie.setDirectors(directors);
        }

        if (req.getWriterNames() != null) {
            Set<Person> writers = req.getWriterNames().stream()
                    .filter(n -> n != null && !n.isBlank())
                    .map(this::findOrCreatePerson)
                    .collect(Collectors.toSet());
            movie.setWriters(writers);
        }

        if (req.getTags() != null) {
            Set<Tag> tags = req.getTags().stream()
                    .filter(n -> n != null && !n.isBlank())
                    .map(this::findOrCreateTag)
                    .collect(Collectors.toSet());
            movie.setTags(tags);
        }

        Movie saved = movieRepository.save(movie);
        return new MovieSummaryDto(
                saved.getId(),
                saved.getTitle(),
                saved.getProductionDate() != null ? saved.getProductionDate().getYear() : null,
                saved.getThumbnailUrl()
        );
    }

    private Person findOrCreatePerson(String name) {
        String trimmed = name.trim();
        return personRepository.findByNameIgnoreCase(trimmed)
                .orElseGet(() -> personRepository.save(Person.builder().name(trimmed).build()));
    }

    private Tag findOrCreateTag(String name) {
        String trimmed = name.trim();
        return tagRepository.findByNameIgnoreCase(trimmed)
                .orElseGet(() -> tagRepository.save(Tag.builder().name(trimmed).build()));
    }
}
