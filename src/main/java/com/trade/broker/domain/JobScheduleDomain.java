package com.trade.broker.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trade.broker.entity.TJobHistoryEO;
import com.trade.broker.entity.TJobScheduleEO;
import com.trade.broker.exception.TradeScheduleBusinessException;
import com.trade.broker.repository.ITradeJobHistoryScheduleRepsitory;
import com.trade.broker.repository.ITradeJobScheduleRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobScheduleDomain implements IJobScheduleDomain {
	
	
	@Autowired
	private ITradeJobScheduleRepository tradeJobScheduleRepsitory;
	
	@Autowired
	private ITradeJobHistoryScheduleRepsitory tradeJobHistoryScheduleRepsitory;
	
	
	
	

	@Override
	public TJobScheduleEO findByJobCode(String jobCode) throws TradeScheduleBusinessException {
		
		TJobScheduleEO tJobScheduleEO=tradeJobScheduleRepsitory.findByJobCode(jobCode);
		
		return tJobScheduleEO;
	}
	
	

	@Override
	public TJobScheduleEO updateJobSchedule(TJobScheduleEO jobScheduleEO) throws TradeScheduleBusinessException {
		return tradeJobScheduleRepsitory.save(jobScheduleEO);
	}

	@Override
	public TJobHistoryEO insertJobExcutionHistory(TJobHistoryEO jobHistoryEO) throws TradeScheduleBusinessException {
		return tradeJobHistoryScheduleRepsitory.save(jobHistoryEO);
		 
	}

	@Override
	public TJobScheduleEO fetchJobStatusDetails(String jobCode) throws TradeScheduleBusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
