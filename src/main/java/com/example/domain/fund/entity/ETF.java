package com.example.domain.fund.entity;

import com.example.domain.fund.model.ETFCategory;
import com.example.domain.fund.model.ETFSubCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ETF {
    @Id
    private String code;

    @Column(name = "etf_name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "etf_category")
    private ETFCategory category;

    @Enumerated(EnumType.STRING)
    private ETFSubCategory subCategory;

    @Builder
    public ETF(String code, String name, ETFCategory category, ETFSubCategory subCategory) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;
    }
}

