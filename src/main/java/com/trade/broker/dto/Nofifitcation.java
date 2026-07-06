package com.trade.broker.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class Nofifitcation {
	
	private List<StockChange> dataList;
	private String candleType;
	private String message;
	
	public Nofifitcation() {
		
	}
	
	public Nofifitcation(List<StockChange> dataList, String candleType, String message) {
		this.dataList = dataList;
		this.candleType = candleType;
		this.message = message;
	}
	
	

}
