package com.example.domain.fund.model;

import lombok.Getter;

@Getter
public enum ETFCategory {

    STOCK("주식형"),
    BOND("채권형"),
    COMMODITY("원자재"),
    SECTOR("섹터별"),
    HighLisk("고위험");

    private final String description;

    ETFCategory(String description) {
        this.description = description;
    }
}
