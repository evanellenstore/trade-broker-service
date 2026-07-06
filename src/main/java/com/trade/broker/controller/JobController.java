package com.trade.broker.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.trade.broker.dto.JobScheduleDTO;
import com.trade.broker.entity.TJobScheduleEO;
import com.trade.broker.exception.TradeScheduleBusinessException;
import com.trade.broker.service.FNOStockService;
import com.trade.broker.service.IJobScheduleService;

@RestController
@RequestMapping("/api/job")
public class JobController {

	@Autowired
	private IJobScheduleService jobScheduleService;
	
	@Autowired
	private FNOStockService fnoStockService;

	@GetMapping("/topGainerStock")
	public ResponseEntity<String> getTopGainerStock() {
		jobScheduleService.fetchTopGainerStock();
		return ResponseEntity.ok("Completed..");
	}
	
	@GetMapping("/nifity50candle")
	public ResponseEntity<String> nifity50JobSchedule() {
		jobScheduleService.nifity50JobSchedule();
		return ResponseEntity.ok("Completed..");
	}
	
	
	@GetMapping("/takeEntryInStockOption")
	public ResponseEntity<String> takeEntryInStockOption() {
		jobScheduleService.takeEntryInStockOptions();
		return ResponseEntity.ok("Completed..");
	}

	@PostMapping("/saveJobDetail")
	public ResponseEntity<TJobScheduleEO> saveJobDetail(JobScheduleDTO jobScheduleDTO) {
		TJobScheduleEO tJobScheduleEO = jobScheduleService.saveJobCronPattern(jobScheduleDTO);
		return ResponseEntity.ok(tJobScheduleEO);
	}
	
	
	@GetMapping("/saveFNOStock")
	public ResponseEntity<String> saveFNOStock() {
		String response="";
		try {
			response=fnoStockService.insertFNOStock();
		} catch (TradeScheduleBusinessException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(response);
	}
	
	
	@GetMapping("/saveMarketData")
	public ResponseEntity<String> saveMarketData() {
		jobScheduleService.saveMarketData();
		return ResponseEntity.ok("Completed.");
	}


}
