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
import com.trade.broker.entity.Subscription;
import com.trade.broker.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class SubscriptionService {

	@Autowired
	private SmartApiLogin smartApiLogin;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	// Persisted subscriptions are stored in DB via SubscriptionRepository

	/**
	 * Subscribe to symbols for a given exchange
	 */
	public SubscriptionResponse subscribe(SubscriptionRequest request) throws WebSocketException {
		String exchange = request.getExchange();
		Map<String, String> symbols = request.getSymbols();

		List<String> tokenList = new ArrayList<>();
		Map<String, String> currentMetadata = new HashMap<>();

		if (symbols != null) {
			for (Map.Entry<String, String> entry : symbols.entrySet()) {
				String token = entry.getKey();
				String symbol = entry.getValue();
				if (token != null && !token.trim().isEmpty() && symbol != null && !symbol.trim().isEmpty()) {
					token = token.trim();
					symbol = symbol.trim();

					// Upsert: update existing or create new
					Subscription existing = subscriptionRepository.findByExchangeAndToken(exchange, token).orElse(null);
					if (existing != null) {
						if (!symbol.equals(existing.getSymbol())) {
							existing.setSymbol(symbol);
							subscriptionRepository.save(existing);
						}
					} else {
						Subscription sub = new Subscription();
						sub.setExchange(exchange);
						sub.setToken(token);
						sub.setSymbol(symbol);
						subscriptionRepository.save(sub);
					}

					tokenList.add(token);
					currentMetadata.put(token, symbol);
				}
			}
		}

		// Rebuild currentSymbols from DB for response
		List<Subscription> all = subscriptionRepository.findByExchange(exchange);
		Set<String> currentSymbols = new HashSet<>();
		for (Subscription s : all) {
			currentSymbols.add(s.getSymbol());
			currentMetadata.put(s.getToken(), s.getSymbol());
		}

		// subscribe to smart api stream
		smartApiLogin.subcribeToSmartStreamConnect(tokenList, exchange, currentMetadata);

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

		if (symbols != null) {
			for (String token : symbols.keySet()) {
				if (token == null || token.trim().isEmpty()) continue;
				subscriptionRepository.deleteByExchangeAndToken(exchange, token.trim());
			}
		}

		// Rebuild currentSymbols from DB for response
		List<Subscription> all = subscriptionRepository.findByExchange(exchange);
		Set<String> currentSymbols = new HashSet<>();
		for (Subscription s : all) {
			currentSymbols.add(s.getSymbol());
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
		List<Subscription> all = subscriptionRepository.findByExchange(exchange);
		Set<String> currentSymbols = new HashSet<>();
		Map<String, String> tokenMap = new HashMap<>();
		for (Subscription s : all) {
			currentSymbols.add(s.getSymbol());
			tokenMap.put(s.getToken(), s.getSymbol());
		}

		CurrentSubscriptionsResponse response = new CurrentSubscriptionsResponse();
		response.setExchange(exchange);
		response.setSymbols(new ArrayList<>(currentSymbols));
		response.setTokenMap(tokenMap);

		return response;
	}

}
