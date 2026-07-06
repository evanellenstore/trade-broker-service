package com.trade.broker.algo.indicator.temp;

import org.apache.commons.lang3.tuple.Pair;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.AwesomeOscillatorIndicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

public class AwesomeIndicatorWithTrend extends CachedIndicator<Pair<Trend, Num>>
{
    private final AwesomeOscillatorIndicator awesomeOscillatorIndicator;

    public AwesomeIndicatorWithTrend(BarSeries series)
    {
        super(series);
        this.awesomeOscillatorIndicator = new AwesomeOscillatorIndicator(series);
    }

    @Override
    protected Pair<Trend, Num> calculate(int index)
    {
        Num previous = awesomeOscillatorIndicator.getValue(index - 1);
        Num current  = awesomeOscillatorIndicator.getValue(index);
        return Pair.of(
                previous.isLessThan(current)
                    ? Trend.UP
                    : Trend.DOWN
                , current);
    }
}