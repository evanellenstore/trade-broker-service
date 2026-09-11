package com.trade.broker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trade.broker.entity.SubscriptionBatch;

public interface SubscriptionBatchRepository extends JpaRepository<SubscriptionBatch, String> {
    List<SubscriptionBatch> findByExchangeOrderByCreatedAtDesc(String exchange);
}