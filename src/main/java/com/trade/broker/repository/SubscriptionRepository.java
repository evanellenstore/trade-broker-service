package com.trade.broker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trade.broker.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByExchangeAndToken(String exchange, String token);

    List<Subscription> findByExchange(String exchange);

    void deleteByExchangeAndToken(String exchange, String token);

}
