package com.trade.broker.algo.indicator;

import org.json.JSONArray;

public class SimpleMovingAverage {
	
	/**
	 * 
	 * @param candles
	 * @param period
	 * @return
	 */
	public double calculateSMA(JSONArray candles, int period) {
		
		if (candles.length() < period) {
            System.out.println("Not enough data to calculate SMA");
            return -1;
        }
		
		
		if(candles!=null && candles.length()>8) 
			candles.remove(0);
		

		 double sma=0;
        for (int i = period - 1; i < candles.length(); i++) {
        	double sum = 0;
            for (int j = i - period + 1; j <= i; j++) {
                sum += candles.getJSONArray(j).getDouble(4); // Close price
            }
             sma = sum / period;
            System.out.println("SMA at " + candles.getJSONArray(i).getString(0) + " is " + sma);
           
        }
        
        return sma;
    }
	
	
	/**
	 * 
	 * @param ltp
	 * @param sma
	 * @param tradeEntryStock
	 */
	public boolean processCandle(JSONArray candleData, double sma, String gainerorlooser) {

		if (candleData == null || candleData.length() < 2) {
			System.out.println("Not enough candle data to retrieve the last two candles.");
			return false;
		}

		JSONArray secondCandle = candleData.getJSONArray(candleData.length() - 2);
		JSONArray firstCandle = candleData.getJSONArray(candleData.length() - 1);

		double firstCandleClose = 0;
		double secondCandleClose = 0;
		if (firstCandle != null) {
			firstCandleClose = firstCandle.getDouble(4);
		}

		if (secondCandle != null) {
			secondCandleClose = secondCandle.getDouble(4);
		}
		System.out.println("sma: " + sma + ", first Candle close :  " + firstCandle.getDouble(4)+ ", second Candle close: " + secondCandle.getDouble(4));

		if ("CE".equalsIgnoreCase(gainerorlooser)) {
			// candle first and candle second are closing below of the 8 sma for CE option
			
			if (firstCandleClose < sma && secondCandleClose < sma)
				return true;

		} else if ("PE".equalsIgnoreCase(gainerorlooser)) {
			// candle first and candle second are closing up of the 8 sma for PE option
			if (firstCandleClose > sma && secondCandleClose > sma)
				return true;

		}
		return false;

	}


	

}
