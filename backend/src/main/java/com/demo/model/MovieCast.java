package com.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movie_cast")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieCast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person actor;

    @Column(name = "character_name", length = 200)
    private String characterName;

    @Column(name = "billing_order")
    private Integer billingOrder;
}
