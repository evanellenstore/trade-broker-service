package com.trade.broker.util;



public enum TradeStatus {
    OPEN("Open"),
    PENDING("Pending"),
    INPROGESS("Inprogress"),
    EXECUTED("Executed"),
    CANCELLED("Cancelled"),
    DISQUALIFY("Disqualified"),
    CLOSE("Close");

    private final String value;

    TradeStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


