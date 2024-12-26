package com.example.domain.fund.model;

import lombok.Getter;

@Getter
public enum ETFSubCategory {
    //주식형(STOCK)
    LARGE_CAP("대형주"),
    SMALL_MID_CAP("중소형"),
    //채권형(BOND)
    DOMESTIC_BOND("국내채권"),
    FOREIGN_BOND("해외채권"),
    //원자재(COMMODITY)
    PRECIOUS_METAL("귀금속"),
    ENERGY("에너지"),
    //섹터(SECTOR)
    ELECTRONIC_COMPONENTS_AND_MATERIALS("전자부품 및 소재"),
    IT_ROBOT("IT/로봇"),
    DIGITAL_PLATFORM("디지털 플랫폼"),
    HEALTHCARE("헬스케어"),
    FINANCE("금융"),
    CONSTRUCTION_SHIP("건설 및 조선 해운"),
    ECO_FRIENDLY("친환경"),
    ESG("ESG"),
    //고위험(HighLisk)
    LEVERAGE("레버리지"),
    INVERSE("인버스");

    private final String description;

    ETFSubCategory(String description) {
        this.description = description;
    }
}
