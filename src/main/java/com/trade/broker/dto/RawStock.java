package com.trade.broker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawStock {
	
	private String name;
	private String exch;
	private String lotsize;
	private String symbol;
	private String instrumenttype;
	
	public RawStock() {
		
	}
	
	public RawStock(String name, String exch, String lotsize, String symbol, String instrumenttype) {
		this.name = name;
		this.exch = exch;
		this.lotsize = lotsize;
		this.symbol = symbol;
		this.instrumenttype = instrumenttype;
	}
	
	
	

}
