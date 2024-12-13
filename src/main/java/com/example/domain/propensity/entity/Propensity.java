package com.example.domain.propensity.entity;

import com.example.domain.user.entity.SiteUser;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Propensity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long propensityId;

    @Column
    private String surveyAnswer;

    @Column
    private String surveyResult;

    @Column
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "propensity")
    private List<SiteUser> user;
}
