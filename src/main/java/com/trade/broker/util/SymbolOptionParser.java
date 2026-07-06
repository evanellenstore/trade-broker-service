package com.trade.broker.util;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class SymbolOptionParser {

    // Regex explanation:
    // ([A-Z]+)              -> Group 1: Underlying symbol (one or more uppercase letters)
    // (\\d{2}[A-Z]{3}\\d{2}) -> Group 2: Expiry string in the format ddMMMyy (e.g. "27MAR25")
    // (\\d+)                -> Group 3: Strike price (one or more digits)
    // ([A-Z]{2})            -> Group 4: Option type (exactly two letters, e.g., "CE" or "PE")
    private static final Pattern OPTION_SYMBOL_PATTERN1 =
            Pattern.compile("([A-Z]+)(\\d{2}[A-Z]{3}\\d{2})(\\d+)([A-Z]{2})");
    
    
    private static final Pattern OPTION_SYMBOL_PATTERN2 = 
    		Pattern.compile("([A-Z]+)(\\d{2}[A-Z]{3}\\d{2})([0-9]+(?:\\.[0-9]+)?)([A-Z]{2})");
    
    
    private static final Pattern OPTION_SYMBOL_PATTERN3 = Pattern.compile("^([A-Z&]+)(\\d{2}[A-Z]{3}\\d{2})(\\d+)([A-Z]{2})$");
    
    private static final Pattern OPTION_SYMBOL_PATTERN4 = Pattern.compile("([A-Z]+-[A-Z]+)(\\d{2}[A-Z]{3}\\d{2})(\\d+(?:\\.\\d+)?)([A-Z]{2})");

   
    public static SymbolOptionData parseOptionSymbol(String symbol) {
    
    	Matcher matcher=OPTION_SYMBOL_PATTERN1.matcher(symbol);
    	
    	if(!matcher.matches()) {
    		matcher=OPTION_SYMBOL_PATTERN2.matcher(symbol);
    		if(!matcher.matches()) {
    			matcher=OPTION_SYMBOL_PATTERN3.matcher(symbol);
    			
    			if(!matcher.matches()) {
    				matcher=OPTION_SYMBOL_PATTERN4.matcher(symbol);
    			}
    		}
    		
    	}
    	
    	
    	
        if (!matcher.matches()) {
          
          // System.out.println("Invalid option symbol format: " + symbol);
           return null;
        }

        String underlying = matcher.group(1);
        String expiryStr = matcher.group(2);
        String strikeStr = matcher.group(3);
        String optionType = matcher.group(4);
        
       
        // Parse expiry date using pattern "ddMMMyy"
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("ddMMMyy")
                .toFormatter(Locale.ENGLISH);
        LocalDate expiry = LocalDate.parse(expiryStr, formatter);
        Date expiryDate = Date.from(expiry.atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        
        double strike=Double.parseDouble(strikeStr);
        int strikeInt = (int) strike;

        return new SymbolOptionData(underlying, expiryDate, strikeInt, optionType);
    }
}




