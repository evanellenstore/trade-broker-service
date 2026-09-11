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
import com.trade.broker.dto.SubscriptionBatchResponse;
import com.trade.broker.dto.SubscriptionRequest;
import com.trade.broker.dto.SubscriptionResponse;
import com.trade.broker.entity.Subscription;
import com.trade.broker.entity.SubscriptionBatch;
import com.trade.broker.repository.SubscriptionRepository;
import com.trade.broker.repository.SubscriptionBatchRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class SubscriptionService {

	@Autowired
	private SmartApiLogin smartApiLogin;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private SubscriptionBatchRepository subscriptionBatchRepository;

	// Persisted subscriptions are stored in DB via SubscriptionRepository

	/**
	 * Subscribe to symbols for a given exchange
	 */
	public SubscriptionResponse subscribe(SubscriptionRequest request) throws WebSocketException {
		String exchange = request.getExchange();
		Map<String, String> symbols = request.getSymbols();
		Map<String, String> requestedSymbols = new HashMap<>();

		List<String> tokenList = new ArrayList<>();
		Map<String, String> currentMetadata = new HashMap<>();

		if (symbols != null) {
			for (Map.Entry<String, String> entry : symbols.entrySet()) {
				String token = entry.getKey();
				String symbol = entry.getValue();
				if (token != null && !token.trim().isEmpty() && symbol != null && !symbol.trim().isEmpty()) {
					token = token.trim();
					symbol = symbol.trim();
					requestedSymbols.put(token, symbol);

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

		if (requestedSymbols.isEmpty()) {
			throw new IllegalArgumentException("At least one token and symbol are required");
		}

		SubscriptionBatch batch = new SubscriptionBatch();
		batch.setSubscriptionName(request.getSubscriptionName() == null || request.getSubscriptionName().isBlank()
				? "Market subscription" : request.getSubscriptionName().trim());
		batch.setExchange(exchange);
		batch.setSymbols(requestedSymbols);
		subscriptionBatchRepository.save(batch);

		// Keep all currently subscribed tokens active so a later batch can add stocks
		// without replacing an earlier subscription.
		Set<String> currentSymbols = new HashSet<>(requestedSymbols.values());
		List<Subscription> all = subscriptionRepository.findByExchange(exchange);
		Map<String, String> activeIds = new HashMap<>();
		Map<String, String> activeNames = new HashMap<>();
		for (SubscriptionBatch savedBatch : subscriptionBatchRepository.findByExchangeOrderByCreatedAtDesc(exchange)) {
			for (String token : savedBatch.getSymbols().keySet()) {
				if (all.stream().anyMatch(subscription -> subscription.getToken().equals(token)
						&& !activeIds.containsKey(token))) {
					activeIds.put(token, savedBatch.getSubscriptionId());
					activeNames.put(token, savedBatch.getSubscriptionName());
				}
			}
		}
		List<String> activeTokens = all.stream()
				.map(Subscription::getToken)
				.filter(activeIds::containsKey)
				.toList();
		tokenList = activeTokens;
		currentSymbols.clear();
		currentMetadata.keySet().retainAll(activeTokens);
		for (Subscription subscription : all) {
			if (activeIds.containsKey(subscription.getToken())) {
				currentSymbols.add(subscription.getSymbol());
				currentMetadata.put(subscription.getToken(), subscription.getSymbol());
			}
		}
		smartApiLogin.subcribeToSmartStreamConnect(tokenList, exchange, currentMetadata, activeIds, activeNames);

		SubscriptionResponse response = new SubscriptionResponse();
		response.setStatus("SUCCESS");
		response.setMessage("Subscribed successfully");
		response.setSubscribedSymbols(new ArrayList<>(currentSymbols));
		response.setSubscriptionId(batch.getSubscriptionId());
		response.setSubscriptionName(batch.getSubscriptionName());
		response.setCreatedAt(batch.getCreatedAt());

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

		refreshStream(exchange);
		Set<String> currentSymbols = new HashSet<>();
		for (Subscription subscription : subscriptionRepository.findByExchange(exchange)) {
			currentSymbols.add(subscription.getSymbol());
		}

		SubscriptionResponse response = new SubscriptionResponse();
		response.setStatus("SUCCESS");
		response.setMessage("Unsubscribed successfully");
		response.setSubscribedSymbols(new ArrayList<>(currentSymbols));

		return response;
	}

	public SubscriptionResponse start(String subscriptionId) throws WebSocketException {
		SubscriptionBatch batch = subscriptionBatchRepository.findById(subscriptionId)
				.orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionId));
		refreshStream(batch.getExchange());
		SubscriptionResponse response = new SubscriptionResponse();
		response.setStatus("SUCCESS");
		response.setMessage("Subscription started");
		response.setSubscriptionId(batch.getSubscriptionId());
		response.setSubscriptionName(batch.getSubscriptionName());
		response.setCreatedAt(batch.getCreatedAt());
		return response;
	}

	public SubscriptionResponse update(String subscriptionId, SubscriptionRequest request) throws WebSocketException {
		SubscriptionBatch batch = subscriptionBatchRepository.findById(subscriptionId)
				.orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionId));
		if (request.getSubscriptionName() == null || request.getSubscriptionName().isBlank()
				|| request.getSymbols() == null || request.getSymbols().isEmpty()) {
			throw new IllegalArgumentException("Subscription name and at least one stock are required");
		}
		Map<String, String> updatedSymbols = normalizeSymbols(request.getSymbols());
		batch.setSubscriptionName(request.getSubscriptionName().trim());
		batch.setSymbols(updatedSymbols);
		subscriptionBatchRepository.save(batch);
		for (Map.Entry<String, String> entry : updatedSymbols.entrySet()) {
			Subscription subscription = subscriptionRepository.findByExchangeAndToken(batch.getExchange(), entry.getKey())
					.orElseGet(Subscription::new);
			subscription.setExchange(batch.getExchange());
			subscription.setToken(entry.getKey());
			subscription.setSymbol(entry.getValue());
			subscriptionRepository.save(subscription);
		}
		removeOrphanedTokens(batch.getExchange());
		refreshStream(batch.getExchange());
		return start(subscriptionId);
	}

	public SubscriptionResponse deleteBatch(String subscriptionId) throws WebSocketException {
		SubscriptionBatch batch = subscriptionBatchRepository.findById(subscriptionId)
				.orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionId));
		String exchange = batch.getExchange();
		subscriptionBatchRepository.delete(batch);
		removeOrphanedTokens(exchange);
		refreshStream(exchange);
		SubscriptionResponse response = new SubscriptionResponse();
		response.setStatus("SUCCESS");
		response.setMessage("Subscription deleted");
		response.setSubscriptionId(subscriptionId);
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
		response.setSubscriptions(subscriptionBatchRepository.findByExchangeOrderByCreatedAtDesc(exchange).stream()
				.map(batch -> new SubscriptionBatchResponse(batch.getSubscriptionId(), batch.getSubscriptionName(),
						batch.getExchange(), batch.getCreatedAt(), batch.getSymbols(), true))
				.toList());

		return response;
	}

	private Map<String, String> normalizeSymbols(Map<String, String> symbols) {
		Map<String, String> normalized = new HashMap<>();
		for (Map.Entry<String, String> entry : symbols.entrySet()) {
			if (entry.getKey() != null && !entry.getKey().isBlank()
					&& entry.getValue() != null && !entry.getValue().isBlank()) {
				normalized.put(entry.getKey().trim(), entry.getValue().trim());
			}
		}
		if (normalized.isEmpty()) {
			throw new IllegalArgumentException("At least one valid stock is required");
		}
		return normalized;
	}

	private void removeOrphanedTokens(String exchange) {
		Set<String> referencedTokens = new HashSet<>();
		for (SubscriptionBatch batch : subscriptionBatchRepository.findByExchangeOrderByCreatedAtDesc(exchange)) {
			referencedTokens.addAll(batch.getSymbols().keySet());
		}
		for (Subscription subscription : subscriptionRepository.findByExchange(exchange)) {
			if (!referencedTokens.contains(subscription.getToken())) {
				subscriptionRepository.delete(subscription);
			}
		}
	}

	private void refreshStream(String exchange) throws WebSocketException {
		List<Subscription> all = subscriptionRepository.findByExchange(exchange);
		Map<String, String> activeMetadata = new HashMap<>();
		Map<String, String> activeIds = new HashMap<>();
		Map<String, String> activeNames = new HashMap<>();
		for (SubscriptionBatch batch : subscriptionBatchRepository.findByExchangeOrderByCreatedAtDesc(exchange)) {
			for (Map.Entry<String, String> entry : batch.getSymbols().entrySet()) {
				if (!activeIds.containsKey(entry.getKey())) {
					activeIds.put(entry.getKey(), batch.getSubscriptionId());
					activeNames.put(entry.getKey(), batch.getSubscriptionName());
				}
			}
		}
		List<String> activeTokens = all.stream().map(Subscription::getToken).filter(activeIds::containsKey).toList();
		for (Subscription subscription : all) {
			if (activeIds.containsKey(subscription.getToken())) {
				activeMetadata.put(subscription.getToken(), subscription.getSymbol());
			}
		}
		smartApiLogin.subcribeToSmartStreamConnect(activeTokens, exchange, activeMetadata, activeIds, activeNames);
	}

}
