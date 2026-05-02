package com.demo.repository;

import com.demo.model.MoviePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoviePhotoRepository extends JpaRepository<MoviePhoto, Long> {

    List<MoviePhoto> findByMovieId(Long movieId);
}
