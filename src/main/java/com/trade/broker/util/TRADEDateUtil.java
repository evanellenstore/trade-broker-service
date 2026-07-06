package com.trade.broker.util;




import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;



/**
 * @author Toyota Motor Sales
 *
 */
@Slf4j
public final class TRADEDateUtil {
	
	public static String getDateInStringFormat(String dtFrmt, Date dtObj) {
		DateFormat dateFormat = new SimpleDateFormat(dtFrmt);
		return dateFormat.format(dtObj);
	}
	
	/**
	 * @return
	 */
	public static long get3MonthBackDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MONTH, -3);
		return cal.getTimeInMillis();
	}
	
	/**
	 * @param format
	 * @param date
	 * @param months
	 * @return
	 */
	public static String addMonthsToStringDate(String format, String date, int months) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		try {
			Date inputDate = dateFormat.parse(date);
			cal.setTime(inputDate);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.MONTH, months);
			return dateFormat.format(cal.getTime());
		} catch (ParseException e) {
			return null;
		}
		
		
	}
	
	/**
	 * @return
	 */
	public static Date getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}

	/**
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static Date getCurrentDateInFormat(String format) throws ParseException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(sdf.format(cal.getTime()));
	}

	/**
	 * 
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static String getCurrentDateFormat(String format){
		
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat(format);
       return dateFormat.format(date);
	}
	/**
	 * @param date
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateFromString(String date, String format) throws ParseException {
		if (null != date) {
			return new SimpleDateFormat(format).parse(date);
		}
		return null;
	}
	
	/**
	 * @param date
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static Date formatDate(Date date, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if (null != date) {
			return sdf.parse(sdf.format(date));
		}
		return null;
	}
	
	
	/**
	 * This method is to create a deep copy of the date object to fix sonar
	 * violation of Malicious code vulnerability - May expose internal
	 * representation by returning reference to mutable object
	 * 
	 * @param date
	 * @return Date
	 */
	public static  java.sql.Date getDate( java.sql.Date date) {

		if (null != date) {
			return new  java.sql.Date(date.getTime());
		}
		return date;
	}
	
	
	/**
	 * Created for setting the updated timestamp
	 * @return
	 */
	public static java.sql.Timestamp getCurrentJavaSqlTimestamp() {
		java.util.Date date = new java.util.Date();
		return new java.sql.Timestamp(date.getTime());
	}
	
	
	
	/**
	 * This method will return the current java.sql.Date 
	 * @return
	 */
	public static java.sql.Date getCurrentSqlDate() {
		Calendar cal = Calendar.getInstance();
			return new java.sql.Date(cal.getTime().getTime());
		
	}
	
	
	
	/**
	 * This method will return the nth java.sql.Date
	 * @param n
	 * @return
	 */
	public static java.sql.Date getNthSqlDate(int n) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, n);
		return new java.sql.Date(cal.getTime().getTime());
		
	}
	
	
	public static String getCurrentFiscalYear() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
	    int month = Calendar.getInstance().get(Calendar.MONTH);
	    if (month > 2) {
	        return String.valueOf(year+1);
	    } else {
	    	return String.valueOf(year);
	    }
	}
	
	
	public static java.sql.Date getHighEndDateSqlDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 01);
		cal.set(Calendar.MONDAY, 00);
		cal.set(Calendar.YEAR, 9999);
	return new java.sql.Date(cal.getTime().getTime());
		
	}
	
	public static String convertStringDatetoStringDateFormat(String strDate, String fromFormat, String toFormat) {
		SimpleDateFormat dateFromFormat = new SimpleDateFormat(fromFormat);
		SimpleDateFormat dateToFormat = new SimpleDateFormat(toFormat);
		try {
			return dateToFormat.format(dateFromFormat.parse(strDate));
		} catch (ParseException objParseException) {
			
			return null;
		}
		
	}
	
	public static Date convertStringDatetoDate(String strDate, String fromFormat) {
		SimpleDateFormat dateFromFormat = new SimpleDateFormat(fromFormat);
		try {
			return dateFromFormat.parse(strDate);
		} catch (ParseException objParseException) {
			return null;
		}		
	}
	
	public static String convertDatetoStringDate(Date date, String fromFormat) {
		SimpleDateFormat dateFromFormat = new SimpleDateFormat(fromFormat);		
		return dateFromFormat.format(date);
		 	
	}
	
	

	public static void dealy(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static String dynamicDate(int hour, int minute) {
		LocalDate currentDate=DateSelection.getLocalDate();
		LocalTime nineAM = LocalTime.of(hour, minute);
		LocalDateTime dateTime = LocalDateTime.of(currentDate, nineAM);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String fromdate = dateTime.format(formatter);
		return fromdate;
	}
	
	/**
	 * 
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static String dynamicPrevDate(int hour, int minute) {
		LocalDate currentDate=DateSelection.getPreviousTradingLocalDate();
		LocalTime nineAM = LocalTime.of(hour, minute);
		LocalDateTime dateTime = LocalDateTime.of(currentDate, nineAM);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String fromdate = dateTime.format(formatter);
		return fromdate;
	}
	
	
	
}

