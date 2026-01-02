package com.example.demo.Service;

import com.example.demo.Repository.StockRepository;
import com.example.demo.model.Stock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Random random = new Random();
    private volatile String singleSubscribedSymbol = null;

    public StockService(StockRepository stockRepository,
                        SimpMessagingTemplate messagingTemplate) {
        this.stockRepository = stockRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void init() {

        stockRepository.saveAll(List.of(
                new Stock("AAPL", "Apple", 180),
                new Stock("GOOG", "Google", 135),
                new Stock("MSFT", "Microsoft", 420),
                new Stock("AMZN", "Amazon", 155),
                new Stock("NTF", "Netflix", 165),
                new Stock("SPF", "Spotify", 210)
        ));

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::updatePrices, 0, 1, TimeUnit.SECONDS);
    }

    public void subscribeSingleStock(String symbol) {
        this.singleSubscribedSymbol = symbol.toUpperCase();
    }


    private void updatePrices() {

        List<Stock> stocks = stockRepository.findAll();

        for (Stock stock : stocks) {
            double change = (random.nextDouble() * 2 - 1) * 0.5;
            double newPrice = stock.getPrice() * (1 + change / 100);
            stock.setPrice(Math.round(newPrice * 100.0) / 100.0);
        }
        stockRepository.saveAll(stocks);

        messagingTemplate.convertAndSend("/topic/prices", stocks);

        if (singleSubscribedSymbol != null) {
            System.out.println(singleSubscribedSymbol);
            stockRepository.findById(singleSubscribedSymbol)
                    .ifPresent(stock ->
                            {
                                System.out.println(stock+ "From service");
                            messagingTemplate.convertAndSend("/topic/single", stock);}
                    );

        }
    }
}