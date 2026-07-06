package com.trade.broker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.broker.dto.RawStock;

@Component
public class FnoCSVData {

    
    public Map<String,String>  readCSVData() throws IOException {
        
    	 Map<String,String> stockDataMap = new HashMap<>();

         // Load the CSV file from the resources folder
         ClassPathResource resource = new ClassPathResource("fno_stock_list.csv");

         try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
             String line;
             boolean isFirstLine = true;
             while ((line = br.readLine()) != null) {
                 // Skip the header row
                 if (isFirstLine) {
                     isFirstLine = false;
                     continue;
                 }

                 // Split the CSV row using comma as delimiter
                 String[] tokens = line.split(",");
                 if (tokens.length >= 3) {
                     String stockName = tokens[0].trim();
                     String symbol = tokens[1].trim();
                     
                     stockDataMap.put(symbol,stockName);
                 }
             }
         }
         
       return stockDataMap;
    }
    
    
    
    public  Map<String,RawStock>  readDataFromOpenAPIScripMaster(String exchange) {
        String url = "https://margincalculator.angelbroking.com/OpenAPI_File/files/OpenAPIScripMaster.json";
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,RawStock> exchangeStockmap=new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(new URL(url));
           // System.out.println(rootNode.toPrettyString()); // Print formatted JSON
            
            List<JsonNode> nfoNodes = StreamSupport.stream(rootNode.spliterator(), false)
                    .filter(node -> exchange.equals(node.path("exch_seg").asText()))
                    .collect(Collectors.toList());
                
            // Print the filtered JSON nodes
           // nfoNodes.forEach(node -> System.out.println(node.toPrettyString()));
            
            for(JsonNode jsonNode:nfoNodes) {
            	String name=jsonNode.path("name").asText();
            	String exch_seg=jsonNode.path("exch_seg").asText();
            	String lotsize=jsonNode.path("lotsize").asText();
            	String symbol=jsonNode.path("symbol").asText();
            	String instrumenttype=jsonNode.path("instrumenttype").asText();
            	
            	//System.out.println("name : "+name+","+"symbol : "+symbol +", lotsize : "+lotsize);
            	
            	RawStock rawStock=new RawStock(name,exch_seg,lotsize,symbol,instrumenttype);
            	exchangeStockmap.put(name, rawStock);
            }
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return exchangeStockmap;
    }
    
    
    
}

