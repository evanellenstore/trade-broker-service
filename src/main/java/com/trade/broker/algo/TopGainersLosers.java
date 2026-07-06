package com.trade.broker.algo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.utils.Constants;

public class TopGainersLosers {
    public  void getTopGainersLosers(SmartConnect smartConnect ) {
        try {
            

            // Step 2: Define Stock Tokens (Replace with actual stock tokens from Angel One CSV)
            Map<String, String> stockList = new HashMap<>();
            stockList.put("RELIANCE", "3045");
            stockList.put("TCS", "11536");
            stockList.put("HDFC", "2885");
            stockList.put("INFY", "1594");
            stockList.put("NIFTY 50", "26000");  // Index Example

            // Step 3: Fetch Market Data for Each Stock
            List<StockChange> stockChanges = new ArrayList<>();
            for (Map.Entry<String, String> entry : stockList.entrySet()) {
                String stockName = entry.getKey();
                String stockToken = entry.getValue();

                // Fetch market data
                JSONObject marketData = smartConnect.getLTP(Constants.EXCHANGE_NSE, stockToken,"");
                //double ltp = marketData.getLtp();
                //double prevClose = marketData.getClose();
                
                double ltp=00;
                double prevClose=00;

                // Calculate percentage change
                double changePercent = ((ltp - prevClose) / prevClose) * 100;
                stockChanges.add(new StockChange(stockName, ltp, prevClose, changePercent));
            }

            // Step 4: Sort Stocks by Percentage Change
            stockChanges.sort(Comparator.comparingDouble(StockChange::getChangePercent).reversed());

            // Step 5: Print Top Gainers & Top Losers
            System.out.print(" Top Gainers: ");
            stockChanges.stream().limit(3).forEach(System.out::println);

            System.out.println("Top Losers: ");
            stockChanges.stream().sorted(Comparator.comparingDouble(StockChange::getChangePercent))
                        .limit(3).forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper class to store stock data
    static class StockChange {
        String stockName;
        double ltp;
        double prevClose;
        double changePercent;

        public StockChange(String stockName, double ltp, double prevClose, double changePercent) {
            this.stockName = stockName;
            this.ltp = ltp;
            this.prevClose = prevClose;
            this.changePercent = changePercent;
        }

        public double getChangePercent() {
            return changePercent;
        }

        @Override
        public String toString() {
            return stockName + " - LTP: " + ltp + ", Change%: " + String.format("%.2f", changePercent) + "%";
        }
    }
}
