package com.trade.broker.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neovisionaries.ws.client.WebSocketException;
import com.trade.broker.algo.SmartApiLogin;
import com.trade.broker.dto.CurrentSubscriptionsResponse;
import com.trade.broker.dto.SubscriptionRequest;
import com.trade.broker.dto.SubscriptionResponse;

@Service
public class SubscriptionService {

	@Autowired
	private SmartApiLogin smartApiLogin;

	// In-memory storage for symbol names by exchange and token
	private Map<String, Set<String>> subscriptions = new HashMap<>();
	private Map<String, Map<String, String>> subscriptionMetadata = new HashMap<>();

	/**
	 * Subscribe to symbols for a given exchange
	 */
	public SubscriptionResponse subscribe(SubscriptionRequest request) throws WebSocketException {
		String exchange = request.getExchange();
		Map<String, String> symbols = request.getSymbols();

		Set<String> currentSymbols = subscriptions.getOrDefault(exchange, new HashSet<>());
		Map<String, String> currentMetadata = subscriptionMetadata.getOrDefault(exchange, new HashMap<>());
		List<String> tokenList = new ArrayList<>();

		if (symbols != null) {
			for (Map.Entry<String, String> entry : symbols.entrySet()) {
				String token = entry.getKey();
				String symbol = entry.getValue();
				if (token != null && !token.trim().isEmpty() && symbol != null && !symbol.trim().isEmpty()) {
					tokenList.add(token.trim());
					currentSymbols.add(symbol.trim());
					currentMetadata.put(token.trim(), symbol.trim());
				}
			}
		}

		smartApiLogin.subcribeToSmartStreamConnect(tokenList, exchange, currentMetadata);
		subscriptions.put(exchange, currentSymbols);
		subscriptionMetadata.put(exchange, currentMetadata);

		SubscriptionResponse response = new SubscriptionResponse();
		response.setStatus("SUCCESS");
		response.setMessage("Subscribed successfully");
		response.setSubscribedSymbols(new ArrayList<>(currentSymbols));

		return response;
	}

	/**
	 * Unsubscribe from symbols for a given exchange
	 */
	public SubscriptionResponse unsubscribe(SubscriptionRequest request) throws WebSocketException {
		String exchange = request.getExchange();
		Map<String, String> symbols = request.getSymbols();

		Set<String> currentSymbols = subscriptions.getOrDefault(exchange, new HashSet<>());
		Map<String, String> currentMetadata = subscriptionMetadata.getOrDefault(exchange, new HashMap<>());

		if (symbols != null) {
			for (String token : symbols.keySet()) {
				String symbol = currentMetadata.remove(token);
				if (symbol != null) {
					currentSymbols.remove(symbol);
				}
			}
		}

		if (currentSymbols.isEmpty()) {
			subscriptions.remove(exchange);
			subscriptionMetadata.remove(exchange);
		} else {
			subscriptions.put(exchange, currentSymbols);
			subscriptionMetadata.put(exchange, currentMetadata);
		}

		SubscriptionResponse response = new SubscriptionResponse();
		response.setStatus("SUCCESS");
		response.setMessage("Unsubscribed successfully");
		response.setSubscribedSymbols(new ArrayList<>(currentSymbols));

		return response;
	}

	/**
	 * Get current subscriptions for a given exchange
	 */
	public CurrentSubscriptionsResponse getCurrentSubscriptions(String exchange) {
		Set<String> currentSymbols = subscriptions.getOrDefault(exchange, new HashSet<>());

		CurrentSubscriptionsResponse response = new CurrentSubscriptionsResponse();
		response.setExchange(exchange);
		response.setSymbols(new ArrayList<>(currentSymbols));

		return response;
	}

}
