package com.trade.broker.algo.indicator;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.DecimalNum;

/**
 * TA4J-backed SuperTrend calculator. Mimics previous SuperTrendCalculator API
 * but uses TA4J ATR for the volatility component.
 */
public class Ta4jSuperTrendCalculator {

    private final int atrPeriod;
    private final double multiplier;
    private final BarSeries series = new BaseBarSeries();

    private final List<Double> finalUpperList = new ArrayList<>();
    private final List<Double> finalLowerList = new ArrayList<>();
    private final List<Double> superTrendValues = new ArrayList<>();
    private final List<Boolean> isUptrend = new ArrayList<>();

    public Ta4jSuperTrendCalculator(int atrPeriod, double multiplier) {
        this.atrPeriod = atrPeriod;
        this.multiplier = multiplier;
    }

    public void addCandle(Candle candle) {
        // parse candle date (expecting ISO/OffsetDateTime string)
        ZonedDateTime endTime;
        try {
            OffsetDateTime odt = OffsetDateTime.parse(candle.getDate());
            endTime = odt.toZonedDateTime();
        } catch (Exception e) {
            endTime = ZonedDateTime.now();
        }

        BaseBar bar = BaseBar.builder()
                .timePeriod(Duration.ofMinutes(5))
                .endTime(endTime)
                .openPrice(DecimalNum.valueOf(candle.getOpen()))
                .highPrice(DecimalNum.valueOf(candle.getHigh()))
                .lowPrice(DecimalNum.valueOf(candle.getLow()))
                .closePrice(DecimalNum.valueOf(candle.getClose()))
                .volume(DecimalNum.valueOf(candle.getVolume()))
                .build();

        series.addBar(bar);

        if (series.getBarCount() >= atrPeriod) {
            // use TA4J ATR
            ATRIndicator atrIndicator = new ATRIndicator(series, atrPeriod);
            int idx = series.getEndIndex();
            Num atrNum = atrIndicator.getValue(idx);
            double atr = atrNum.doubleValue();

            double high = series.getBar(idx).getHighPrice().doubleValue();
            double low = series.getBar(idx).getLowPrice().doubleValue();
            double close = series.getBar(idx).getClosePrice().doubleValue();

            double hl2 = (high + low) / 2.0;
            double basicUpper = hl2 + (multiplier * atr);
            double basicLower = hl2 - (multiplier * atr);

            double finalUpper;
            double finalLower;
            double currentSuperTrend;
            boolean currentTrend;

            if (superTrendValues.isEmpty()) {
                finalUpper = basicUpper;
                finalLower = basicLower;
                currentTrend = close >= hl2;
                currentSuperTrend = currentTrend ? finalLower : finalUpper;
            } else {
                double prevFinalUpper = finalUpperList.get(finalUpperList.size() - 1);
                double prevFinalLower = finalLowerList.get(finalLowerList.size() - 1);
                double prevClose = series.getBar(idx - 1).getClosePrice().doubleValue();

                finalUpper = (basicUpper < prevFinalUpper || prevClose > prevFinalUpper) ? basicUpper : prevFinalUpper;
                finalLower = (basicLower > prevFinalLower || prevClose < prevFinalLower) ? basicLower : prevFinalLower;

                double prevSuperTrend = superTrendValues.get(superTrendValues.size() - 1);
                if (Double.compare(prevSuperTrend, prevFinalUpper) == 0) {
                    currentTrend = close > finalUpper;
                } else {
                    currentTrend = close >= finalLower;
                }
                currentSuperTrend = currentTrend ? finalLower : finalUpper;
            }

            finalUpperList.add(finalUpper);
            finalLowerList.add(finalLower);
            superTrendValues.add(currentSuperTrend);
            isUptrend.add(currentTrend);
        }
    }

    public double getLatestSuperTrend() {
        if (superTrendValues.isEmpty()) return -1;
        return superTrendValues.get(superTrendValues.size() - 1);
    }

    public boolean isUptrend() {
        if (isUptrend.isEmpty()) return false;
        return isUptrend.get(isUptrend.size() - 1);
    }

    public List<Candle> getCandles() {
        List<Candle> list = new ArrayList<>();
        for (int i = 0; i < series.getBarCount(); i++) {
            BaseBar b = (BaseBar) series.getBar(i);
            String date = b.getEndTime().toString();
            double open = b.getOpenPrice().doubleValue();
            double high = b.getHighPrice().doubleValue();
            double low = b.getLowPrice().doubleValue();
            double close = b.getClosePrice().doubleValue();
            int volume = (int) b.getVolume().doubleValue();
            list.add(new Candle(date, open, high, low, close, volume));
        }
        return list;
    }
}
