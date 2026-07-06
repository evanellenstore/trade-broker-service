package com.trade.broker.algo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.broker.config.DynamicScheduledService;
import com.trade.broker.constant.TRADEConstants;
import com.trade.broker.domain.IntraDayAlgoStagiesHelper;
import com.trade.broker.dto.Nofifitcation;
import com.trade.broker.entity.FNOStockDetail;
import com.trade.broker.entity.TopGainerAndLooserStocks;
import com.trade.broker.entity.TradeEntryStock;
import com.trade.broker.exception.TradeScheduleBusinessException;
import com.trade.broker.service.FNOStockService;
import com.trade.broker.service.TopGainerAndLooserStockService;
import com.trade.broker.util.DateSelection;
import com.trade.broker.util.TRADEDateUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IntraDayAlgoStagies {

	@Autowired
	private FNOStockService fnoStockService;

	@Autowired
	private TopGainerAndLooserStockService topGainerAndLooserStockService;

	@Autowired
	private IntraDayAlgoStagiesHelper intraDayAlgoStagiesHelper;
	
	@Autowired
	private DynamicScheduledService dynamicScheduledService;
	
	
	
	@Value("${nifity50.candle.from.time.hour}")
	private int nifity50_candle_from_time_hour;
	
	@Value("${nifity50.candle.from.time.minute}")
	private int nifity50_candle_from_time_minute;
	
	@Value("${nifity50.candle.to.time.hour}")
	private int nifity50_candle_to_time_hour;
	
	@Value("${nifity50.candle.to.time.minute}")
	private int nifity50_candle_to_time_minute;
	
	@Value("${stock.entry.to.time.hour}")
	private int stock_entry_to_time_hour;
	
	@Value("${stock.entry.to.time.minute}")
	private int stock_entry_to_time_minute;
	
	@Value("${init.cron.expression}")
	private String init_cron_expression;
	
	
	


	/**
	 * 
	 * @param lastSuccessRun
	 */
	public void excuteStagies(Date lastSuccessRun) {
		try {
			Map<String, Nofifitcation> step1Notification = new HashMap<>();
			// STEP 1
			// AT 09:20
			// CUSTOM LOGIC FOR CALCULATE TOPGAINER AND TOPLOOSER WHICH IS COME UNDER FUTURE
			List<FNOStockDetail> fnoStockDetailList = fnoStockService.findByExchange(TRADEConstants.EXCHANGE);

			ObjectMapper objectMapper = new ObjectMapper();

			intraDayAlgoStagiesHelper.getTopGainerAndLooser(fnoStockDetailList);
			String top20GainerMessage = "Top 20 Gainer";
			String top20LooserMessage = "Top 20 Looser";
			intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_GAINER, TRADEConstants.TOP20GAINER, "st1",
					top20GainerMessage, step1Notification);
			intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_LOOSER, TRADEConstants.TOP20LOOSER, "st2",
					top20LooserMessage, step1Notification);

			// STEP 2
			// CHECK SHOUL BE MOVE MORE THAN 2%
			intraDayAlgoStagiesHelper.applyFilterMoreThan2percentChangesOnLTPToGainer(objectMapper);
			intraDayAlgoStagiesHelper.applyFilterMoreThan2percentChangesOnLTPToLooser(objectMapper);
			String apply2perChangeGainerMessage = "Filter ltp 2% changes Gainer";
			String apply2perChangeLooserMessage = "Filter ltp 2% changes Looser";

			intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_GAINER,
					TRADEConstants.MORE_THAN_2_PERCENT_ON_LTP_GAINER, "st3", apply2perChangeGainerMessage,
					step1Notification);
			intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_LOOSER,
					TRADEConstants.MORE_THAN_2_PERCENT_ON_LTP_LOOSER, "st4", apply2perChangeLooserMessage,
					step1Notification);

			// STEP 3
			// CHECK OPEN INTEREST(OI) DATA MOVE MORE THAN 7 PERCENT

			// this.getOpenInterestGainerAndLooser(topGainersMoreThan2PercentMoveList,topLosersMoreThan2PercentMoveList);

			intraDayAlgoStagiesHelper.applyOpenInterestOnGainerAndLooser(objectMapper);

			String apply2perChangeGainerCalOIMessage = "Calculate OI on ltp 2% changes Gainer";
			String apply2perChangeLooserCalOIMessage = "Calculate OI on ltp 2% changes Looser";

			intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_GAINER,
					TRADEConstants.MORE_THAN_2_PERCENT_ON_LTP_GAINER, "st5", apply2perChangeGainerCalOIMessage,
					step1Notification);
			intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_LOOSER,
					TRADEConstants.MORE_THAN_2_PERCENT_ON_LTP_LOOSER, "st6", apply2perChangeLooserCalOIMessage,
					step1Notification);

			// APPLY FILTER MORE THAN 7 PERCENT OI
			intraDayAlgoStagiesHelper.applyFilterMoreThan7percentChangesOIToGainerAndLooser(objectMapper);
			String apply7perChangeGainerOIMessage = "Filter OI 7% changes Gainer";
			String apply7perChangeLooserOIMessage = "Filter OI 7% changes Looser";

			intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_GAINER,
					TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_GAINER, "st7", apply7perChangeGainerOIMessage,
					step1Notification);
			intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_LOOSER,
					TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_LOOSER, "st8", apply7perChangeLooserOIMessage,
					step1Notification);

			if(step1Notification!=null && step1Notification.size()>0)
				intraDayAlgoStagiesHelper.sendNotification(step1Notification, "Stock Selection");

			// 99926000 token nifity 50
			// JSONObject marketData= smartApiLogin.getMarketData("OHLC","99926000") ;
			// System.err.println("===>"+marketData.toString());

		} catch (Exception e) {
			System.err.println("------excuteStagies------>"+e.getMessage());
		}

	}

	/**
	 * 
	 * @param lastSuccessRun
	 */
	public void excuteNifity50CandleData(Date lastSuccessRun) {

		try {
			Map<String, Nofifitcation> step2Notification = new HashMap<>();

			// STEP 4
			// AT 09:25 WE WILL WAIT FOR 10 MINUTE CANDLE
			// READ CANDLE DATA FOR 99926000 TOKEN NIFITY 50
			String fromdate = TRADEDateUtil.dynamicDate(nifity50_candle_from_time_hour, nifity50_candle_from_time_minute);
			String todate = TRADEDateUtil.dynamicDate(nifity50_candle_to_time_hour, nifity50_candle_to_time_minute);

			String candleType = intraDayAlgoStagiesHelper.getCandleType(fromdate, todate, TRADEConstants.NIFITY50_TOKEN,
					TRADEConstants.INTERVAL_TEN_MINUTE);

			intraDayAlgoStagiesHelper.saveTradeCandleForLooserAndGainer(candleType);

			String apply7perChangeGainerOICandleMailSubject = "Nifity50 Candle Type filter 7% changes OI Gainer";
			String apply7perChangeLooserOICandleMailSubject = "Nifity50 Candle Type filter 7% changes OI Looser";

			intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_GAINER,
					TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_GAINER, "st1", apply7perChangeGainerOICandleMailSubject,
					step2Notification);
			intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_LOOSER,
					TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_LOOSER, "st2", apply7perChangeLooserOICandleMailSubject,
					step2Notification);

			intraDayAlgoStagiesHelper.sendNotification(step2Notification, "According nifity50 candle type Stock");
		} catch (Exception e) {
			System.err.println("------excuteNifity50CandleData------>"+e.getMessage());
		}

	}

	/**
	 * 
	 * @param lastSuccessRun
	 */
	public void excuteTakeEntryInStockOptions(Date lastSuccessRun) {
		
		List<TradeEntryStock> topGainerTradeEntryStock=null;
		List<TradeEntryStock> topLooserTradeEntryStock=null;
		Map<String, Nofifitcation> step3Notification = new HashMap<>();
		String candleType="";

		try {

			// top gainer
			TopGainerAndLooserStocks topGainersOpenIntrestMoreThan7PercentList = topGainerAndLooserStockService
					.getTopGainerAndLooser(TRADEConstants.TYPE_GAINER, TRADEConstants.EXCHANGE,
							TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_GAINER);

			// top looser
			TopGainerAndLooserStocks topLoosersOpenIntrestMoreThan7PercentList = topGainerAndLooserStockService
					.getTopGainerAndLooser(TRADEConstants.TYPE_LOOSER, TRADEConstants.EXCHANGE,
							TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_LOOSER);

			// printing gainer with candle details of nifity50
			String gainerCandleType = null;
			String looserCandleType = null;
			
		
			if (topGainersOpenIntrestMoreThan7PercentList != null) {
				gainerCandleType = topGainersOpenIntrestMoreThan7PercentList.getNifity50candletype();
			}

			if (topLoosersOpenIntrestMoreThan7PercentList != null) {
				looserCandleType = topLoosersOpenIntrestMoreThan7PercentList.getNifity50candletype();
			}

			if ((gainerCandleType != null && gainerCandleType.equals(TRADEConstants.NIFITY50_CANDLE_GREEN_CALCULATED))
					|| (looserCandleType != null
							&& looserCandleType.equals(TRADEConstants.NIFITY50_CANDLE_GREEN_CALCULATED))) {
				
				candleType=TRADEConstants.NIFITY50_CANDLE_GREEN_CALCULATED;

				// printing gainer with candle details of nifity50
				
				String entryGainerCandleGreenMailSubject = "Taking entry in Gainer based on Candle Type Green";
				intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_GAINER,
						TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_GAINER, "st1", entryGainerCandleGreenMailSubject,
						step3Notification);
				
				// work on top gainer
				
				topGainerTradeEntryStock=intraDayAlgoStagiesHelper.startWorkOnTopGainerOrLooser(topGainersOpenIntrestMoreThan7PercentList,"CE");
				
				// printing looser with candle details of nifity50
				String entryLooserCandleGreenMailSubject = "Taking entry in Looser based on Candle Type Green";
				intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_LOOSER,
						TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_LOOSER, "st2", entryLooserCandleGreenMailSubject,
						step3Notification);
				
				// work on top looser
				 topLooserTradeEntryStock=intraDayAlgoStagiesHelper.startWorkOnTopGainerOrLooser(topLoosersOpenIntrestMoreThan7PercentList,"PE");

			} else if ((gainerCandleType != null
					&& gainerCandleType.equals(TRADEConstants.NIFITY50_CANDLE_RED_CALCULATED))
					|| (looserCandleType != null
							&& looserCandleType.equals(TRADEConstants.NIFITY50_CANDLE_RED_CALCULATED))) {
				candleType=TRADEConstants.NIFITY50_CANDLE_RED_CALCULATED;
				
				// printing looser with candle details of nifity50
				String entryLooserCandleGreenMailSubject = "Taking entry in Looser based on Candle Type Red";
				intraDayAlgoStagiesHelper.printStockData(TRADEConstants.TYPE_LOOSER,
						TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_LOOSER, "st1", entryLooserCandleGreenMailSubject,
						step3Notification);
				
				// work on top looser
				topLooserTradeEntryStock=intraDayAlgoStagiesHelper.startWorkOnTopGainerOrLooser(topLoosersOpenIntrestMoreThan7PercentList,"PE");
			}
			
			//send mail
			if(step3Notification.size()>0)
				intraDayAlgoStagiesHelper.sendNotification(step3Notification,"Taking entry in stock ");
			
			//web socket connect code
			//intraDayAlgoStagiesHelper.openConnection(topGainerTradeEntryStock,topLooserTradeEntryStock,candleType) ;
			//LocalDateTime now=DateSelection.getLocalDateTime();
			
			LocalDateTime now = DateSelection.getLocalDateTime();
			LocalTime nine_twenty_six = LocalTime.of(stock_entry_to_time_hour,stock_entry_to_time_minute);
			LocalDateTime updatedDateTime = now.toLocalDate().atTime(nine_twenty_six);
			 
			//String cronExpression = "0 0/5 * * * ?";
			dynamicScheduledService.updateCron(init_cron_expression,updatedDateTime,0);
			
		} catch (TradeScheduleBusinessException e) {
			System.err.println("------excuteTakeEntryInStockOptions------>"+e.getMessage());
		}
		
		
	}

}
