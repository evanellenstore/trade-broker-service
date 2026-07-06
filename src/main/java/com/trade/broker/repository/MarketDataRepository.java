package com.trade.broker.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trade.broker.entity.MarketDataHistorical;

@Repository
public interface MarketDataRepository extends JpaRepository<MarketDataHistorical, Long> {
	
	public List<MarketDataHistorical> findByExchange(@Param("exchange") String exchange);
	
	// 1. Fetch the latest (i.e. previous) data for a given trading symbol and exchange.
    MarketDataHistorical findTopByTradingsymbolAndExchangeOrderByCreatTimestampDesc(String tradingsymbol, String exchange);

    
    
    // 2. Fetch the latest record from the previous day for a given trading symbol and exchange.
    //    (Assume you calculate startOfPreviousDay and endOfPreviousDay in your service layer.)
    @Query("SELECT m FROM MarketDataHistorical m " +
           "WHERE m.symboltoken = :symboltoken " +
           "  AND m.exchange = :exchange " +
           "  AND m.creatTimestamp BETWEEN :startOfPreviousDay AND :endOfPreviousDay " +
           "ORDER BY m.creatTimestamp DESC LIMIT 1")
    MarketDataHistorical findLatestBySymboltokenForPreviousDay(
            @Param("symboltoken") String symboltoken,
            @Param("exchange") String exchange,
            @Param("startOfPreviousDay") Timestamp startOfPreviousDay,
            @Param("endOfPreviousDay") Timestamp endOfPreviousDay 
    );
	
	
	
}

