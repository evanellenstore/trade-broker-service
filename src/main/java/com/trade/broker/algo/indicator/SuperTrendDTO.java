package com.trade.broker.algo.indicator;

import lombok.Getter;

@Getter
public class SuperTrendDTO {
	
	
	private String superTrend;
	private double atr;
	private double finalUB; 
	private double finalLB;
	private double superTrendValue;
	
	public SuperTrendDTO() {
		
	}
	
	public SuperTrendDTO(String superTrend, double atr, double finalUB, double finalLB, double superTrendValue) {
	
		this.superTrend = superTrend;
		this.atr = atr;
		this.finalUB = finalUB;
		this.finalLB = finalLB;
		this.superTrendValue = superTrendValue;
	}

	@Override
	public String toString() {
		return "SuperTrendDTO [superTrend=" + superTrend + ", superTrendValue=" + superTrendValue + "]";
	}
	
	
	
    
    

}
