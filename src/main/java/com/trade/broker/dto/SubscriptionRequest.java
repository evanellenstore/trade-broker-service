package com.trade.broker.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {
	
	private String exchange;
	private String subscriptionName;
	private Map<String, String> symbols;
	
}
