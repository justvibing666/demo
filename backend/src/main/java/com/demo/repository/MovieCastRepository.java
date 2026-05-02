package com.demo.repository;

import com.demo.model.MovieCast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieCastRepository extends JpaRepository<MovieCast, Long> {

    List<MovieCast> findByMovieIdOrderByBillingOrder(Long movieId);
}
