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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fno_stocks")
@Setter
@Getter
public class FNOStockDetail {

		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "stackname", nullable = false,unique = true)
	    private String stackname;
	    
	    @Column(name = "live_or_test", nullable = false)
	    private String liveortest;
	    
	    @Column(name = "exchange", nullable = false)
	    private String exchange;
	    
	    @Column(name = "trading_symbol", nullable = false, unique = true)
	    private String tradingsymbol;
	    
	    @Column(name = "symbol_token", nullable = false, unique = true)
	    private String symboltoken;
	    
	    @Column(name = "lot_size", nullable = false)
	    private long lotsize;
	    
	    @Column(name = "instrument_type", nullable = false)
	    private String instrumenttype;
	    
	    @Column(name="UPD_TIMESTAMP")
		private Timestamp updTimestamp;
	    
	    @Column(name="CREAT_TIMESTAMP", updatable = false)
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

		@Override
		public String toString() {
			return "FNOStockDetail [stackName=" + stackname + ", tradingsymbol=" + tradingsymbol + ", symboltoken="
					+ symboltoken + ", lotsize=" + lotsize + "]";
		}

		

		
		
	    

	    
	}
