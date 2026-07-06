package com.trade.broker.service;

import com.trade.broker.dto.JobScheduleDTO;
import com.trade.broker.entity.TJobScheduleEO;

public interface IJobScheduleService {
	
	public void fetchTopGainerStock();
	
	public TJobScheduleEO  saveJobCronPattern(JobScheduleDTO jobScheduleDTO);
	
	public void  saveMarketData( );
	
	public void nifity50JobSchedule();
	
	public void takeEntryInStockOptions();
	
	
	
	
	

}
