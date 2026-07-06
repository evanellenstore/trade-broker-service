package com.trade.broker.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trade.broker.constant.TRADEConstants;

/**
 * Class for date manipulation.
 * @author Cognizant
 */
public final class DateUtil {
    /**
     * Default Constructor.
     */
    private DateUtil()
    {
    }
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DateUtil.class);
       
   
    
    /**
     * Method to assign values to enum constants.
     * @param dateFormat dateFormat
     * @return String
     */
    private static String getDateFormat(String dateFormat){        
        String returnFormat = null;
        switch(dateFormat){
        case TRADEConstants.DATEFORMAT:
            returnFormat = "MM/dd/yyyy";
            break;
        }
        return returnFormat;
    }
    
    /**
     * Method for converting Util date to sql date.
     * @param utilDate java.util.Date
     * @return sqlDate
     */
 
    public static java.sql.Date  convertUtilToSQL(java.util.Date utilDate){
        return  new java.sql.Date(utilDate.getTime());
    }
    
     /**
      * Method for converting Util date to sql date.
      * @param sqlDate java.sql.Date
      * @return utilDate
      */
    public static java.util.Date  convertSQLToUtil(java.sql.Date sqlDate){
      return new java.util.Date(sqlDate.getTime());
                                                                            
    }
    
    /**
     * Method to compare current date with given date for checking the same day.
     * @param sqlDate1 java.sql.Date
     * @return boolean 
     */
    public static boolean isSameDay(java.util.Date sqlDate1){
        java.util.Date utilDate1 = new java.util.Date(sqlDate1.getTime());
        java.util.Date utilDate2 = new java.util.Date();
        return DateUtils.isSameDay(utilDate2, utilDate1);
    }
    
    
    /**
     * Method to compare current date with given date for checking the future day.
     * @param sqlDate1 java.sql.Date
     * @return boolean 
     */
    public static boolean isFutureDay(java.util.Date sqlDate1){
        java.util.Date utilDate1 = new java.util.Date(sqlDate1.getTime());
        java.util.Date utilDate2 = new java.util.Date();
       return utilDate1.after(utilDate2);
    }
    
   /**
     * Method to format date in the given format.
     * @param sqlDate Date     
     * @return Date
     */
    public static Date formatDate(Date sqlDate) {
        Date formattedDate = null;
        if(sqlDate != null){
        SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat(TRADEConstants.DATEFORMAT));
        String dateStr = sdf.format(sqlDate);
        java.util.Date utilDate = null;
            try {
                utilDate = sdf.parse(dateStr);
            } catch (ParseException e) {
                LOGGER.debug("formatDate " + e);
            }
            if(utilDate!=null){
            formattedDate = new Date(utilDate.getTime());
            }
        }
        return formattedDate;
    }
    /**
     * Method to calculate days between dates.
     * @param startDate startDate
     * @param endDate endDate
     * @return long.
     */
    public static long calculateDaysBetweenDates(final Date startDate, final Date endDate){
        long days=0;
        try{
            final long diff = endDate.getTime() - startDate.getTime();
            days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        }catch(Exception e){
            LOGGER.error("calculateDaysBetweenDates " +e.getMessage());
            }    
    return days;
    }
    
    
    /**
     * Method to compare current date with given date for checking the past day.
     * @param sqlDate1 java.sql.Date
     * @return boolean 
     */
    public static boolean isPastDay(java.util.Date sqlDate1){
       SimpleDateFormat sdf = new SimpleDateFormat(TRADEConstants.YEAR_MONTH_DATE);
        java.util.Date utilDate2 = new java.util.Date();
        String dateString = sdf.format(utilDate2);
        java.sql.Date currentDate = Date.valueOf(dateString);
        return sqlDate1.before(currentDate);
    
    }
    
    
    /**
     * Method to return  current date.
     * @return boolean 
     */
    public static Date getCurrentSQLDate(){
       SimpleDateFormat sdf = new SimpleDateFormat(TRADEConstants.YEAR_MONTH_DATE);
        java.util.Date utilDate1 = new java.util.Date();
        String dateString = sdf.format(utilDate1);
        java.sql.Date currentDate = Date.valueOf(dateString);
        return currentDate;
    
    }
    
    /**
     * Method to compare current date with given date for checking the past day.
     * @param sqlDate1 java.sql.Date
     * @return boolean 
     */
    public static boolean isPastOrSameDay(java.util.Date sqlDate1){
       SimpleDateFormat sdf = new SimpleDateFormat(TRADEConstants.YEAR_MONTH_DATE);
        java.util.Date utilDate2 = new java.util.Date();
        String dateString = sdf.format(utilDate2);
        java.sql.Date currentDate = Date.valueOf(dateString);
        
        return sqlDate1.before(currentDate) || sqlDate1.equals(currentDate);
    
    }
    
    
	/**
	 * This Method Converts the String to Date Format.
	 * 
	 * @param inputDate
	 *            inputDate
	 * @return Date
	 */
	public static java.util.Date stringToDate(final String inputDate) {
		
		java.util.Date formattedDate = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(TRADEConstants.YEAR_MONTH_DATE);
			formattedDate = dateFormat.parse(inputDate);
		} catch (ParseException e) {

		}
		return formattedDate;
	}
	
	public static java.util.Date getDate(java.util.Date date) {
		return date != null ? new Date(date.getTime()) : null;
	}

	public static java.sql.Date getDate(java.sql.Date date) {
		return date != null ? new java.sql.Date(date.getTime()) : null;
	}
	
	public static Timestamp getTimestamp(Timestamp timestamp) {
		return timestamp != null ? new Timestamp(timestamp.getTime()) : null;
	}
	
	public static String convertSqlDateToString(Date sqlDate) {
		return sqlDate != null
				? TRADEDateUtil.convertDatetoStringDate(convertSQLToUtil(sqlDate),
						TRADEConstants.STANDARD_USER_DATE_FORMAT)
				: null;
	}
	
	/**
	 * This method returns java sql date
	 * 
	 * @return Date
	 */
	public static Date getSystemDate() {
		return new Date(System.currentTimeMillis());

	}
}

 
 

 


