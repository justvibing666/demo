package com.demo.repository;

import com.demo.model.ExampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExampleRepository extends JpaRepository<ExampleEntity, Long> {

    List<ExampleEntity> findByNameContainingIgnoreCase(String name);
}
