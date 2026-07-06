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
@Table(name = "trade_entry_stocks")
@Setter
@Getter
public class TradeEntryStock {

		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
		
		 @Column(name = "exchange", nullable = false)
		 private String exchange;

	    @Column(name = "orginal_tradingsymbol", nullable = false)
	    private String orginaltradingsymbol;
	    
	    @Column(name = "stock_trading_symbol", nullable = false)
	    private String stocktradingsymbol;
	    
	    @Column(name = "stock_symbol_token", nullable = false)
	    private String stocksymboltoken;
	    
	    @Column(name = "option_trading_symbol")
	    private String optiontradingsymbol;
	    
	    @Column(name = "option_symbol_token")
	    private String optionsymboltoken;
	    
	    @Column(name="option_expiry")
		private String optionexpiry;
	    
	    @Column(name = "option_orginaltradingsymbol")
	    private String optionorginaltradingsymbol;
	        
	    @Column(name="option_strike_price")
		private int optionstrikeprice;
 	       
	    @Column(name = "high_point")
	    private double highpoint;
	    
	    @Column(name = "high_breakup")
	    private String highbreakup;
	    
	    @Column(name = "high_breakdown")
	    private String highbreakdown;
	    
	    @Column(name = "low_point")
	    private double lowpoint;
	    
	    @Column(name = "low_breakdown")
	    private String lowbreakdown;
	    
	    @Column(name = "low_breakup")
	    private String lowbreakup;
	    
	    @Column(name = "gainer_or_looser")
	    private String gainerorlooser;
	    
	    @Column(name = "trade_status")
	    private String tradestatus;
	    
	    @Column(name = "order_id")
	    private String oderid;
	    	    
	    @Column(name = "buy_ltp")
	    private double buyltp;
	    
	    @Column(name="BUY_TIMESTAMP")
		private Timestamp buyTimestamp;
	    	    
	    @Column(name = "sell_ltp")
	    private double sellltp;
	    
	    @Column(name="sell_TIMESTAMP")
		private Timestamp sellTimestamp;
	    
	    @Column(name = "buy_lot_quantity")
	    private int buylotquantity;
	    
	    @Column(name = "sell_lot_quantity")
	    private int selllotquantity;
	          
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

		@Override
		public String toString() {
			return "TradeEntryStock [exchange=" + exchange + ", orginaltradingsymbol=" + orginaltradingsymbol
					+ ", stocktradingsymbol=" + stocktradingsymbol + ", stocksymboltoken=" + stocksymboltoken
					+ ", optiontradingsymbol=" + optiontradingsymbol + ", optionsymboltoken=" + optionsymboltoken
					+ ", optionexpiry=" + optionexpiry + ", optionorginaltradingsymbol=" + optionorginaltradingsymbol
					+ ", optionstrikeprice=" + optionstrikeprice + ", highpoint=" + highpoint + ", highbreakup="
					+ highbreakup + ", highbreakdown=" + highbreakdown + ", lowpoint=" + lowpoint + ", lowbreakdown="
					+ lowbreakdown + ", lowbreakup=" + lowbreakup + ", gainerorlooser=" + gainerorlooser
					+ ", tradestatus=" + tradestatus + ", oderid=" + oderid + ", buyltp=" + buyltp + ", buyTimestamp="
					+ buyTimestamp + ", sellltp=" + sellltp + ", sellTimestamp=" + sellTimestamp + ", updTimestamp="
					+ updTimestamp + ", creatTimestamp=" + creatTimestamp + "]";
		}

		

		
		
	    

	    
	}
