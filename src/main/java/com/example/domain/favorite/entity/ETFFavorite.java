package com.example.domain.favorite.entity;

import com.example.domain.fund.entity.ETF;
import com.example.domain.user.entity.SiteUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ETFFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private SiteUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    private ETF etf;

    @Builder
    public ETFFavorite(SiteUser user, ETF etf) {
        this.user = user;
        this.etf = etf;
    }
}

