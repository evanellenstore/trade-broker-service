package com.trade.broker.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class JobScheduleDTO  implements Serializable{
	
	private static final long serialVersionUID = 1089043771625616126L;

	private String jobCode;
	
	private String jobName;
	
	private String schedulePattern;
	
	private int maxIteration;

}
