package com.trade.broker.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trade.broker.entity.TradeEntryStock;

@Repository
public interface TradeEntryStockRepository extends JpaRepository<TradeEntryStock, Long> {
	/**
	 * 
	 * @param tradestatus
	 * @return
	 */
	public List<TradeEntryStock> findByTradestatus(@Param("tradestatus") String tradestatus);
	
	
	/**
	 * 
	 * @param tradestatus
	 * @param exchange
	 * @param startOfDay
	 * @param endOfDay
	 * @return
	 */
	 @Query("SELECT t FROM TradeEntryStock t WHERE t.tradestatus = :tradestatus  " +
	           "  AND t.exchange = :exchange " +
	           "  AND t.creatTimestamp BETWEEN :startOfDay AND :endOfDay ")
	List<TradeEntryStock>  findTodayByTrade(@Param("tradestatus") String tradestatus, @Param("exchange") String exchange,
	            @Param("startOfDay") Timestamp startOfDay,@Param("endOfDay") Timestamp endOfDay );
	
	 
	 
	 @Query("SELECT t FROM TradeEntryStock t WHERE t.tradestatus NOT IN ('Close', 'Disqualified', 'Cancelled') " +
		       "AND t.exchange = :exchange " )
		List<TradeEntryStock> findByActiveTrade(@Param("exchange") String exchange);
	 
	 
	 
	 /**
	  * 
	  * @param stocksymboltoken
	  * @param tradestatus
	  * @return
	  */
	
	public TradeEntryStock findByStocksymboltokenAndTradestatus(@Param("stocksymboltoken") String stocksymboltoken, @Param("tradestatus") String tradestatus);
	
	
	/**
	 * 
	 * @param stocksymboltoken
	 * @param tradestatus
	 * @param startOfDay
	 * @param endOfDay
	 * @return
	 */
	@Query("SELECT t FROM TradeEntryStock t WHERE t.stocksymboltoken = :stocksymboltoken  " +
	           "  AND t.tradestatus = :tradestatus " +
	           "  AND t.creatTimestamp BETWEEN :startOfDay AND :endOfDay ")
	TradeEntryStock findTodayStocksymboltokenAndTradestatus(@Param("stocksymboltoken") String stocksymboltoken, @Param("tradestatus") String tradestatus,
	            @Param("startOfDay") Timestamp startOfDay,@Param("endOfDay") Timestamp endOfDay );
	
	
	
}

