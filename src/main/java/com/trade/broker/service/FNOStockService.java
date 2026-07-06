package com.trade.broker.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.trade.broker.algo.SmartApiLogin;
import com.trade.broker.dto.RawStock;
import com.trade.broker.entity.FNOStockDetail;
import com.trade.broker.exception.TradeScheduleBusinessException;
import com.trade.broker.repository.FNOStockRepository;
import com.trade.broker.util.FnoCSVData;

@Service
public class FNOStockService {
	@Autowired
	private FNOStockRepository fnoStockRepository;
	
	@Autowired
	private FnoCSVData fnoCSVData;
	
	@Autowired
	private SmartApiLogin smartApiLogin;
	
	/**
	 * 
	 * @return
	 * @throws TradeScheduleBusinessException
	 */
	@Transactional
	public String insertFNOStock() throws TradeScheduleBusinessException {
		// get fno list data
		String result="Save data successfully ";
		try {
			Map<String, RawStock> fnoList=fnoCSVData.readDataFromOpenAPIScripMaster("NFO");
			//fnoList = fnoCSVData.readCSVData();
			
			for (Map.Entry<String, RawStock> entry : fnoList.entrySet()) {
				String response = smartApiLogin.getSearchScrip("NSE", entry.getKey());
				// extract Json From Response
				JSONObject resultData = this.extractJsonFromResponse(response, entry.getKey());
				String status = resultData.optString("status", "");
				if ("success".equals(status)) {
					System.out.println("Response was successful!");
					JSONArray filteredData = this.filterByTradingSymbolSuffix(resultData, "-EQ");
					if(filteredData!=null && filteredData.length()>0) {
						
						JSONObject stock = filteredData.getJSONObject(0);
						
		                String tradingSymbol = stock.optString("tradingsymbol", "");
		                String exchange = stock.optString("exchange", "");
		                String symboltoken = stock.optString("symboltoken", "");
		                
		                
		                FNOStockDetail dbStockDetail=fnoStockRepository.findByStackname(entry.getKey());
		                
		                
		                FNOStockDetail fnoStockDetail=new FNOStockDetail();
		                if(dbStockDetail!=null) {
		                	fnoStockDetail.setId(dbStockDetail.getId());
		                }
		                fnoStockDetail.setExchange(exchange);
		                fnoStockDetail.setTradingsymbol(tradingSymbol);
		                fnoStockDetail.setSymboltoken(symboltoken);
		                fnoStockDetail.setStackname(entry.getKey());
		                fnoStockDetail.setLotsize(Long.parseLong(entry.getValue().getLotsize()));
		                fnoStockDetail.setInstrumenttype(entry.getValue().getInstrumenttype());
		                
		                Pattern pattern = Pattern.compile(".*test$", Pattern.CASE_INSENSITIVE);
		                Matcher matcher = pattern.matcher(entry.getKey());
		                if (matcher.matches()) {
		                    fnoStockDetail.setLiveortest("test");
		                } else {
		                	fnoStockDetail.setLiveortest("live");
		                }
		                
		                FNOStockDetail  dbFNOStockDetail =fnoStockRepository.save(fnoStockDetail);
		                System.err.println(dbFNOStockDetail);
					}else{
						System.err.println("Not save :"+entry.getKey());
						System.err.println(response);
					}
					
				}
				Thread.sleep(1000);
			}
		} catch (IOException | SmartAPIException | InterruptedException e) {
			e.printStackTrace();
			result="Save data not successfully "+e.getMessage();
		}

		return result;
		

	}
	
	@Transactional	
	public List<FNOStockDetail> findByExchange(String exchange) throws TradeScheduleBusinessException {
		
		List<FNOStockDetail> data= fnoStockRepository.findByExchangeAndLiveortest(exchange,"live");
		if(data==null) {
			throw new TradeScheduleBusinessException("fno stock not present in db");
		}
		return data;
	}
	
	
	@Transactional	
	public FNOStockDetail findByStockName(String stockname) throws TradeScheduleBusinessException {
		
		FNOStockDetail data= fnoStockRepository.findByStackname(stockname);
		if(data==null) {
			throw new TradeScheduleBusinessException("fno stock not present in db");
		}
		return data;
	}
	
	
	/**
	 * 
	 * @param fnoStockDetail
	 * @return
	 * @throws TradeScheduleBusinessException
	 */
	@Transactional
	public FNOStockDetail updateFNOStock(FNOStockDetail fnoStockDetail) throws TradeScheduleBusinessException {
		
		return fnoStockRepository.save(fnoStockDetail);
	}
	
	/**
	 * 
	 * @param resultData
	 * @param suffix
	 * @return
	 */
	 private  JSONArray filterByTradingSymbolSuffix(JSONObject resultData, String suffix) {
	        JSONArray dataArray = resultData.optJSONArray("data");
	        JSONArray filteredArray = new JSONArray();

	        if (dataArray != null) {
	            for (int i = 0; i < dataArray.length(); i++) {
	                JSONObject stock = dataArray.getJSONObject(i);
	                String tradingSymbol = stock.optString("tradingsymbol", ""); // Avoid exceptions if missing

	                if (tradingSymbol.endsWith(suffix)) {
	                    filteredArray.put(stock); 
	                }
	            }
	        }
	        return filteredArray;
	    }
	/**
	 * 
	 * @param response
	 * @param orginaltradingsymbol
	 * @return
	 */
	private JSONObject extractJsonFromResponse(String response,String orginaltradingsymbol) {
	    JSONObject resultJson = new JSONObject();
	    JSONArray dataArray = new JSONArray();
	    
	    String[] lines = response.split("\n");
	    for (String line : lines) {
	        if (line.matches("\\d+\\. exchange: .*?, tradingsymbol: .*?, symboltoken: .*")) {
	            String[] parts = line.split(", ");
	            String exchange = parts[0].split(": ")[1];
	            String tradingSymbol = parts[1].split(": ")[1];
	            String symbolToken = parts[2].split(": ")[1];

	            JSONObject stockJson = new JSONObject();
	            stockJson.put("exchange", exchange);
	            stockJson.put("tradingsymbol", tradingSymbol);
	            stockJson.put("symboltoken", symbolToken);
	            
	            String matchString=orginaltradingsymbol+"-EQ";
	            if(matchString.equalsIgnoreCase(tradingSymbol)) {
	            	dataArray.put(stockJson);
	            }
	            
	        }
	    }

	    resultJson.put("status", "success");
	    resultJson.put("data", dataArray);

	    return resultJson;
	}

	
	
	
	

}
