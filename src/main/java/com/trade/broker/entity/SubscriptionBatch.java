package com.trade.broker.entity;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.trade.broker.util.TRADEDateUtil;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subscription_batches")
@Getter
@Setter
public class SubscriptionBatch {

    @Id
    @Column(name = "subscription_id", nullable = false, updatable = false, length = 36)
    private String subscriptionId;

    @Column(name = "subscription_name", nullable = false)
    private String subscriptionName;

    @Column(name = "exchange", nullable = false)
    private String exchange;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @ElementCollection
    @CollectionTable(name = "subscription_batch_tokens", joinColumns = @JoinColumn(name = "subscription_id"))
    @MapKeyColumn(name = "token")
    @Column(name = "symbol", nullable = false)
    private Map<String, String> symbols = new HashMap<>();

    @PrePersist
    public void initialize() {
        if (subscriptionId == null || subscriptionId.isBlank()) {
            subscriptionId = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = TRADEDateUtil.getCurrentJavaSqlTimestamp();
        }
    }
}