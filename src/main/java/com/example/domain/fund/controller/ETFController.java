package com.example.domain.fund.controller;

import com.example.domain.fund.service.ETFService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/etf")
public class ETFController {

    private final ETFService etfService;

    @GetMapping("/ETF")
    public String getStockInfo(Model model) {
        try {
            String etfInfo = etfService.getETFInfo("069500");
            model.addAttribute("etfInfo", etfInfo);
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching stock information: " + e.getMessage());
        }
        return "etfInfo";
    }
}