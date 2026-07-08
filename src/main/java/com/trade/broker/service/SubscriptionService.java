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

	// In-memory storage for subscriptions by exchange
	private Map<String, Set<String>> subscriptions = new HashMap<>();

	/**
	 * Subscribe to symbols for a given exchange
	 */
	public SubscriptionResponse subscribe(SubscriptionRequest request) throws WebSocketException {
		String exchange = request.getExchange();
		Map<String, String> symbols = request.getSymbols();

		// Get or create set of symbols for the exchange
		Set<String> currentSymbols = subscriptions.getOrDefault(exchange, new HashSet<>());
		List<String> tokenList = new ArrayList<>();
		if (symbols != null) {
			for (Map.Entry<String, String> entry : symbols.entrySet()) {
				tokenList.add(entry.getKey());
				currentSymbols.add(entry.getValue());
			}
		}

		// Subscribe to new symbols via SmartApiLogin using token values
		smartApiLogin.subcribeToSmartStreamConnect(tokenList, exchange);
		subscriptions.put(exchange, currentSymbols);

		// Create response
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

		// Get current symbols for the exchange
		Set<String> currentSymbols = subscriptions.getOrDefault(exchange, new HashSet<>());

		if (symbols != null) {
			for (String symbol : symbols.values()) {
				currentSymbols.remove(symbol);
			}
		}

		if (currentSymbols.isEmpty()) {
			subscriptions.remove(exchange);
		} else {
			subscriptions.put(exchange, currentSymbols);
		}

		// Create response
		SubscriptionResponse response = new SubscriptionResponse();
		response.setStatus("SUCCESS");
		response.setMessage("Unsubscribed successfully");
		response.setSubscribedSymbols(null);

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
