package com.trade.broker.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trade.broker.entity.TopGainerAndLooserStocks;
import com.trade.broker.repository.TopGainerAndLooserStockRepository;
import com.trade.broker.util.DateSelection;

@Service
public class TopGainerAndLooserStockService {
	
	
	@Autowired
	private TopGainerAndLooserStockRepository topGainerAndLooserStockRepository;
	
	public TopGainerAndLooserStocks getTopGainerAndLooser(String type,String exchange,String applyfilter) {
		
		// Calculate start and end of previous day.
        LocalDate todayday=DateSelection.getLocalDate();
        LocalDateTime startOfday = todayday.atStartOfDay();
        LocalDateTime endOfday = todayday.atTime(LocalTime.MAX);

        Timestamp startTimestamp = Timestamp.valueOf(startOfday);
        Timestamp endTimestamp = Timestamp.valueOf(endOfday);
		
		return topGainerAndLooserStockRepository.findLatestByTopGainerAndLooser(type,exchange,applyfilter,startTimestamp,endTimestamp);
		
	}
	
	
public TopGainerAndLooserStocks saveTopGainerAndLooser(String strategies,String type,String exchange,String applyfilter,String jsonData,String candletype,Long id) {
		
		TopGainerAndLooserStocks topGainerAndLooserStocks=new TopGainerAndLooserStocks();
		topGainerAndLooserStocks.setExchange(exchange);
		topGainerAndLooserStocks.setApplyfilter(applyfilter);
		topGainerAndLooserStocks.setStrategies(strategies);
		topGainerAndLooserStocks.setJsonData(jsonData);
		topGainerAndLooserStocks.setType(type);
		topGainerAndLooserStocks.setNifity50candletype(candletype);
		
		if(id!=null) {
			topGainerAndLooserStocks.setId(id);
		}
		return topGainerAndLooserStockRepository.save(topGainerAndLooserStocks);
		
	}

}
