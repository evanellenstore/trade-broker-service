package com.trade.broker.util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionDataParser {
    public static List<Map<String,Object>> parseOptionData( String input) {
        
    	 List<Map<String,Object>> allData =new ArrayList<>();

        // Pattern explanation:
        // ^(\d+)\.      --> Index (one or more digits) followed by a dot.
        // \s*exchange:\s*  --> The literal "exchange:" with optional spaces.
        // (\w+),        --> Exchange value (word characters) followed by a comma.
        // \s*tradingsymbol:\s* --> The literal "tradingsymbol:".
        // (\w+),        --> Trading symbol (word characters) followed by a comma.
        // \s*symboltoken:\s* --> The literal "symboltoken:".
        // (\d+)$        --> Symbol token (one or more digits) till end of line.
        Pattern pattern1 = Pattern.compile("^(\\d+)\\.\\s*exchange:\\s*(\\w+),\\s*tradingsymbol:\\s*(\\w+),\\s*symboltoken:\\s*(\\d+)$");

        Pattern pattern2 = Pattern.compile("^(\\d+)\\.\\s*exchange:\\s*(\\w+),\\s*tradingsymbol:\\s*([^,]+),\\s*symboltoken:\\s*(\\d+)$");
        
        Pattern pattern3 = Pattern.compile("^(\\d+)\\.\\s*exchange:\\s*(\\w+),\\s*tradingsymbol:\\s*([^,]+),\\s*symboltoken:\\s*(\\d+)$");
        Pattern pattern4 = Pattern.compile("^(\\d+)\\.\\s*exchange:\\s*(\\w+),\\s*tradingsymbol:\\s*([\\w\\-]+),\\s*symboltoken:\\s*(\\d+)$");
        // Split the input string into individual lines.
        String[] lines = input.split("\\r?\\n");

        for (String line : lines) {
        	//System.out.println("---->>"+line);
            Matcher matcher1 = pattern1.matcher(line.trim());
            Matcher matcher2 = pattern2.matcher(line.trim());
            Matcher matcher3 = pattern3.matcher(line.trim());
            Matcher matcher4 = pattern4.matcher(line.trim());
            if (matcher1.matches()) {
            	parseDataWithPattern(allData, matcher1);
            }else if(matcher2.matches()) {
            	parseDataWithPattern(allData, matcher2);
            }
            else if(matcher3.matches()) {
            	parseDataWithPattern(allData, matcher3);
            }
            else if(matcher4.matches()) {
            	parseDataWithPattern(allData, matcher4);
            }
            
            else {
               // System.err.println("No match for: " + line);
            }
        }
        
        
        
        return allData;
    }

	public static void parseDataWithPattern(List<Map<String, Object>> allData, Matcher matcher1) {
		
		SymbolOptionData symbolOptionData = SymbolOptionParser.parseOptionSymbol(matcher1.group(3));
		if(symbolOptionData!=null) {
			Map<String,Object> data=new HashMap<>();
		    data.put("exchange" , matcher1.group(2));
		    data.put("tradingsymboldetails" , symbolOptionData);
		    data.put("symboltoken" , matcher1.group(4));
		    data.put("orginaloptionsymbol" , matcher1.group(3));
		    
		    //System.err.println("orginaloptionsymbol==="+matcher1.group(3));
		    allData.add(data);
		}
	}
}
