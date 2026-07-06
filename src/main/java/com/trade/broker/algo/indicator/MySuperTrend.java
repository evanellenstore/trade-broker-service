package com.trade.broker.algo.indicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySuperTrend {
	
	// Method to calculate ATR from a list of candles given the period
    public List<Double> calculateATR(List<Candle> candles, int atrPeriod) {
    	 List<Double> atrValues = new ArrayList<>();
         List<Double> trueRanges = new ArrayList<>();
         
      // Calculate True Range (TR) for each candle
         for (int i = 0; i < candles.size(); i++) {
             Candle current = candles.get(i);
             double tr;
             if (i == 0) {
                 tr = current.getHigh() - current.getLow();
             } else {
            	 Candle previous = candles.get(i - 1);
            	 double range1 = current.getHigh() - current.getLow();
            	 double range2 = Math.abs(current.getHigh() - previous.getClose());
            	 double range3 = Math.abs(current.getLow() - previous.getClose());
            	 tr = Math.max(range1, Math.max(range2, range3));
            	 
            	 
             }
             
             trueRanges.add(tr);
             
          // Only start ATR calculation once we have enough candles
             
             if (i >= atrPeriod - 1) {
            	 if (i == atrPeriod - 1) {
            		// First ATR is the simple average of the first 'atrPeriod' TR values
            		 double sum = 0;
                     for (int j = 0; j < atrPeriod; j++) {
                         sum += trueRanges.get(j);
                     }
                     double atr = sum / atrPeriod;
                     atrValues.add(atr);
            	 }else {
            		 // Subsequent ATR values use the smoothing formula
            		 double previousATR = atrValues.get(atrValues.size() - 1);
            		 double atr = ((previousATR * (atrPeriod - 1)) + trueRanges.get(i)) / atrPeriod;
            		 atrValues.add(atr);
            	 }
             }

         }
         
         return atrValues;
    }
    
    
    
    public SuperTrendDTO calculateSuperTrend(List<Candle> candles, int atrPeriod, double multiplier) {
        // Return an empty DTO if not enough candles
        SuperTrendDTO superTrendDTO = new SuperTrendDTO();
        if (candles.size() < atrPeriod) {
            System.out.println("Not enough candle data to calculate ATR and SuperTrend.");
            return superTrendDTO;
        }
        
        List<Double> atrList = calculateATR(candles, atrPeriod);
        List<Double> finalUpperBand = new ArrayList<>();
        List<Double> finalLowerBand = new ArrayList<>();
        List<Double> superTrend = new ArrayList<>();
        
        // A flag to track whether the current trend is up or down
        boolean isUptrend = false;
        
        // Start calculating SuperTrend from the candle where the first ATR is available
        int startIndex = atrPeriod - 1;
        for (int i = startIndex; i < candles.size(); i++) {
            Candle current = candles.get(i);
            double atr = atrList.get(i - startIndex);
            
            // Calculate basic bands
            double basicUpperBand = ((current.getHigh() + current.getLow()) / 2) + (multiplier * atr);
            double basicLowerBand = ((current.getHigh() + current.getLow()) / 2) - (multiplier * atr);
            
            double finalUB, finalLB;
            double st; // current SuperTrend
            
            if (i == startIndex) {
                // For the first calculation, final bands are simply the basic bands
                finalUB = basicUpperBand;
                finalLB = basicLowerBand;
                // Initialize SuperTrend based on current close relative to the basic upper band
                if (current.getClose() <= basicUpperBand) {
                    st = finalUB;
                    isUptrend = false;
                } else {
                    st = finalLB;
                    isUptrend = true;
                }
            } else {
                // Get previous final bands and previous candle for adjustment
                double prevFinalUB = finalUpperBand.get(i - startIndex - 1);
                double prevFinalLB = finalLowerBand.get(i - startIndex - 1);
                Candle previous = candles.get(i - 1);
                
                // Adjust final upper band
                finalUB = (basicUpperBand < prevFinalUB || previous.getClose() > prevFinalUB)
                          ? basicUpperBand : prevFinalUB;
                // Adjust final lower band
                finalLB = (basicLowerBand > prevFinalLB || previous.getClose() < prevFinalLB)
                          ? basicLowerBand : prevFinalLB;
                
                // Use the boolean flag to determine the trend from the previous period
                if (!isUptrend) { // Previous trend was down
                    if (current.getClose() <= finalUB) {
                        st = finalUB;
                        isUptrend = false;
                    } else {
                        st = finalLB;
                        isUptrend = true;
                    }
                } else { // Previous trend was up
                    if (current.getClose() >= finalLB) {
                        st = finalLB;
                        isUptrend = true;
                    } else {
                        st = finalUB;
                        isUptrend = false;
                    }
                }
            }
            
            // Save bands and SuperTrend
            finalUpperBand.add(finalUB);
            finalLowerBand.add(finalLB);
            superTrend.add(st);
            
            // Determine the current trend based on the close and the SuperTrend value.
            // (Note: you might choose to use >= rather than > if you want to favor uptrend in case of equality.)
            String trend = current.getClose() > st ? "Uptrend" : "Downtrend";
            
            // Optionally update the DTO with the most recent values.
            // (Here we update it on every iteration; you may choose to return the final state.)
            superTrendDTO = new SuperTrendDTO(trend, atr, finalUB, finalLB, st);
            
            // Optionally print details for debugging
            System.out.println("Candle " + i + ": ATR = " + atr +
                               ", FinalUB = " + finalUB +
                               ", FinalLB = " + finalLB +
                               ", SuperTrend = " + st +
                               ", Trend = " + trend);
        }
        
        return superTrendDTO;
    }


}
