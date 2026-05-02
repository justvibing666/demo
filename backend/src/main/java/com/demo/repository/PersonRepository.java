package com.demo.repository;

import com.demo.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    List<Person> findByNameContainingIgnoreCase(String name);

    Optional<Person> findByNameIgnoreCase(String name);
}
