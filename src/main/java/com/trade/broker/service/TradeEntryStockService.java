package com.trade.broker.service;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trade.broker.constant.TRADEConstants;
import com.trade.broker.entity.TradeEntryStock;
import com.trade.broker.repository.TradeEntryStockRepository;
import com.trade.broker.util.DateSelection;

@Service
public class TradeEntryStockService {
	
	@Autowired
	private TradeEntryStockRepository tradeEntryStockRepository;
	
	public List<TradeEntryStock> fetchTradeWithStatus(String tradestatus ){
		return  tradeEntryStockRepository.findByTradestatus(tradestatus);
	}
	
	
	
	public TradeEntryStock saveOrUpdate(TradeEntryStock tradeEntryStock ){
		return  tradeEntryStockRepository.save(tradeEntryStock);
	}
	
	
	public List<TradeEntryStock> fetchTodayTrade(String tradestatus ){
		// Calculate start and end of previous day.
		LocalDate todayday=DateSelection.getLocalDate();
        LocalDateTime startOfday = todayday.atStartOfDay();
        LocalDateTime endOfday = todayday.atTime(LocalTime.MAX);
        Timestamp startTimestamp = Timestamp.valueOf(startOfday);
        Timestamp endTimestamp = Timestamp.valueOf(endOfday);
		return  tradeEntryStockRepository.findTodayByTrade(tradestatus,TRADEConstants.EXCHANGE, startTimestamp, endTimestamp);
	}
	
	
	public List<TradeEntryStock> fetchActiveTrade(){
		
		return  tradeEntryStockRepository.findByActiveTrade(TRADEConstants.EXCHANGE);
	}
	
	
	public TradeEntryStock fetchStocksymboltokenAndTradestatus(String stocksymboltoken,String tradestatus ){
		return  tradeEntryStockRepository.findByStocksymboltokenAndTradestatus(stocksymboltoken,tradestatus);
	}
	
	
	public TradeEntryStock fetchTodayStocksymboltokenAndTradestatus(String stocksymboltoken,String tradestatus) {
		
		LocalDate todayday=DateSelection.getLocalDate();
        LocalDateTime startOfday = todayday.atStartOfDay();
        LocalDateTime endOfday = todayday.atTime(LocalTime.MAX);
        Timestamp startTimestamp = Timestamp.valueOf(startOfday);
        Timestamp endTimestamp = Timestamp.valueOf(endOfday);
		
		return  tradeEntryStockRepository.findTodayStocksymboltokenAndTradestatus(stocksymboltoken,tradestatus,startTimestamp,endTimestamp);
		
	}
	
	
	
	
	

}
