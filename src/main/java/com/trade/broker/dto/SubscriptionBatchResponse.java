package com.trade.broker.dto;

import java.sql.Timestamp;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionBatchResponse {
    private String subscriptionId;
    private String subscriptionName;
    private String exchange;
    private Timestamp createdAt;
    private Map<String, String> symbols;
    private boolean active;
}