package com.example.domain.fund.entity;

import com.example.domain.favorite.entity.ETFFavorite;
import com.example.domain.fund.model.ETFCategory;
import com.example.domain.fund.model.ETFSubCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "etf_sub_category")
    private ETFSubCategory subCategory;

    @Column(name = "current_price")
    private String price;

    @Column(name = "component_count")
    private String componentCount;

    @Column(name = "net_asset")
    private String netAsset;

    @Column(name = "nav")
    private String nav;

    @Column(name = "prev_final_nav")
    private String prevNav;

    @Column(name = "nav_change")
    private String navChange;

    @Column(name = "dividend_cycle")
    private String dividendCycle;

    @Column(name = "company_name")
    private String company;

    @Column(name = "price_change")
    private String priceChange;

    @Column(name = "price_change_rate")
    private String priceChangeRate;

    @OneToMany(mappedBy = "etf")
    private List<ETFFavorite> favorites = new ArrayList<>();

    @Builder
    public ETF(String code, String name, ETFCategory category, ETFSubCategory subCategory,
               String price, String componentCount, String netAsset, String nav, String prevNav, String navChange, String dividendCycle,
               String company, String priceChange, String priceChangeRate) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;
        this.price = price;
        this.componentCount = componentCount;
        this.netAsset = netAsset;
        this.nav = nav;
        this.prevNav = prevNav;
        this.navChange = navChange;
        this.dividendCycle = dividendCycle;
        this.company = company;
        this.priceChange = priceChange;
        this.priceChangeRate = priceChangeRate;
    }

}

