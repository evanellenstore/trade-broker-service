package com.trade.broker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trade.broker.constant.TRADEConstants;
import com.trade.broker.domain.IJobScheduleDomain;
import com.trade.broker.dto.JobScheduleDTO;
import com.trade.broker.entity.TJobScheduleEO;
import com.trade.broker.exception.TradeScheduleBusinessException;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@EnableScheduling
public class JobScheduleService implements IJobScheduleService{
	
	@Autowired
	private IJobScheduleDomain jobScheduleDomain;
	
	
	
	@Autowired
	private JobScheduleHelper JobScheduleHelper;
	
	
	//Step 1 select stock
	
	@Transactional
	@Override
	@Scheduled(cron="#{@getTGTLJobSchedule}")
	public void fetchTopGainerStock() {
		try {
			log.info("Fetching getTopGainerStockr jobs from the database...");
			String jobName=TRADEConstants.JOB_CODE_INTRADAY_TOPGAINER_TOPLOOSER;
			TJobScheduleEO jobSchedule = jobScheduleDomain.findByJobCode(jobName);		
			JobScheduleHelper.jobProcessing(jobSchedule,jobName);
		} catch (Exception exception) {
			log.error("processJob()", exception);
		}
		
	}
	
	// Step 2 Check Nifity50 candle chart
	@Transactional
	@Override
	@Scheduled(cron="#{@getNCDJobSchedule}")
	public void nifity50JobSchedule() {
		try {
			log.info("nifity50JobSchedule jobs from the database...");
			String jobName=TRADEConstants.JOB_CODE_NIFITY50_CANDLE_DATA;
			TJobScheduleEO jobSchedule = jobScheduleDomain.findByJobCode(jobName);		
			JobScheduleHelper.jobProcessing(jobSchedule,jobName);
		} catch (Exception exception) {
			log.error("processJob()", exception);
		}
		
	}
	
	
	//Step 3 Take entry
	
	
	@Transactional
	@Override
	@Scheduled(cron="#{@getEntryJobSchedule}")
	public void takeEntryInStockOptions() {
		try {
			log.info("take entry in stock option jobs from the database...");
			String jobName=TRADEConstants.JOB_CODE_TAKE_ENTRY_IN_STOCK_OPTIONS;
			TJobScheduleEO jobSchedule = jobScheduleDomain.findByJobCode(jobName);		
			JobScheduleHelper.jobProcessing(jobSchedule,jobName);
		} catch (Exception exception) {
			System.err.println("processJob() "+exception.getMessage());
		}
		
	}
	
	
	
	
	
	
	//Save market IO data every day becuase
	@Transactional
	@Override
	@Scheduled(cron="#{@getSMDJobSchedule}")
	public void saveMarketData() {
		try {
			log.info("saveMarketData jobs from the database...");
			String jobName=TRADEConstants.JOB_CODE_SAVE_MARKET_DATA;
			TJobScheduleEO jobSchedule = jobScheduleDomain.findByJobCode(jobName);		
			JobScheduleHelper.jobProcessing(jobSchedule,jobName);
		} catch (Exception exception) {
			log.error("processJob()", exception);
		}
		
	}
	
	
	
	
	

	@Transactional
	@Override
	public TJobScheduleEO  saveJobCronPattern(JobScheduleDTO jobScheduleDTO){
		TJobScheduleEO jobSchedule=new TJobScheduleEO();
		jobSchedule.setUpdUsr(TRADEConstants.SYSTEM_TRADE_SOURCE);
		jobSchedule.setJobCode(jobScheduleDTO.getJobCode());
		jobSchedule.setJobName(jobScheduleDTO.getJobName());
		jobSchedule.setSchedulePattern(jobScheduleDTO.getSchedulePattern());
		jobSchedule.setMaxIteration(jobScheduleDTO.getMaxIteration());
		TJobScheduleEO result=null;
		try {
			result= jobScheduleDomain.updateJobSchedule(jobSchedule);
		} catch (TradeScheduleBusinessException e) {
			log.error("error occur in save job cron ..");
			
		}
		
		return result;
	}
	
	

}
