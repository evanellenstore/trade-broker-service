package com.trade.broker.entity;


import java.sql.Timestamp;

import com.trade.broker.util.DateSelection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "top_gainer_and_looser_stocks")
@Setter
@Getter
public class TopGainerAndLooserStocks {

		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
		
	    @Column(name = "apply_filter", nullable = false)
	    private String applyfilter;
	    
	    @Column(name = "exchange", nullable = false)
	    private String exchange;
	    
	    
	    @Column(name = "type", nullable = false)
	    private String type;
	    
	    @Column(name = "nifity50candletype", nullable = false)
	    private String nifity50candletype;
	    
	    @Column(name = "strategies", nullable = false)
	    private String strategies;
	    
	    @Lob
	    @Column(name = "json_data", columnDefinition = "LONGTEXT")
	    private String jsonData;
	   
	    
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
		    this.creatTimestamp =  DateSelection.convertLocalDateToTimestamp();
		    this.updTimestamp =  DateSelection.convertLocalDateToTimestamp();
		}

		
	    
	}
