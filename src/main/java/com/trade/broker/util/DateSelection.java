package com.trade.broker.util;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.trade.broker.constant.TRADEConstants;

public class DateSelection {
	
	
	public static String formatedDateTime(LocalDateTime currentDateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String formatterDateTime = currentDateTime.format(formatter);
		return formatterDateTime;
	}
	
	
	public static LocalDate getLocalDate() {
		
		if(TRADEConstants.TRADE_LIVE_OR_BACKTRAKING.equalsIgnoreCase("live")) {
			 LocalDate currentDate = LocalDate.now();
			 return currentDate;
		}else {
			DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime parsedDateTime = LocalDateTime.parse(TRADEConstants.TRADE_BACKTRAKING_DATE, dtFormatter);
			LocalDate fixedDate = parsedDateTime.toLocalDate();
			LocalTime currentTime = LocalTime.now();
			LocalDateTime updatedDateTime = fixedDate.atTime(currentTime);
			LocalDate localDate = updatedDateTime.toLocalDate();
			return localDate;
		}
		
	}
	
	/**
	 * 
	 * @return
	 */
	public static LocalDate getPreviousTradingLocalDate() {
			LocalDate localDate= getLocalDate();
			LocalDate yesterday= localDate.minusDays(1);
			
			 if (yesterday.getDayOfWeek() == DayOfWeek.SATURDAY) {
		        	yesterday = yesterday.minusDays(1); // Saturday -> Friday.
		        } else if (yesterday.getDayOfWeek() == DayOfWeek.SUNDAY) {
		        	yesterday = yesterday.minusDays(2); // Sunday -> Friday.
		        }
			return yesterday;
	}
	
	/**
	 * 
	 * @return
	 */
	public static LocalDateTime getLocalDateTime() {
		
		if(TRADEConstants.TRADE_LIVE_OR_BACKTRAKING.equalsIgnoreCase("live")) {
			LocalDateTime currentDate = LocalDateTime.now();
			 return currentDate;
		}else {
			DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime parsedDateTime = LocalDateTime.parse(TRADEConstants.TRADE_BACKTRAKING_DATE, dtFormatter);
			LocalDate fixedDate = parsedDateTime.toLocalDate();
			LocalTime currentTime = LocalTime.now();
			LocalDateTime updatedDateTime = fixedDate.atTime(currentTime);
			return updatedDateTime;
		}
		
	}
	
	/**
	 * 
	 * @return
	 */
	public static Timestamp convertLocalDateToTimestamp() {
		LocalDateTime localDate=getLocalDateTime();
        return Timestamp.valueOf(localDate);
    }
	
	
	
	public static Timestamp convertLocalDateToTimestamp(LocalDateTime localDate) {
        return Timestamp.valueOf(localDate);
    }
	
	

}
