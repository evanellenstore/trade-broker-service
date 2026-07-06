package com.trade.broker.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class StockChange {
	private String stockName;
	private String symboltoken;
	private double ltp;
	private double prevClose;
	private double changePercent;
	
	private double changeOIPercent;
	long currentOI;
	long previousOI;
	
	public StockChange() {
    }

	public StockChange(String stockName, double ltp, double prevClose, double changePercent, String symboltoken) {
		this.stockName = stockName;
		this.symboltoken = symboltoken;
		this.ltp = ltp;
		this.prevClose = prevClose;
		this.changePercent = changePercent;
	}

	public double getChangePercent() {
		return changePercent;
	}

	@Override
	public String toString() {
		return "StockChange [stockName="+stockName+ ", ltp=" + ltp + ", prevClose="
				+ prevClose + ", changePercent=" + changePercent + ", changeOIPercent=" + changeOIPercent
				+ ", currentOI=" + currentOI + ", previousOI=" + previousOI + "]";
	}

	
}
