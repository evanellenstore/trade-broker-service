package com.trade.broker.service;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.angelbroking.smartapi.utils.Constants;
import com.trade.broker.algo.SmartApiLogin;
import com.trade.broker.entity.FNOStockDetail;
import com.trade.broker.entity.MarketDataHistorical;
import com.trade.broker.exception.TradeScheduleBusinessException;
import com.trade.broker.repository.MarketDataRepository;
import com.trade.broker.util.DateSelection;
@Service
public class MarketDataHistoricalService {
	
	@Autowired
	private FNOStockService fnoStockService;
	
	@Autowired
	private SmartApiLogin smartApiLogin;
	
	@Autowired
	private MarketDataRepository marketDataRepository;
	
	
	public MarketDataHistorical getPreviousDayLatestData(String symboltoken, String exchange) {
        // Calculate start and end of previous day.
		LocalDate currentDate=DateSelection.getLocalDate();
		LocalDate yesterday = currentDate.minusDays(1);
        
        if (yesterday.getDayOfWeek() == DayOfWeek.SATURDAY) {
        	yesterday = yesterday.minusDays(1); // Saturday -> Friday.
        } else if (yesterday.getDayOfWeek() == DayOfWeek.SUNDAY) {
        	yesterday = yesterday.minusDays(2); // Sunday -> Friday.
        }
        
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        LocalDateTime endOfYesterday = yesterday.atTime(LocalTime.MAX);

        Timestamp startTimestamp = Timestamp.valueOf(startOfYesterday);
        Timestamp endTimestamp = Timestamp.valueOf(endOfYesterday);
        

        return marketDataRepository.findLatestBySymboltokenForPreviousDay(symboltoken, exchange, startTimestamp, endTimestamp);
    }
	
	
	
	
	/**
	 * 
	 * @return
	 * @throws TradeScheduleBusinessException
	 */
	
	public String saveMarketData(Date lastSuccessRun) throws TradeScheduleBusinessException {

		String result = "Save data successfully ";
		System.err.println("saveMarketData....");	
		try {

			List<FNOStockDetail> fnoStockDetailList = fnoStockService.findByExchange(Constants.EXCHANGE_NSE);
			for (FNOStockDetail fnoStockDetail : fnoStockDetailList) {
			JSONObject todayMarketData= smartApiLogin.getMarketData("FULL",fnoStockDetail.getSymboltoken(),Constants.EXCHANGE_NSE) ; 
			System.out.println("todayMarketData "+todayMarketData);	
				if (todayMarketData != null) {
					MarketDataHistorical marketDataHistorical = new MarketDataHistorical();
					marketDataHistorical.setExchange(Constants.EXCHANGE_NSE);
					marketDataHistorical.setTradingsymbol(fnoStockDetail.getTradingsymbol());
					marketDataHistorical.setSymboltoken(fnoStockDetail.getSymboltoken());
					marketDataHistorical.setMarketdata(todayMarketData.toString());

					marketDataRepository.save(marketDataHistorical);
					Thread.sleep(500);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "Save data not successfully " + e.getMessage();
		}

		return result;

	}
	
	
	
	

}
