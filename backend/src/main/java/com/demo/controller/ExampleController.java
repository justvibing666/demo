package com.demo.controller;

import com.demo.model.ExampleEntity;
import com.demo.service.ExampleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/examples")
@RequiredArgsConstructor
public class ExampleController {

    private final ExampleService exampleService;

    @GetMapping
    public ResponseEntity<List<ExampleEntity>> getAll() {
        return ResponseEntity.ok(exampleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExampleEntity> getById(@PathVariable Long id) {
        return exampleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExampleEntity> create(@Valid @RequestBody ExampleEntity entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exampleService.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        exampleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
