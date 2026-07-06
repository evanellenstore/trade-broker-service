package com.trade.broker.domain;

import com.trade.broker.entity.TJobHistoryEO;
import com.trade.broker.entity.TJobScheduleEO;
import com.trade.broker.exception.TradeScheduleBusinessException;

public interface IJobScheduleDomain {
	
	public TJobScheduleEO findByJobCode(String jobCode) throws TradeScheduleBusinessException; 
	
	
	public TJobScheduleEO updateJobSchedule(TJobScheduleEO jobScheduleEO) throws TradeScheduleBusinessException; 
	
	public TJobHistoryEO insertJobExcutionHistory(TJobHistoryEO jobHistoryEO) throws TradeScheduleBusinessException; 
	
	public TJobScheduleEO fetchJobStatusDetails(String jobCode) throws TradeScheduleBusinessException; 
		
	

}
