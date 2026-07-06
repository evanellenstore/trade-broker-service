package com.trade.broker.util;

public enum INTERVAL {
	
	MINUTE_1("ONE_MINUTE"),
	MINUTE_3("THREE_MINUTE"),
	MINUTE_5("FIVE_MINUTE"),
	MINUTE_10("TEN_MINUTE"),
	MINUTE_15("FIFTEEN_MINUTE"),
	MINUTE_30("THIRTY_MINUTE"),
	HOUR_1("ONE_HOUR"),
	DAY_1("ONE_DAY"),;

	    private final String value;

	    INTERVAL(String value) {
	        this.value = value;
	    }

	    public String getValue() {
	        return value;
	    }

}
