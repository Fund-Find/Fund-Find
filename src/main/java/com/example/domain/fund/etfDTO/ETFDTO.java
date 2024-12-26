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
    private String price;
    private String componentCount;
    private String netAsset;
    private String nav;
    private String prevNav;
    private String navChange;
    private String dividendCycle;
    private String company;
    private String priceChange;
    private String priceChangeRate;
}
