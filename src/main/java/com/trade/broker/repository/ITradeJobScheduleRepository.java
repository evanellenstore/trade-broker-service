package com.trade.broker.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trade.broker.entity.TJobScheduleEO;
import com.trade.broker.exception.TradeScheduleBusinessException;

@Repository
public interface ITradeJobScheduleRepository extends ITradeBaseRepository<TJobScheduleEO, Integer> {
	
	public TJobScheduleEO findByJobCode(String jobCode) throws TradeScheduleBusinessException;
	
	@Modifying
	@Query("update TJobScheduleEO jobSchedule set jobSchedule.lastSuccessRun = :jobExecutionDate, jobSchedule.updTimestamp = CURRENT_TIMESTAMP, jobSchedule.updUsr = :source where jobSchedule.jobCode = :jobCode " )
	public int updateLastSuccessfulJobExecution(@Param("jobCode") String jobCode, @Param("jobExecutionDate") Date jobExecutionDate, @Param("source") String source)throws TradeScheduleBusinessException;
	
	@Query(value = "select ID,JOB_NAME,JOB_CODE,SCHEDULE_PATTERN,UPD_TIMESTAMP,LAST_SUCCESS_RUN from NAPARTS.TSPAO_JOB_SCHEDULES WHERE JOB_NAME NOT in ('CHECK 594 ROLE SYNCUP','TALON LOAD')", nativeQuery = true)
	public List<Object[]> fetchJobStatusDetails() throws TradeScheduleBusinessException;
	
}

