package com.trade.broker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trade.broker.algo.AlgoStagies;
import com.trade.broker.constant.TRADEConstants;
import com.trade.broker.domain.IJobScheduleDomain;
import com.trade.broker.entity.TJobHistoryEO;
import com.trade.broker.entity.TJobScheduleEO;
import com.trade.broker.exception.TradeScheduleBusinessException;
import com.trade.broker.util.TRADEDateUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobScheduleHelper {
	
	@Autowired
	private IJobScheduleDomain jobScheduleDomain;
	
	@Autowired
	private AlgoStagies algoStagies;
	
	
	
	public void jobProcessing(TJobScheduleEO jobSchedule,String jobName) {
		TJobHistoryEO jobHistory=new TJobHistoryEO();
		jobHistory.setCreatUsr(TRADEConstants.SYSTEM_TRADE_SOURCE);
		jobHistory.setUpdUsr(TRADEConstants.SYSTEM_TRADE_SOURCE);
		jobHistory.setExecutionDate(TRADEDateUtil.getCurrentSqlDate());
		for(int i = 1 ; i <= jobSchedule.getMaxIteration() ; i++) {
			jobHistory.setIteration(i);
			if(!dailyProcessJob(jobSchedule, jobHistory, i,jobName)) {
				try {
					jobHistory.setStatus(TRADEConstants.TRADE_JOB_FAILURE);
					jobHistory.setSpaoJobSchedule(jobSchedule);
					jobScheduleDomain.insertJobExcutionHistory(jobHistory);
				} catch (TradeScheduleBusinessException batchLog) {
					log.error("processJob()", batchLog);
				}
			}else {
				break;
			}
		}
	}
	
	/**
	 * @param soxTransferJobSchedule
	 * @param spaoJobHistory
	 * @param i
	 */
	private boolean dailyProcessJob(TJobScheduleEO jobSchedule, TJobHistoryEO jobHistory,int i,String jobName) {
		try {
			
			log.info("dailyProcessJob inside the iteration number:"+i);
			
			// actual logic
			algoStagies.applyAlgoStagies(jobSchedule.getLastSuccessRun(),jobName);
			
			jobSchedule.setUpdUsr(TRADEConstants.SYSTEM_TRADE_SOURCE);		
			jobHistory.setStatus(TRADEConstants.TRADE_JOB_SUCCESS);
			jobSchedule.setLastSuccessRun(TRADEDateUtil.getCurrentSqlDate());
			
			jobScheduleDomain.updateJobSchedule(jobSchedule);
			jobHistory.setSpaoJobSchedule(jobSchedule);				
			//no cascading for the schedule to history, persist separately 
			
			jobScheduleDomain.insertJobExcutionHistory(jobHistory);								
			//TODO Send the batch status email as success to support accounts 
			this.sendJobStatus(jobSchedule.getJobName(), TRADEConstants.TRADE_JOB_SUCCESS, TRADEConstants.STRING_EMPTY);
			return true;
		} catch (TradeScheduleBusinessException e) {			
			this.sendJobStatus(jobSchedule.getJobName(), TRADEConstants.TRADE_JOB_FAILURE, e.getCause().getMessage());					
			log.error("error dailyProcessJob inside the iteration number:"+i);
			return false;
		}
	}
	
	private void sendJobStatus(String jobCode, String jobStatus,String message) {
		// send mail
		
	}


}
