package com.trade.broker.algo.indicator;

import java.util.ArrayList;
import java.util.List;

public class CandleAnalyzer {


    // List to store all the candles
    private final List<Candle> candles = new ArrayList<>();

    
    public boolean checkCandleBreakHigh(Candle newCandle) {
    	boolean result=false;
    
        if (!candles.isEmpty()) {
            Candle previousCandle = candles.get(candles.size() - 1);
            if (newCandle.getHigh() > previousCandle.getHigh())
            {
                System.out.println("Breakout! Current candle high (" + newCandle.getHigh() +
                                   ") is above previous candle high (" + previousCandle.getHigh() + ").");
                result= true;
            } else {
            	result= false;
            }
        } 
        candles.add(newCandle);
        
        
        return result;
    }
    
}