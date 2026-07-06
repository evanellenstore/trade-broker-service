package com.trade.broker.service;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReadJsonExample {
    public static void main(String[] args) {
        String url = "https://margincalculator.angelbroking.com/OpenAPI_File/files/OpenAPIScripMaster.json";
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(new URL(url));
           // System.out.println(rootNode.toPrettyString()); // Print formatted JSON
            
            List<JsonNode> nfoNodes = StreamSupport.stream(rootNode.spliterator(), false)
                    .filter(node -> "NFO".equals(node.path("exch_seg").asText()))
            		//.filter(node -> "FSL".equals(node.path("name").asText()))
                    .collect(Collectors.toList());
                
                // Print the filtered JSON nodes
               
            
           // nfoNodes.forEach(node -> System.out.println(node.toPrettyString()));
            
            Map<String,String> map=new HashMap<>();
            
            for(JsonNode jsonNode:nfoNodes) {
            	String name=jsonNode.path("name").asText();
            	String exch_seg=jsonNode.path("exch_seg").asText();
            	map.put(name, exch_seg);
            }
            
            
            
            for(Map.Entry<String,String> entry:map.entrySet()) {
            	System.out.println(entry.getKey()+", "+entry.getValue());
            }
            
            
            
            
            System.err.println(" entry  :"+map.size());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

