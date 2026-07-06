package com.trade.broker.util;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SymbolOptionData {
	
	

	private final String underlying;
	    private final Date expiry;
	    private final int strike;
	    private final String optionType;

	    public SymbolOptionData(String underlying, Date expiry, int strike, String optionType) {
	        this.underlying = underlying;
	        this.expiry = expiry;
	        this.strike = strike;
	        this.optionType = optionType;
	    }
	    
	    
	    
	    @Override
		public String toString() {
			return "SymbolOptionData [underlying=" + underlying + ", expiry=" + expiry + ", strike=" + strike
					+ ", optionType=" + optionType + "]";
		}

}
