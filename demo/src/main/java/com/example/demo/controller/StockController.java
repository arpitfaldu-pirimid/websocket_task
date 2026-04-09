package com.example.demo.controller;

import com.example.demo.service.StockService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @MessageMapping("/single-stock")
    public void singleStock(String symbol) {
        System.out.println("reaches to controller");
        System.out.println(symbol);
        stockService.subscribeSingleStock(symbol);
    }
}