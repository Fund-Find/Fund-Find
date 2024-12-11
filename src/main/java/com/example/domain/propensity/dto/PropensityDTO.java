package com.example.domain.propensity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropensityDTO {

    private Long propensityId;
    private String surveyAnswer;
    private String surveyResult;

}
