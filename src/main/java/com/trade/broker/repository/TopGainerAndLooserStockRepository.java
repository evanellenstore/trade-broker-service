package com.trade.broker.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trade.broker.entity.TopGainerAndLooserStocks;

public interface TopGainerAndLooserStockRepository extends JpaRepository<TopGainerAndLooserStocks, Long>{
	
	
	 @Query("SELECT tl FROM TopGainerAndLooserStocks tl " +
	           "WHERE tl.exchange = :exchange  " +
	           "  AND tl.type = :type " +
	           "  AND tl.applyfilter = :applyfilter " +
	           "  AND tl.creatTimestamp BETWEEN :startOfDay AND :endOfDay " +
	           "ORDER BY tl.creatTimestamp DESC LIMIT 1")
	 
	 TopGainerAndLooserStocks findLatestByTopGainerAndLooser(
			 @Param("type") String type,
	            @Param("exchange") String exchange,
	            @Param("applyfilter") String applyfilter,
	            @Param("startOfDay") Timestamp startOfDay,
	            @Param("endOfDay") Timestamp endOfDay 
	    );
	
	
}
