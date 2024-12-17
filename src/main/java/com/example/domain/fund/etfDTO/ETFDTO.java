package com.example.domain.fund.etfDTO;

import com.example.domain.fund.model.ETFCategory;
import com.example.domain.fund.model.ETFSubCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ETFDTO {
    private String code;
    private String name;
    private ETFCategory category;
    private ETFSubCategory subCategory;
}
