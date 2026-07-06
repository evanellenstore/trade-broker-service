package com.trade.broker.entity;


import java.sql.Timestamp;

import com.trade.broker.util.DateSelection;
import com.trade.broker.util.TRADEDateUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "marketdata_historical")
@Setter
@Getter
public class MarketDataHistorical {

		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    
	    @Column(name = "trading_symbol", nullable = false)
	    private String tradingsymbol;
	    
	    @Column(name = "exchange", nullable = false)
	    private String exchange;
	    
	    @Column(name = "symbol_token", nullable = false)
	    private String symboltoken;
	    
	    
	    @Column(name = "market_data", nullable = false, length = 1500)
	    private String marketdata;
	   
	    
	    @Column(name="UPD_TIMESTAMP")
		private Timestamp updTimestamp;
	    
	    @Column(name="CREAT_TIMESTAMP", updatable = false)
		private Timestamp creatTimestamp;
	    
		@PreUpdate
		public void updateTimeStamps() {
		    this.updTimestamp = DateSelection.convertLocalDateToTimestamp();	    
		}

		@PrePersist
		public void createTimeStamps() {
		    this.creatTimestamp = DateSelection.convertLocalDateToTimestamp();
		    this.updTimestamp = DateSelection.convertLocalDateToTimestamp();
		}

		
		
	    

	    
	}
