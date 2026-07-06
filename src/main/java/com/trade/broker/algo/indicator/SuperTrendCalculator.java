package com.trade.broker.algo.indicator;

import java.util.ArrayList;
import java.util.List;

public class SuperTrendCalculator {
    private final int atrPeriod;
    private final double multiplier;
    private final List<Candle> candles;
    

	private final List<Double> trValues;         // True Range values
    private final List<Double> atrList;          // ATR values
    private final List<Double> basicUpperList;   // Basic Upper Band values
    private final List<Double> basicLowerList;   // Basic Lower Band values
    private final List<Double> finalUpperList;   // Final Upper Band values
    private final List<Double> finalLowerList;   // Final Lower Band values
    private final List<Double> superTrendValues; // SuperTrend values
    private final List<Boolean> isUptrend;       // Trend direction (true = uptrend)

    public SuperTrendCalculator(int atrPeriod, double multiplier) {
        this.atrPeriod = atrPeriod;
        this.multiplier = multiplier;
        this.candles = new ArrayList<>();
        this.trValues = new ArrayList<>();
        this.atrList = new ArrayList<>();
        this.basicUpperList = new ArrayList<>();
        this.basicLowerList = new ArrayList<>();
        this.finalUpperList = new ArrayList<>();
        this.finalLowerList = new ArrayList<>();
        this.superTrendValues = new ArrayList<>();
        this.isUptrend = new ArrayList<>();
    }

    public void addCandle(Candle candle) {
        candles.add(candle);
        calculateSuperTrend();
    }

    private void calculateSuperTrend() {
        if (candles.isEmpty()) {
            return; // No data
        }

        // Get current candle and compute TR
        Candle currentCandle = candles.get(candles.size() - 1);
        double tr;
        if (candles.size() == 1) {
            tr = currentCandle.getHigh() - currentCandle.getLow();
        } else {
            Candle previousCandle = candles.get(candles.size() - 2);
            tr = Math.max(
                    currentCandle.getHigh() - currentCandle.getLow(),
                    Math.max(
                            Math.abs(currentCandle.getHigh() - previousCandle.getClose()),
                            Math.abs(currentCandle.getLow() - previousCandle.getClose())
                    )
            );
        }
        trValues.add(tr);

        // Only calculate if we have enough TR values (equal to atrPeriod)
        if (trValues.size() >= atrPeriod) {
            double atr = calculateATR();
            atrList.add(atr);

            // Calculate basic bands for current candle
            double hl2 = (currentCandle.getHigh() + currentCandle.getLow()) / 2.0;
            double basicUpper = hl2 + (multiplier * atr);
            double basicLower = hl2 - (multiplier * atr);
            basicUpperList.add(basicUpper);
            basicLowerList.add(basicLower);

            double finalUpper, finalLower, currentSuperTrend;
            boolean currentTrend;

            if (superTrendValues.isEmpty()) {
                // First SuperTrend calculation: initialize final bands to basic bands.
                finalUpper = basicUpper;
                finalLower = basicLower;
                // Many implementations choose to initialize the trend based on a condition.
                // For example, here we assume an uptrend if close is above hl2.
                currentTrend = currentCandle.getClose() >= hl2;
                currentSuperTrend = currentTrend ? finalLower : finalUpper;
            } else {
                // Retrieve previous final bands and previous candle
                double prevFinalUpper = finalUpperList.get(finalUpperList.size() - 1);
                double prevFinalLower = finalLowerList.get(finalLowerList.size() - 1);
                Candle previousCandle = candles.get(candles.size() - 2);

                // Update final upper band: if basicUpper < previous finalUpper OR previous close > previous finalUpper, then use basicUpper; otherwise, carry forward previous finalUpper.
                finalUpper = (basicUpper < prevFinalUpper || previousCandle.getClose() > prevFinalUpper)
                        ? basicUpper : prevFinalUpper;

                // Update final lower band: if basicLower > previous finalLower OR previous close < previous finalLower, then use basicLower; otherwise, carry forward previous finalLower.
                finalLower = (basicLower > prevFinalLower || previousCandle.getClose() < prevFinalLower)
                        ? basicLower : prevFinalLower;

                // Determine trend:
                // If previous SuperTrend was at the finalUpper band, then:
                //   if current close <= finalUpper, trend remains down; else, trend switches to up.
                // If previous SuperTrend was at the finalLower band, then:
                //   if current close >= finalLower, trend remains up; else, trend switches to down.
                double prevSuperTrend = superTrendValues.get(superTrendValues.size() - 1);
                if (prevSuperTrend == prevFinalUpper) {
                    currentTrend = currentCandle.getClose() > finalUpper;
                } else {
                    currentTrend = currentCandle.getClose() >= finalLower;
                }
                currentSuperTrend = currentTrend ? finalLower : finalUpper;
            }

            // Save final bands, SuperTrend, and trend direction
            finalUpperList.add(finalUpper);
            finalLowerList.add(finalLower);
            superTrendValues.add(currentSuperTrend);
            isUptrend.add(currentTrend);
        }
    }

    // ATR calculated as the simple average of the last 'atrPeriod' TR values.
    private double calculateATR() {
        int startIdx = trValues.size() - atrPeriod;
        double sum = 0;
        for (int i = startIdx; i < trValues.size(); i++) {
            sum += trValues.get(i);
        }
        return sum / atrPeriod;
    }

    // Returns the latest SuperTrend value; if not available, returns -1.
    public double getLatestSuperTrend() {
        if (superTrendValues.isEmpty()) return -1;
        return superTrendValues.get(superTrendValues.size() - 1);
    }

    // Returns true if the current trend is up.
    public boolean isUptrend() {
        if (isUptrend.isEmpty()) return false;
        return isUptrend.get(isUptrend.size() - 1);
    }
    
    
    public List<Candle> getCandles() {
		return candles;
	}
}
