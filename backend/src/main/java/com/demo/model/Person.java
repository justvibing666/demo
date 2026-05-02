package com.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @ManyToMany(mappedBy = "directors")
    @Builder.Default
    private Set<Movie> directedMovies = new HashSet<>();

    @ManyToMany(mappedBy = "writers")
    @Builder.Default
    private Set<Movie> writtenMovies = new HashSet<>();

    @OneToMany(mappedBy = "actor")
    @Builder.Default
    private List<MovieCast> castRoles = new ArrayList<>();
}
