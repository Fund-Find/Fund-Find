package com.example.domain.propersity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

}
