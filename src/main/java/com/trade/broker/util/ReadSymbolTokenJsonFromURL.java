package com.trade.broker.util;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReadSymbolTokenJsonFromURL {
    public static void main(String[] args) {
        String url = "https://margincalculator.angelbroking.com/OpenAPI_File/files/OpenAPIScripMaster.json";
        ObjectMapper objectMapper = new ObjectMapper();
        
        //OPTFUT (Commodity options)
        //FUTSTK (Stock futures)
        //OPTSTK (Stock options)

        try {
        	
        	
        	JsonNode rootNode = objectMapper.readTree(new URL(url));
        	
        	int i=0;
        	if (rootNode.isArray()) {
                for (JsonNode element : rootNode) {
                	
                	
                	if (element.has("instrumenttype")) {
                		
                	    String instrumentType = element.get("instrumenttype").asText();
                	    if (instrumentType.equals("FUTSTK") && element.has("exch_seg") && "NSE".equals(element.get("exch_seg").asText())
                	    		
                	    		) {
                	        System.out.println(element.toPrettyString());
                	        i++;
                	    }
                	    /*
                		 List<JsonNode> stockList = new ArrayList<>();
                		
                		if (element.has("exch_seg") && "NSE".equals(element.get("exch_seg").asText())) {
                            
                            stockList.add(element);
                           
                        }
                		
                		
                		
                		 List<JsonNode> top500Stocks = stockList.stream()
                                 .sorted(Comparator.comparing(stock -> stock.get("token").asText())) // Sort by token (Modify if needed)
                                 .limit(500) // Get Top 500
                                 .collect(Collectors.toList());
                		 
                		 
                		 top500Stocks.forEach(stock -> System.out.println(stock.toPrettyString()));
                		*/
                	}
                	
                	
                    
                   
                }
            }
        System.err.println("-----------total--"+i);	
        	 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

