package com.demo.service;

import com.demo.model.ExampleEntity;
import com.demo.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleService {

    private final ExampleRepository exampleRepository;

    @Transactional(readOnly = true)
    public List<ExampleEntity> findAll() {
        log.debug("Pobieranie wszystkich rekordów");
        return exampleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ExampleEntity> findById(Long id) {
        return exampleRepository.findById(id);
    }

    @Transactional
    public ExampleEntity save(ExampleEntity entity) {
        log.info("Zapisywanie encji: {}", entity.getName());
        return exampleRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Usuwanie encji o id: {}", id);
        exampleRepository.deleteById(id);
    }
}
