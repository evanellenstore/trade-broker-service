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
@Table(name = "user_tokens")
@Setter
@Getter
public class DBTokenDetail {

		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "client_id", nullable = false)
	    private String clientId;
	    
	    @Column(name = "app_name", nullable = false, unique = true)
	    private String appName;
	    
	    @Column(name = "access_token", nullable = false, length = 1500)
	    private String accesstoken;

	    @Column(name = "refresh_token", nullable = false, length = 1500)
	    private String refreshtoken;
	    
	    @Column(name = "feed_token", nullable = true, length = 1500)
	    private String feedtoken;
	    
	    @Column(name = "token_expried")
	    private String tokenexpried;
	    
    	@Column(name = "live_or_backtest")
    	private String liveOrBacktest;
	    
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
			return "DBTokenDetail [id=" + id + ", clientId=" + clientId + ", appName=" + appName + ", accesstoken="
				+ accesstoken + ", refreshtoken=" + refreshtoken + ", feedtoken=" + feedtoken + ", tokenexpried="
				+ tokenexpried + ", liveOrBacktest=" + liveOrBacktest + ", updTimestamp=" + updTimestamp + ", creatTimestamp=" + creatTimestamp + "";
		}
}
