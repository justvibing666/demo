package com.demo.controller;

import com.demo.dto.StreamingServiceDto;
import com.demo.repository.StreamingServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/streaming-services")
@RequiredArgsConstructor
public class StreamingServiceController {

    private final StreamingServiceRepository streamingServiceRepository;

    @GetMapping
    public ResponseEntity<List<StreamingServiceDto>> getAll() {
        List<StreamingServiceDto> services = streamingServiceRepository.findAll().stream()
                .map(s -> new StreamingServiceDto(s.getId(), s.getName(), s.getLogoUrl()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(services);
    }
}
