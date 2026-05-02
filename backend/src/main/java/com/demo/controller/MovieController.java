package com.demo.controller;

import com.demo.dto.CreateMovieRequest;
import com.demo.dto.MovieSummaryDto;
import com.demo.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<MovieSummaryDto>> getAll() {
        return ResponseEntity.ok(movieService.findAll());
    }

    @PostMapping
    public ResponseEntity<MovieSummaryDto> create(@Valid @RequestBody CreateMovieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.create(request));
    }
}
