package com.trade.broker.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentSubscriptionsResponse {
	
	private String exchange;
	private List<String> symbols;
	private Map<String, String> tokenMap;
	private List<SubscriptionBatchResponse> subscriptions;
	
}
