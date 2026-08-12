package com.trade.broker.entity;

import java.sql.Timestamp;

import com.trade.broker.util.TRADEDateUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subscriptions", uniqueConstraints = @UniqueConstraint(columnNames = { "exchange", "token" }))
@Getter
@Setter
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exchange", nullable = false)
    private String exchange;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "UPD_TIMESTAMP")
    private Timestamp updTimestamp;

    @Column(name = "CREAT_TIMESTAMP", updatable = false)
    private Timestamp creatTimestamp;

    @PreUpdate
    public void updateTimeStamps() {
        this.updTimestamp = TRADEDateUtil.getCurrentJavaSqlTimestamp();
    }

    @PrePersist
    public void createTimeStamps() {
        this.creatTimestamp = TRADEDateUtil.getCurrentJavaSqlTimestamp();
        this.updTimestamp = TRADEDateUtil.getCurrentJavaSqlTimestamp();
    }

}
