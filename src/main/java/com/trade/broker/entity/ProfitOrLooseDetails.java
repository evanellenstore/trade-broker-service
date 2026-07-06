package com.trade.broker.entity;


import java.sql.Timestamp;

import com.trade.broker.util.DateSelection;

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
@Table(name = "profite_or_loose")
@Setter
@Getter
public class ProfitOrLooseDetails {

		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
		
		 @Column(name = "exchange", nullable = false)
		 private String exchange;

	    @Column(name = "stockname", nullable = false)
	    private String stockname;
	    
	    @Column(name = "stock_trading_symbol", nullable = false)
	    private String stocktradingsymbol;
	    
	    @Column(name = "stock_symbol_token", nullable = false)
	    private String stocksymboltoken;
	    
	    @Column(name = "order_id", nullable = false)
	    private String orderid;
	          
	    @Column(name = "buy_ltp")
	    private double buyltp;
	    
	    @Column(name = "sell_ltp")
	    private double sellltp;
	     
	    @Column(name = "gainer_or_looser")
	    private String gainerorlooser;
	    
	    @Column(name = "trade_status")
	    private String tradestatus;
	       
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
