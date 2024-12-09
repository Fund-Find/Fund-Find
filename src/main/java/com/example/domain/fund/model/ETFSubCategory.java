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
    IT_SEMICONDUCTOR("IT/반도체"),
    FINANCE("금융"),
    HEALTHCARE("헬스케어"),
    //고위험(HighLisk)
    LEVERAGE("레버리지"),
    INVERSE("인버스");

    private final String description;

    ETFSubCategory(String description) {
        this.description = description;
    }
}
