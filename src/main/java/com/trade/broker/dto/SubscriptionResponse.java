package com.trade.broker.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
	
	private String status;
	private String message;
	private List<String> subscribedSymbols;
	private String subscriptionId;
	private String subscriptionName;
	private java.sql.Timestamp createdAt;
	
}
