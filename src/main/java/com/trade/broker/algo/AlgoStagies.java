package com.trade.broker.algo;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trade.broker.constant.TRADEConstants;
import com.trade.broker.exception.TradeScheduleBusinessException;
import com.trade.broker.service.MarketDataHistoricalService;

@Component
public class AlgoStagies {

	@Autowired
	private IntraDayAlgoStagies intraDayAlgoStagies;
	
	@Autowired
	private MarketDataHistoricalService marketDataHistoricalService;
	
	

	public void applyAlgoStagies(Date lastSuccessRun, String jobName) throws TradeScheduleBusinessException {

		switch (jobName) {
		case TRADEConstants.JOB_CODE_INTRADAY_TOPGAINER_TOPLOOSER:
			intraDayAlgoStagies.excuteStagies(lastSuccessRun);
			break;
		case TRADEConstants.JOB_CODE_SAVE_MARKET_DATA:
			marketDataHistoricalService.saveMarketData(lastSuccessRun);
			break;
		case TRADEConstants.JOB_CODE_NIFITY50_CANDLE_DATA:
			intraDayAlgoStagies.excuteNifity50CandleData(lastSuccessRun);
			break;
		case TRADEConstants.JOB_CODE_TAKE_ENTRY_IN_STOCK_OPTIONS:
			intraDayAlgoStagies.excuteTakeEntryInStockOptions(lastSuccessRun);
			break;	
			
			
			
			

		default:
			System.out.println("Invalid day");
		}

	}

}
