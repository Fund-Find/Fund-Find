package com.example.domain.fund.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ETF {

/*
    ticker는 ETF 종목 고유 코드를 의미
    나중에 ETF 객체를 생성할 때, ETF kodex200 = new ETF("069500", "KODEX 200", 31000.0);
    이렇게 특정 ETF의 고유 코드를 설정하여, 나중에 서비스 계층에서 필요한 데이터를 조회하거나
    응답으로 반환할 때 사용할 수 있음
*/

    private String ticker;
    private String name;
    private double price;

    public ETF(String ticker, String name, double price) {
        this.ticker = ticker;
        this.name = name;
        this.price = price;
    }
}

