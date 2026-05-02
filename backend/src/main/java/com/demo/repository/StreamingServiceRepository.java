package com.demo.repository;

import com.demo.model.StreamingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreamingServiceRepository extends JpaRepository<StreamingService, Long> {

    Optional<StreamingService> findByNameIgnoreCase(String name);
}
