package com.trade.broker.algo.indicator;

import lombok.Getter;

@Getter
public class Candle {
	private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private int volume;

    public Candle(String date,double open, double high, double low, double close,int volume) {
    	this.date=date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume=volume;
    }

	@Override
	public String toString() {
		return "Candle [date=" + date + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close
				+ ", volume=" + volume + "]";
	}

	
    
    
    
    

    
}
