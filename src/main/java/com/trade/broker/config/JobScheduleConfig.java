package com.trade.broker.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.trade.broker.constant.TRADEConstants;
import com.trade.broker.entity.TJobScheduleEO;
import com.trade.broker.exception.TradeScheduleBusinessException;
import com.trade.broker.repository.ITradeJobScheduleRepository;


@Configuration
@ComponentScan("com.amresh.trade.*")
public class JobScheduleConfig {
	
	@Autowired
	private ITradeJobScheduleRepository tradePldsJobScheduleRepsitory;
	
	
	@Bean
	public String getTGTLJobSchedule() throws TradeScheduleBusinessException {
		String pattern = "0 20 09 * * *";
		TJobScheduleEO tJobScheduleEO = tradePldsJobScheduleRepsitory.findByJobCode(TRADEConstants.JOB_CODE_INTRADAY_TOPGAINER_TOPLOOSER);
		if (tJobScheduleEO != null) {
			pattern = tJobScheduleEO.getSchedulePattern();
		}
		System.err.println(" JOB_CODE_INTRADAY_TOPGAINER_TOPLOOSER Job Schedule " +" pattern "+pattern);
		
		return pattern;
	}
	
	
	@Bean
	public String getSMDJobSchedule() throws TradeScheduleBusinessException {
		String pattern = "0 30 11 * * *";
		TJobScheduleEO tJobScheduleEO = tradePldsJobScheduleRepsitory.findByJobCode(TRADEConstants.JOB_CODE_SAVE_MARKET_DATA);
		if (tJobScheduleEO != null) {
			pattern = tJobScheduleEO.getSchedulePattern();
		}
		System.err.println("JOB_CODE_SAVE_MARKET_DATA Job Schedule  pattern "+pattern );
		return pattern;
	}
	
	
	@Bean
	public String getNCDJobSchedule() throws TradeScheduleBusinessException {
		String pattern = "0 25 09 * * *";
		
		TJobScheduleEO tJobScheduleEO = tradePldsJobScheduleRepsitory.findByJobCode(TRADEConstants.JOB_CODE_NIFITY50_CANDLE_DATA);
		if (tJobScheduleEO != null) {
			pattern = tJobScheduleEO.getSchedulePattern();
		}
		System.err.println("JOB_CODE_NIFITY50_CANDLE_DATA Job Schedule pattern "+pattern );
		return pattern;
	}
	
	
	@Bean
	public String getEntryJobSchedule() throws TradeScheduleBusinessException {
		String pattern = "0 26 09 * * *";
		
		TJobScheduleEO tJobScheduleEO = tradePldsJobScheduleRepsitory.findByJobCode(TRADEConstants.JOB_CODE_TAKE_ENTRY_IN_STOCK_OPTIONS);
		if (tJobScheduleEO != null) {
			pattern = tJobScheduleEO.getSchedulePattern();
		}
		System.err.println("JOB_CODE_TAKE_ENTRY_IN_STOCK_OPTIONS Job Schedule  pattern "+pattern );
		return pattern;
	}
	
	
	
}

