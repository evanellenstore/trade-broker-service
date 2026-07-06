package com.trade.broker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trade.broker.entity.FNOStockDetail;

@Repository
public interface FNOStockRepository extends JpaRepository<FNOStockDetail, Long> {
	
	public List<FNOStockDetail> findByExchangeAndLiveortest(@Param("exchange") String exchange,@Param("liveortest") String liveortest);
	
	
	public FNOStockDetail findByStackname(@Param("stackname") String Stackname);
	
	
	
}

