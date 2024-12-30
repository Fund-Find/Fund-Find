package com.example.domain.user.entity;

import com.example.domain.favorite.entity.ETFFavorite;
import com.example.domain.propensity.entity.Propensity;
import com.example.domain.quizShow.entity.QuizShow;
import com.example.global.jpa.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SiteUser extends BaseEntity {

    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    @Column(unique = true)
    private String email;

    @Column
    private String name;

    @Column(unique = true)
    private String socialProvider;

    @Column(columnDefinition = "text")
    private String intro;

    private String nickname;

    private String thumbnailImg;

    @JsonIgnore
    private String refreshToken;

    @ManyToOne
    @JsonManagedReference
    private Propensity propensity;

    @ManyToMany(mappedBy = "votes")
    @JsonIgnore
    private Set<QuizShow> votedQuizShows = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<ETFFavorite> favorites = new ArrayList<>();

    private LocalDate lastSurveyDate;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int dailySurveyCount;
}