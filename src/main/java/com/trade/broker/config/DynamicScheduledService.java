package com.trade.broker.config;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.trade.broker.service.LiveTradeService;

@Service
public class DynamicScheduledService {

    @Autowired
    private TaskScheduler taskScheduler;
    
    @Autowired
    private LiveTradeService liveTradeService;
    
    private ScheduledFuture<?> scheduledFuture;

    // Default cron expression (e.g., every 2 minutes)
    private String cronExpression = "0 0/2 * * * ?";
    
	@Value("${regular.cron.expression}")
	private String regular_cron_expression;
    
    private LocalDateTime now;
    
    private int counter;

    
    public void scheduleTask() {
        scheduledFuture = taskScheduler.schedule(this::executeTask, new CronTrigger(cronExpression));
    }

    /**
     * 
     */
    private void executeTask() {
       // LocalTime currentTime = LocalTime.now(); 
       // LocalDateTime updatedDateTime = this.now.atTime(currentTime);
        System.out.println("Task executed at " + this.now);
        String status = liveTradeService.loadTrade(this.now,this.counter);
        
        LocalTime nineTwentySix = LocalTime.of(9, 26); 
        
        
        if(this.now.toLocalTime().equals(nineTwentySix)) {
        	updateCron(regular_cron_expression,this.now,this.counter);
        	this.now=this.now.plusMinutes(4);
        }else {
        	this.now=this.now.plusMinutes(5);
        }
        this.counter++;
        
        System.err.println("----executeTask status---"+status);
    }

    /**
     * Updates the cron expression, cancels the current task, and reschedules with the new pattern.
     *
     * @param newCronExpression the new cron expression from the database.
     */
    public boolean updateCron(String newCronExpression,LocalDateTime now,int counter) {
            this.cronExpression = newCronExpression;
            this.now=now;
            this.counter=counter;
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false); // cancel the current task without interrupting it
            }
            scheduledFuture = taskScheduler.schedule(this::executeTask, new CronTrigger(cronExpression));
            System.out.println("Cron expression updated and task rescheduled: " + cronExpression);
            return true;
    }
    
    /**
     * 
     * @return
     */
    public boolean stopTask() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false); // false means don't interrupt if running
            System.out.println("Scheduled task has been stopped.");
            return true;
        }else {
        	return false;
        }
    }
}

