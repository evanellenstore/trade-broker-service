package com.trade.broker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.trade.broker.entity.DBTokenDetail;

public interface TokenRepository extends JpaRepository<DBTokenDetail, Long> {
	
	@Query("SELECT t FROM DBTokenDetail t WHERE t.appName = :appName AND t.tokenexpried = :tokenexpried")
	DBTokenDetail findByAppNameAndTokenexpired(String appName, String tokenexpried);

	
	
}