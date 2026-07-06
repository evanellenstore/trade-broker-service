package com.trade.broker.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.broker.algo.SmartApiLogin;
import com.trade.broker.constant.TRADEConstants;
import com.trade.broker.dto.Nofifitcation;
import com.trade.broker.dto.StockChange;
import com.trade.broker.entity.DBTokenDetail;
import com.trade.broker.entity.FNOStockDetail;
import com.trade.broker.entity.MarketDataHistorical;
import com.trade.broker.entity.TopGainerAndLooserStocks;
import com.trade.broker.entity.TradeEntryStock;
import com.trade.broker.exception.TradeScheduleBusinessException;
import com.trade.broker.service.EmailService;
import com.trade.broker.service.MarketDataHistoricalService;
import com.trade.broker.service.TopGainerAndLooserStockService;
import com.trade.broker.service.TradeEntryStockService;
import com.trade.broker.util.INTERVAL;
import com.trade.broker.util.OptionDataParser;
import com.trade.broker.util.SymbolOptionData;
import com.trade.broker.util.TRADEDateUtil;
import com.trade.broker.util.TradeStatus;

import freemarker.template.TemplateException;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.search.FromStringTerm;
import jakarta.mail.search.SearchTerm;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class IntraDayAlgoStagiesHelper {
	
	private WebDriver driver = null;

	@Autowired
	private SmartApiLogin smartApiLogin;
	
	@Autowired
	private TradeEntryStockService tradeEntryStockService;
	
	@Autowired
	private TopGainerAndLooserStockService topGainerAndLooserStockService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private MarketDataHistoricalService marketDataHistoricalService;
	
	@Value("${stock.selection.from.time.hour}")
	private int stock_selection_from_time_hour;
	
	@Value("${stock.selection.from.time.minute}")
	private int stock_selection_from_time_minute;
	
	@Value("${stock.selection.to.time.hour}")
	private int stock_selection_to_time_hour;
	
	@Value("${stock.selection.to.time.minute}")
	private int stock_selection_to_time_minute;
	
	
	
	@Value("${prev.stock.selection.from.time.hour}")
	private int prev_stock_selection_from_time_hour;
	
	@Value("${prev.stock.selection.from.time.minute}")
	private int prev_stock_selection_from_time_minute;
	
	@Value("${prev.stock.selection.to.time.hour}")
	private int prev_stock_selection_to_time_hour;
	
	@Value("${prev.stock.selection.to.time.minute}")
	private int prev_stock_selection_to_time_minute;
	
	

	@Value("${stock.entry.from.time.hour}")
	private int stock_entry_from_time_hour;
	
	@Value("${stock.entry.from.time.minute}")
	private int stock_entry_from_time_minute;
	
	@Value("${stock.entry.to.time.hour}")
	private int stock_entry_to_time_hour;
	
	@Value("${stock.entry.to.time.minute}")
	private int stock_entry_to_time_minute;
	

	
	/**
	 * 
	 * @param tradeEntryStock
	 * @param lotsize
	 * @param lotquantity
	 * @return
	 * @throws IOException
	 * @throws SmartAPIException
	 */
	public Order placeBuyOrder(TradeEntryStock tradeEntryStock, long lotsize, int lotquantity) throws IOException, SmartAPIException {
		
	return smartApiLogin.placeBuyOrder(tradeEntryStock,lotsize,lotquantity);
		
	}
	
	/**
	 * 
	 * @param tradeEntryStock
	 * @param lotsize
	 * @param lotquantity
	 * @return
	 * @throws IOException
	 * @throws SmartAPIException
	 */
	public Order placeSellOrder(TradeEntryStock tradeEntryStock, long lotsize, int lotquantity) throws IOException, SmartAPIException {
		
		return smartApiLogin.placeSellOrder(tradeEntryStock,lotsize,lotquantity);
			
	}
	
	/**
	 * 
	 * @param topGainerTradeEntryStock
	 * @param topLooserTradeEntryStock
	 * @param candleType
	 */
	public void openConnection(List<TradeEntryStock>topGainerTradeEntryStock,List<TradeEntryStock> topLooserTradeEntryStock,String candleType) {
		
		smartApiLogin.getSocketConnection(topGainerTradeEntryStock,topLooserTradeEntryStock,candleType) ;
		
	}
	


	
	/**
	 * 
	 * @param stockName
	 * @param targetStrickPrice
	 * @param optionType
	 * @throws TradeScheduleBusinessException
	 */
	public Map<String,Object> pickOptionStock(String stockName, int targetStrickPrice,String optionType) throws TradeScheduleBusinessException {
		
		
		//M&MFIN
		//BAJAJ-AUTO
		//M&M
		
		//ASIANPAINT
		
		/*
		
		 String response=null;
		try {
			response = smartApiLogin.getSearchScrip("NFO","ASIANPAINT");
			List<Map<String,Object>> dataList=OptionDataParser.parseOptionData(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SmartAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */
		
		
		/*
		String response=null;
		
				List<FNOStockDetail> list=fNOStockService.findByExchange("NSE");
				for(FNOStockDetail fNOStockDetail:list) {
					System.err.println("-----------------"+fNOStockDetail.getExchange()+","+fNOStockDetail.getOrginaltradingsymbol()+","+fNOStockDetail.getTradingsymbol()+","+fNOStockDetail.getSymboltoken());
				
					try {
						 response = smartApiLogin.getSearchScrip("NFO",fNOStockDetail.getOrginaltradingsymbol());
						 List<Map<String,Object>> dataList=OptionDataParser.parseOptionData(response);
						
						//System.err.println(response);
						dealy(1000);
					} catch (IOException | SmartAPIException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
				
			*/		
			
		String response=null;
		try {
			response = smartApiLogin.getSearchScrip("NFO",stockName);
		} catch (IOException | SmartAPIException e) {
			System.err.println("Issue in pickOptionStock while getting FNO details for stockName : "+stockName+" exchange: NFO "+e.getMessage());
		}
		if(response==null) {
			System.err.println("Issue in pickOptionStock response is no response while getting FNO details for stockName : "+stockName+" exchange: NFO ");
		}
		
		
		
		//Parsing all with no filter
		List<Map<String,Object>> dataList=OptionDataParser.parseOptionData(response);
		
		
		//Define threshold as 30 days in milliseconds.
		final long thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000;
		List<Map<String, Object>> day30List= this.filterOptionFor30DayExpiry(dataList,thirtyDaysInMillis);
		//filter Option For optionType CE or PE
		List<Map<String, Object>> optionTypeList= this.filterOptionType(day30List,optionType);
		//Strick price sorted desc list
	
		Optional<Map<String, Object>> strickPriceOptionTypeList= this.filterOptionStrickPrice(optionTypeList,targetStrickPrice);
	
		//=====================================
		/*
		optionTypeList.stream()
		.filter(map -> map.get("tradingsymboldetails") instanceof SymbolOptionData)
		.map(map -> ((SymbolOptionData) map.get("tradingsymboldetails")))
		.forEach(strike -> System.out.println(" " + strike));
		System.out.println("");
		
		strickPriceOptionTypeList.stream()
		.filter(map -> map.get("tradingsymboldetails") instanceof SymbolOptionData)
		.map(map -> ((SymbolOptionData) map.get("tradingsymboldetails")))
		.forEach(strike -> System.err.println(" -------- After filter Strike Price: " + strike+" ------- "));
		*/
		//adding some dealy for multiple request
				
		TRADEDateUtil.dealy(1000);
		
		Map<String,Object> selectedFNO=null;
		if(strickPriceOptionTypeList.isPresent()) {
			selectedFNO=strickPriceOptionTypeList.get();
		}else{
			
			System.err.println("Not present response is no response while "+ "getting FNO details for stockName : "+stockName+" exchange: NFO ");
		}
		
		return selectedFNO;

	}
	
	/**
	 * 
	 * @param fetched
	 * @param opertor
	 * @return
	 */
	public int calculateTargetStrikePrice(double ltp,String opertor) {
		double ltp2percent=0;
		if(opertor.equalsIgnoreCase("CE")) {
			 ltp2percent=ltp+((ltp*2)/100);
		}else if(opertor.equalsIgnoreCase("PE")) {
			ltp2percent=ltp-((ltp*2)/100);
		}
		int targetStrickPrice = (int) ltp2percent;
		return targetStrickPrice;
		
	}
	
	
	/**
	 * 
	 * @param dataList
	 * @param thirtyDaysInMillis
	 * @return
	 */
	public List<Map<String, Object>> filterOptionFor30DayExpiry(List<Map<String, Object>> dataList,
			long thirtyDaysInMillis) {
		Date now = new Date();
		List<Map<String, Object>> newdataList = dataList.stream().filter(map -> {
			Object detail = map.get("tradingsymboldetails");
			if (!(detail instanceof SymbolOptionData)) {
				return false;
			}
			SymbolOptionData optionData = (SymbolOptionData) detail;
			Date expiry = optionData.getExpiry();
			long diff = Math.abs(expiry.getTime() - now.getTime());
			return diff <= thirtyDaysInMillis;
		}).collect(Collectors.toList());
		return newdataList;
	}
	
	/**
	 * 
	 * @param topGainerAndLooserStocks
	 * @param optionType
	 * @param gainerOrLooser
	 * @throws TradeScheduleBusinessException
	 */
	public List<TradeEntryStock> startWorkOnTopGainerOrLooser(TopGainerAndLooserStocks topGainerAndLooserStocks,String optionType) throws TradeScheduleBusinessException  {
		List<TradeEntryStock> saveTradeEntryStockList=new ArrayList<>();
		ObjectMapper objectMapper=new ObjectMapper();
		String jsonString=topGainerAndLooserStocks.getJsonData();
		List<StockChange> stockList=null;
		try {
			stockList = objectMapper.readValue(jsonString, new TypeReference<List<StockChange>>() {});
		} catch (JsonProcessingException e) {
			throw new TradeScheduleBusinessException("Issue in startWorkOnTopGainer while parsing json string "+e.getMessage(),e);
		}
		
		//iterate selected stock
		for(StockChange stock:stockList) {
			String tradingsymbol=stock.getStockName();
			String symboltoken=stock.getSymboltoken();
			//JSONObject currentMarketData = smartApiLogin.getMarketData("FULL", symboltoken,"NSE");
			String fromdate = TRADEDateUtil.dynamicDate(stock_entry_from_time_hour, stock_entry_from_time_minute);
			String todate = TRADEDateUtil.dynamicDate(stock_entry_to_time_hour, stock_entry_to_time_minute);
			JSONArray candleData = smartApiLogin.getCandleData(symboltoken,INTERVAL.MINUTE_5.getValue(), fromdate, todate);
			
			if (candleData!=null) {
				JSONArray lastCandle = candleData.getJSONArray(candleData.length() - 1);
				if (lastCandle != null) {
		            //colse is equal to ltp
		            double close = lastCandle.getDouble(4);
					//Pick option stock on 2 % percent increase/decrease base on PE or CE
					int targetStrickPrice = this.calculateTargetStrikePrice(close,optionType);
					String optionStockName=tradingsymbol.substring(0, tradingsymbol.length() - 3);
					System.err.println("LTP price : "+close+" target Strick Price is 2% of LTP :"+targetStrickPrice+" stockName : "+optionStockName+" optionType: "+optionType);
					Map<String,Object> selectedFNO=this.pickOptionStock(optionStockName,targetStrickPrice,optionType);
					//Store High/Low price in DB based on PE/CE at 9:25 so get LTP
					if (selectedFNO != null) {
						TradeEntryStock saveTradeEntryStock = saveOrUpdateTrade(tradingsymbol, symboltoken, lastCandle,
								selectedFNO, optionType);
						saveTradeEntryStockList.add(saveTradeEntryStock);
					}
	        }else {
	        	 System.out.println("No last candle Data");
	        }
		
		}else {
			 System.out.println("No enough candle Data");
		}
		
		}
		return saveTradeEntryStockList;
	}
	
	/**
	 * 
	 * @param tradingsymbol
	 * @param symboltoken
	 * @param fetched
	 * @throws TradeScheduleBusinessException
	 */
	public TradeEntryStock saveOrUpdateTrade(String tradingsymbol, String symboltoken, JSONArray lastcandleData,Map<String,Object> selectedFNO,String optionType)
			throws TradeScheduleBusinessException {
		if(lastcandleData==null) {
			System.err.println("issue in startWorkOnTopGainer while getting market data for symboltoken : "+symboltoken);
		}
		TradeEntryStock tradeEntryStock=tradeEntryStockService.fetchTodayStocksymboltokenAndTradestatus(symboltoken,TradeStatus.OPEN.getValue());
		if(tradeEntryStock==null) {
			tradeEntryStock=new TradeEntryStock();
		}
						
		tradeEntryStock.setStocksymboltoken(symboltoken);
		tradeEntryStock.setStocktradingsymbol(tradingsymbol);
		tradeEntryStock.setExchange("NSE");
		tradeEntryStock.setTradestatus(TradeStatus.OPEN.getValue());
		tradeEntryStock.setGainerorlooser(optionType);
		
		if ("CE".equalsIgnoreCase(optionType) && tradeEntryStock.getHighpoint() == 0
				&& tradeEntryStock.getHighbreakdown() == null && tradeEntryStock.getHighbreakup() == null) {
			// pick 9:25 high point only one
			tradeEntryStock.setHighpoint(lastcandleData.getDouble(2));
			tradeEntryStock.setHighbreakdown("No");
			tradeEntryStock.setHighbreakup("No");
		} else if ("PE".equalsIgnoreCase(optionType) && tradeEntryStock.getLowpoint() == 0
				&& tradeEntryStock.getLowbreakdown() == null && tradeEntryStock.getLowbreakup() == null) {
			// pick 9:25 low point only one
			tradeEntryStock.setLowpoint(lastcandleData.getDouble(3));
			tradeEntryStock.setLowbreakup("No");
			tradeEntryStock.setLowbreakdown("No");
		}
		
		tradeEntryStock.setUpdTimestamp(TRADEDateUtil.getCurrentJavaSqlTimestamp());
		if (tradingsymbol.endsWith("-EQ")) {
		    tradeEntryStock.setOrginaltradingsymbol(   tradingsymbol.substring(0, tradingsymbol.length() - 3));
		}
			
		SymbolOptionData symbolOptionData=(SymbolOptionData)selectedFNO.get("tradingsymboldetails");
		Date optinexpiry=symbolOptionData.getExpiry();
		//Format the date and convert to uppercase (to ensure month abbreviations are uppercase)
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMMyyyy", Locale.ENGLISH);
	    String expiryformattedDate = formatter.format(optinexpiry).toUpperCase();
		
		tradeEntryStock.setOptiontradingsymbol(symbolOptionData.getUnderlying());
		tradeEntryStock.setOptionsymboltoken(String.valueOf(selectedFNO.get("symboltoken")));
		tradeEntryStock.setOptionexpiry(expiryformattedDate);
		tradeEntryStock.setOptionstrikeprice(symbolOptionData.getStrike());
		tradeEntryStock.setOptionorginaltradingsymbol(String.valueOf(selectedFNO.get("orginaloptionsymbol")));
		TradeEntryStock saveTradeEntryStock=tradeEntryStockService.saveOrUpdate(tradeEntryStock);
		return saveTradeEntryStock;
		// JSONObject optionChain=smartApiLogin.getOptionChain(orginalStockName, expiryformattedDate);
		// System.err.println("optionChain==="+optionChain.toString());
			     
	}
	
	
	/**
	 * 
	 * @param dataList
	 * @param optionType
	 * @return
	 */
	public List<Map<String, Object>> filterOptionType(List<Map<String, Object>> dataList, String optionType) {
	    return dataList.stream().filter(map -> {
	        Object detail = map.get("tradingsymboldetails");
	        if (!(detail instanceof SymbolOptionData)) {
	            return false;
	        }
	        SymbolOptionData optionData = (SymbolOptionData) detail;
	        return optionData.getOptionType().equalsIgnoreCase(optionType);
	    }).collect(Collectors.toList());
	}
	
	
	
	/**
	 * 
	 * @param dataList
	 * @param targetStrike
	 * @return
	 */
	public Optional<Map<String, Object>> filterOptionStrickPrice(List<Map<String, Object>> dataList, int targetStrike ) {

		
		return dataList.stream()
		        .filter(map -> map.get("tradingsymboldetails") instanceof SymbolOptionData)
		        .min(Comparator.comparingInt(map -> {
		            SymbolOptionData optionData = (SymbolOptionData) map.get("tradingsymboldetails");
		            return Math.abs(optionData.getStrike() - targetStrike);
		        }));
	}
	

	
	
	

	/**
	 * 
	 * @throws JsonProcessingException
	 */
	public void saveTradeRedCandleForLooser() throws JsonProcessingException {
		// top looser
		TopGainerAndLooserStocks topLoosersOpenIntrestMoreThan7PercentList = topGainerAndLooserStockService
				.getTopGainerAndLooser(TRADEConstants.TYPE_LOOSER, TRADEConstants.EXCHANGE,
						TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_LOOSER);
		long topGainersId=topLoosersOpenIntrestMoreThan7PercentList.getId();
		
		String topLoosersOpenIntrestMoreThan7PercentJsonString=topLoosersOpenIntrestMoreThan7PercentList.getJsonData();

		
		topGainerAndLooserStockService.saveTopGainerAndLooser(TRADEConstants.MY_STRATEGIES1, TRADEConstants.TYPE_LOOSER,
				TRADEConstants.EXCHANGE, TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_LOOSER,
				topLoosersOpenIntrestMoreThan7PercentJsonString, TRADEConstants.NIFITY50_CANDLE_RED_CALCULATED,topGainersId);

	}
	/**
	 * 
	 * @param candleType
	 * @throws JsonProcessingException
	 */
	public void saveTradeCandleForLooserAndGainer(String candleType) throws JsonProcessingException {
		// top gainer
		TopGainerAndLooserStocks topGainersOpenIntrestMoreThan7PercentList = topGainerAndLooserStockService
				.getTopGainerAndLooser(TRADEConstants.TYPE_GAINER, TRADEConstants.EXCHANGE,
						TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_GAINER);
		
		long topGainersId=topGainersOpenIntrestMoreThan7PercentList.getId();
		
		String topGainersOpenIntrestMoreThan7PercentJsonString=topGainersOpenIntrestMoreThan7PercentList.getJsonData();
		
		
		topGainerAndLooserStockService.saveTopGainerAndLooser(TRADEConstants.MY_STRATEGIES1, TRADEConstants.TYPE_GAINER,
				TRADEConstants.EXCHANGE, TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_GAINER,
				topGainersOpenIntrestMoreThan7PercentJsonString, candleType,topGainersId);

		// top looser
		TopGainerAndLooserStocks topLoosersOpenIntrestMoreThan7PercentList = topGainerAndLooserStockService
				.getTopGainerAndLooser(TRADEConstants.TYPE_LOOSER, TRADEConstants.EXCHANGE,
						TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_LOOSER);
		long topLoosersId=topLoosersOpenIntrestMoreThan7PercentList.getId();
		String topLoosersOpenIntrestMoreThan7PercentJsonString = topLoosersOpenIntrestMoreThan7PercentList.getJsonData();

		topGainerAndLooserStockService.saveTopGainerAndLooser(TRADEConstants.MY_STRATEGIES1, TRADEConstants.TYPE_LOOSER,
				TRADEConstants.EXCHANGE, TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_LOOSER,
				topLoosersOpenIntrestMoreThan7PercentJsonString, candleType,topLoosersId);
	}
	/**
	 * 
	 * @param objectMapper
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	public void applyFilterMoreThan2percentChangesOnLTPToLooser(ObjectMapper objectMapper)
			throws JsonProcessingException, JsonMappingException {

		TopGainerAndLooserStocks withoutFilterLooser = topGainerAndLooserStockService
				.getTopGainerAndLooser(TRADEConstants.TYPE_LOOSER, TRADEConstants.EXCHANGE, TRADEConstants.TOP20LOOSER);

		if (withoutFilterLooser != null) {
			String top20Looser = withoutFilterLooser.getJsonData();
			List<StockChange> topLooserData = objectMapper.readValue(top20Looser,
					new TypeReference<List<StockChange>>() {
					});

			List<StockChange> topLosersMoreThan2PercentMoveList = topLooserData.stream()
					.filter(stock -> Math.abs(stock.getChangePercent()) > 2) // Filter stocks with absolute change > 2%
					.sorted(Comparator.comparingDouble(StockChange::getChangePercent)) // Sort in ascending order (most
																						// negative first)
					.collect(Collectors.toList());

			String topLooserMoreThan2PercentMoveListJsonString = new ObjectMapper()
					.writeValueAsString(topLosersMoreThan2PercentMoveList);

			topGainerAndLooserStockService.saveTopGainerAndLooser(TRADEConstants.MY_STRATEGIES1,
					TRADEConstants.TYPE_LOOSER, TRADEConstants.EXCHANGE,
					TRADEConstants.MORE_THAN_2_PERCENT_ON_LTP_LOOSER, topLooserMoreThan2PercentMoveListJsonString,
					TRADEConstants.NIFITY50_CANDLE_NOT_CALCULATED,withoutFilterLooser.getId());

		}

	}
	/**
	 * 
	 * @param objectMapper
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	public void applyFilterMoreThan2percentChangesOnLTPToGainer(ObjectMapper objectMapper)
			throws JsonProcessingException, JsonMappingException {

		TopGainerAndLooserStocks withoutFilterGainer = topGainerAndLooserStockService
				.getTopGainerAndLooser(TRADEConstants.TYPE_GAINER, TRADEConstants.EXCHANGE, TRADEConstants.TOP20GAINER);

		if (withoutFilterGainer != null) {
			String top20Gainer = withoutFilterGainer.getJsonData();

			List<StockChange> topGainersData = objectMapper.readValue(top20Gainer,
					new TypeReference<List<StockChange>>() {
					});

			List<StockChange> topGainersMoreThan2PercentMoveList = topGainersData.stream()
					.filter(stock -> stock.getChangePercent() > 2.0).collect(Collectors.toList());

			String topGainersMoreThan2PercentMoveListJsonString = new ObjectMapper()
					.writeValueAsString(topGainersMoreThan2PercentMoveList);

			topGainerAndLooserStockService.saveTopGainerAndLooser(TRADEConstants.MY_STRATEGIES1,
					TRADEConstants.TYPE_GAINER, TRADEConstants.EXCHANGE,
					TRADEConstants.MORE_THAN_2_PERCENT_ON_LTP_GAINER, topGainersMoreThan2PercentMoveListJsonString,
					TRADEConstants.NIFITY50_CANDLE_NOT_CALCULATED,withoutFilterGainer.getId());

		}

	}
	

	
	/**
	 * 
	 * @param type
	 * @param applyfilter
	 * @param messageSequnce
	 * @param message
	 * @param mapData
	 * @throws TradeScheduleBusinessException
	 */
	public void printStockData(String type, String applyfilter, String messageSequnce, String message,Map<String, Nofifitcation> mapData) throws TradeScheduleBusinessException
			 {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		TopGainerAndLooserStocks filterData = topGainerAndLooserStockService.getTopGainerAndLooser(type,
				TRADEConstants.EXCHANGE, applyfilter);
		
		System.out.println("*********************************************************************************************************************");

		if (filterData != null) {
			String dataString = filterData.getJsonData();
			String candleType=filterData.getNifity50candletype();
			
			List<StockChange> dataList=new ArrayList<>();
			try {
				dataList = objectMapper.readValue(dataString, new TypeReference<List<StockChange>>(){});
			} catch (JsonProcessingException e) {
				throw new TradeScheduleBusinessException("Issue in while print Stock Data type : "+type+", applyfilter: "+applyfilter+" message : "+message,e);
				
			}
			
			Nofifitcation nofifitcation=new Nofifitcation(dataList,candleType,message);	
			mapData.put(messageSequnce,nofifitcation);
			System.err.println("Total Records : " + dataList.size()+" " + type + " and apply filter :" + applyfilter+" candle type "+candleType);
		
			dataList.forEach(System.out::println);
			
			

		} else {
			System.err.println("No data present in db for type :" + type + " and applyfilter :" + applyfilter);
		}
		
		System.out.println("*********************************************************************************************************************");


	}
	/**
	 * 
	 * @param notificationData
	 * @param subject
	 * @throws TradeScheduleBusinessException 
	 * @throws MessagingException
	 * @throws IOException
	 * @throws TemplateException
	 */
	public void sendNotification( Map<String, Nofifitcation> notificationData,String subject) throws TradeScheduleBusinessException {
		
		
		Map<String, Object> model = new HashMap<>();
		model.put("stepCount", notificationData.size());

	    // Iterate over the expected keys (st1, st2, ..., st8)
	    for (int i = 1; i <= notificationData.size(); i++) {
	        String key = "st" + i;
	        Nofifitcation notification = notificationData.get(key);
	        if (notification != null) {
	            model.put("step" + i, notification.getDataList());
	            model.put("step" + i + "CandleType", notification.getCandleType());
	            model.put("step" + i + "Massege", notification.getMessage());
	        }
	    }
		
		
		try {
			if (TRADEConstants.NOTIFICATION_ENABLE)
				emailService.sendEmail("geniousamresh@gmail.com", subject, model);
			else
				System.err.println("Notification is disable");

		} catch (MessagingException | IOException | TemplateException e) {

			throw new TradeScheduleBusinessException(
					"issue in sendNotification for subject: " + subject + " message: " + e.getMessage(), e);
		}
		
			
		
	}
	
	
	/**
	 * 
	 * @param fromdate
	 * @param todate
	 * @param nifity50Token
	 * @param interval
	 * @return
	 * @throws TradeScheduleBusinessException 
	 */
	public String getCandleType(String fromdate, String todate, String nifity50Token, String interval) throws TradeScheduleBusinessException {
		String candleType = "";

		JSONArray candleData = smartApiLogin.getCandleData(nifity50Token, interval, fromdate, todate);
		// THE API GIVEN RESONSE LIKE [TIMESTAMP, OPEN, HIGH, LOW, CLOSE, VOLUME].
		
		if(candleData==null) {
			throw new TradeScheduleBusinessException("getCandleType: getting null response. Please relogin.");
		}
		 String candleJsonString = String.valueOf(candleData);
	
		if(candleJsonString!=null && candleJsonString.length()>0 )
		{
			JSONArray candles = new JSONArray(candleJsonString);
			JSONArray datacandle915 = candles.getJSONArray(0);
			JSONArray datacandle925 = candles.getJSONArray(1);

			String timestamp915 = datacandle915.getString(0);
			double open915 = datacandle915.getDouble(1);
			double high915 = datacandle915.getDouble(2);
			double low915 = datacandle915.getDouble(3);
			double close915 = datacandle915.getDouble(4);

			// System.out.println("-----timestamp915----"+timestamp915);
			// System.out.println("-----open915----"+open915);
			// System.out.println("-----high915----"+high915);
			// System.out.println("-----low915----"+low915);
			// System.out.println("-----close915----"+close915);

			String timestamp925 = datacandle925.getString(0);
			double open925 = datacandle925.getDouble(1);
			double high925 = datacandle925.getDouble(2);
			double low925 = datacandle925.getDouble(3);
			double close925 = datacandle925.getDouble(4);

			// System.out.println("-----timestamp925----"+timestamp925);
			// System.out.println("-----open925----"+open925);
			// System.out.println("-----high925----"+high925);
			// System.out.println("-----low925----"+low925);
			// System.out.println("-----close925----"+close925);

			double openPrice = open915;
			double closePrice = close925;

			if (closePrice > openPrice) {
				candleType = "Green";
			} else if (closePrice < openPrice) {
				candleType = "Red";
			} else {
				candleType = "Doji";
			}

			System.out.println("-----candleType----" + candleType);
		}
		



		return candleType;
	}
	
	
	
	

	/**
	 * 
	 * @param topGainersMoreThan2
	 * @param topLosersMoreThan2
	 * @return
	 * @throws InterruptedException
	 * @throws TradeScheduleBusinessException 
	 */
	public void getOpenInterestGainerAndLooser(List<StockChange> topGainersMoreThan2,
			List<StockChange> topLosersMoreThan2) throws InterruptedException, TradeScheduleBusinessException {

		// ADD OI IN TOP LOOSER
		if (topGainersMoreThan2 != null) {
			for (StockChange gainerStockChange : topGainersMoreThan2) {
				addOpenIntrestDetails(gainerStockChange);
			}
		}
		// ADD OI IN TOP LOOSER
		if (topLosersMoreThan2 != null) {
			for (StockChange looserStockChange : topLosersMoreThan2) {
				addOpenIntrestDetails(looserStockChange);

			}
		}

	}
	
	public void applyOpenInterestOnGainerAndLooser(ObjectMapper objectMapper)
			throws JsonProcessingException, JsonMappingException, InterruptedException, TradeScheduleBusinessException {

		TopGainerAndLooserStocks gainerStock = topGainerAndLooserStockService.getTopGainerAndLooser(
				TRADEConstants.TYPE_GAINER, TRADEConstants.EXCHANGE, TRADEConstants.MORE_THAN_2_PERCENT_ON_LTP_GAINER);

		if (gainerStock != null) {
			String gainer = gainerStock.getJsonData();

			List<StockChange> gainersData = objectMapper.readValue(gainer, new TypeReference<List<StockChange>>() {
			});

			// ADD OI IN TOP GAINER
			if (gainersData != null) {
				for (StockChange gainerStockChange : gainersData) {
					this.addOpenIntrestDetails(gainerStockChange);
				}
			}

			String addedOIJsonString = new ObjectMapper().writeValueAsString(gainersData);
			topGainerAndLooserStockService.saveTopGainerAndLooser(TRADEConstants.MY_STRATEGIES1,
					TRADEConstants.TYPE_GAINER, TRADEConstants.EXCHANGE,
					TRADEConstants.MORE_THAN_2_PERCENT_ON_LTP_GAINER, addedOIJsonString,
					TRADEConstants.NIFITY50_CANDLE_NOT_CALCULATED,gainerStock.getId());

		}

		TopGainerAndLooserStocks looserStock = topGainerAndLooserStockService.getTopGainerAndLooser(
				TRADEConstants.TYPE_LOOSER, TRADEConstants.EXCHANGE, TRADEConstants.MORE_THAN_2_PERCENT_ON_LTP_LOOSER);

		if (gainerStock != null) {
			String looser = looserStock.getJsonData();

			List<StockChange> looserData = objectMapper.readValue(looser, new TypeReference<List<StockChange>>() {
			});

			// ADD OI IN TOP LOOSER
			if (looserData != null) {
				for (StockChange looserStockChange : looserData) {
					addOpenIntrestDetails(looserStockChange);
				}
			}

			String addedOIJsonString = new ObjectMapper().writeValueAsString(looserData);
			topGainerAndLooserStockService.saveTopGainerAndLooser(TRADEConstants.MY_STRATEGIES1,
					TRADEConstants.TYPE_LOOSER, TRADEConstants.EXCHANGE,
					TRADEConstants.MORE_THAN_2_PERCENT_ON_LTP_LOOSER, addedOIJsonString,
					TRADEConstants.NIFITY50_CANDLE_NOT_CALCULATED,looserStock.getId());

		}

	}

	public void applyFilterMoreThan7percentChangesOIToGainerAndLooser(ObjectMapper objectMapper)
			throws JsonMappingException, JsonProcessingException, InterruptedException {

		TopGainerAndLooserStocks gainerStock = topGainerAndLooserStockService.getTopGainerAndLooser(
				TRADEConstants.TYPE_GAINER, TRADEConstants.EXCHANGE, TRADEConstants.MORE_THAN_2_PERCENT_ON_LTP_GAINER);

		if (gainerStock != null) {
			String gainer = gainerStock.getJsonData();
			List<StockChange> gainers2pData = objectMapper.readValue(gainer, new TypeReference<List<StockChange>>() {
			});

			List<StockChange> topGainersOpenIntrestMoreThan7PercentList = gainers2pData.stream()
					.filter(stock -> stock.getChangeOIPercent() > 7.0).collect(Collectors.toList());

			String topGainersOpenIntrestMoreThan7PercentJsonString = new ObjectMapper()
					.writeValueAsString(topGainersOpenIntrestMoreThan7PercentList);
			topGainerAndLooserStockService.saveTopGainerAndLooser(TRADEConstants.MY_STRATEGIES1,
					TRADEConstants.TYPE_GAINER, TRADEConstants.EXCHANGE,
					TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_GAINER, topGainersOpenIntrestMoreThan7PercentJsonString,
					TRADEConstants.NIFITY50_CANDLE_NOT_CALCULATED,gainerStock.getId());
		}

		TopGainerAndLooserStocks looserStock = topGainerAndLooserStockService.getTopGainerAndLooser(
				TRADEConstants.TYPE_LOOSER, TRADEConstants.EXCHANGE, TRADEConstants.MORE_THAN_2_PERCENT_ON_LTP_LOOSER);

		if (gainerStock != null) {
			String looser = looserStock.getJsonData();
			List<StockChange> looser2Data = objectMapper.readValue(looser, new TypeReference<List<StockChange>>() {
			});

			List<StockChange> topLosersOpenIntrestMoreThan7PercentList = looser2Data.stream()
					.filter(stock -> Math.abs(stock.getChangeOIPercent()) > 7) // Filter stocks with absolute change >
																				// 2%
					.sorted(Comparator.comparingDouble(StockChange::getChangePercent)) // Sort in ascending order (most
																						// negative first)
					.collect(Collectors.toList());

			String topLosersOpenIntrestMoreThan7PercentJsonString = new ObjectMapper()
					.writeValueAsString(topLosersOpenIntrestMoreThan7PercentList);
			topGainerAndLooserStockService.saveTopGainerAndLooser(TRADEConstants.MY_STRATEGIES1,
					TRADEConstants.TYPE_LOOSER, TRADEConstants.EXCHANGE,
					TRADEConstants.MORE_THAN_7_PERCENT_ON_OI_LOOSER, topLosersOpenIntrestMoreThan7PercentJsonString,
					TRADEConstants.NIFITY50_CANDLE_NOT_CALCULATED,looserStock.getId());

		}

	}

	public void addOpenIntrestDetails(StockChange stockChange) throws InterruptedException, TradeScheduleBusinessException {
		// GETTING LPA MARKET DATA
		JSONObject currentMarketData = smartApiLogin.getMarketData("FULL", stockChange.getSymboltoken(),"NSE");
		// GETTING PREVIOUS DATA MARKET DATA
		
		MarketDataHistorical prevMarketHoleData = marketDataHistoricalService.getPreviousDayLatestData(stockChange.getSymboltoken(), "NSE");
		
		if(prevMarketHoleData!=null) {
			JSONObject prevMarketData = new JSONObject(prevMarketHoleData.getMarketdata());
			long currentOI = this.getOpenIntrest(currentMarketData);
			long previousOI = this.getOpenIntrest(prevMarketData);
			double OIPercent = this.calculateOIMovePercent(currentOI, previousOI);
			stockChange.setCurrentOI(currentOI);
			stockChange.setPreviousOI(previousOI);
			stockChange.setChangeOIPercent(OIPercent);
		}else {
			System.err.println("MarketDataHistorical is not present for stock: "+stockChange.getStockName()+" then setting dummy ");
			stockChange.setCurrentOI(0);
			stockChange.setPreviousOI(0);
			stockChange.setChangeOIPercent(0);
			
		}
		

		Thread.sleep(500);
	}

	private long getOpenIntrest(JSONObject marketData) {
		long opnInterest = 0;
		if (marketData != null) {

			JSONArray fetchedList = marketData.getJSONArray("fetched");
			for (int i = 0; i < fetchedList.length(); i++) {

				JSONObject fetched = fetchedList.getJSONObject(i);
				opnInterest = fetched.getLong("opnInterest");

			}

		}

		return opnInterest;
	}

	/**
	 * 
	 * @param currentOI
	 * @param previousOI
	 * @return
	 */

	public double calculateOIMovePercent(long currentOI, long previousOI) {
		if (previousOI == 0) {
			return 0;
		}
		return ((double) (currentOI - previousOI) / previousOI) * 100;
	}

	
	
	/**
	 * 
	 * @param fnoStockDetailList
	 * @return
	 * @throws IOException
	 * @throws SmartAPIException
	 */
	public void getTopGainerAndLooser(List<FNOStockDetail> fnoStockDetailList)  {

		if (fnoStockDetailList != null) {
			List<StockChange> stockChanges = new ArrayList<>();
			for (FNOStockDetail fnoStockDetail : fnoStockDetailList) {
				String tradingsymbol = fnoStockDetail.getTradingsymbol();
				String symboltoken = fnoStockDetail.getSymboltoken();
				
				double todayClosePrice = 0;
				double prevClosePrice=0;
				
				
				TRADEDateUtil.dealy(200);
				
					String today_from = TRADEDateUtil.dynamicDate(stock_selection_from_time_hour,
							stock_selection_from_time_minute);
					String today_to = TRADEDateUtil.dynamicDate(stock_selection_to_time_hour, stock_selection_to_time_minute);

					//System.out.println("today_from: "+today_from+", today_to:"+today_to);
					
					
					JSONArray today_CandleData = smartApiLogin.getCandleData(symboltoken, INTERVAL.MINUTE_5.getValue(),
							today_from, today_to);

					if (today_CandleData != null && today_CandleData.length() > 0) {
						JSONArray todayCandle = today_CandleData.getJSONArray(today_CandleData.length() - 1);
						if (todayCandle != null)
							todayClosePrice = todayCandle.getDouble(4);
					} else {
						System.err.println("today Candle Data is empty for stock: " + tradingsymbol);
					}
					
				
					TRADEDateUtil.dealy(200);
					
					String prevday_from = TRADEDateUtil.dynamicPrevDate(prev_stock_selection_from_time_hour,
							prev_stock_selection_from_time_minute);
					String prevday_to = TRADEDateUtil.dynamicPrevDate(prev_stock_selection_to_time_hour,
							prev_stock_selection_to_time_minute);
					
					//System.out.println("prevday_from: "+prevday_from+", prevday_to:"+prevday_to);
					
					JSONArray prevday_CandleData = smartApiLogin.getCandleData(symboltoken,
							INTERVAL.MINUTE_5.getValue(), prevday_from, prevday_to);
					if (prevday_CandleData != null && prevday_CandleData.length() > 0) {
						JSONArray prevCandle = prevday_CandleData.getJSONArray(prevday_CandleData.length() - 1);
						if (prevCandle != null)
							prevClosePrice = prevCandle.getDouble(4);
					} else {
						System.err.println("prev day Candle Data is empty for stock: " + tradingsymbol);
					}				
					
				/*
					formula for calulate Change Percentis below
					Change Percent=((Current Price − Previous Price)/Previous Price)*100
				 
				 */
				double changePercent=0;
				 changePercent = ((todayClosePrice - prevClosePrice) / prevClosePrice) * 100;
				System.err.println("Getting stock: "+tradingsymbol+" todayClosePrice: " + todayClosePrice+", prevClosePrice: "+prevClosePrice+", changePercent: "+changePercent);
				
				stockChanges.add(new StockChange(tradingsymbol, todayClosePrice, prevClosePrice, changePercent, symboltoken));
				TRADEDateUtil.dealy(300);
			}

			List<StockChange> topLosers = stockChanges.stream()
					.sorted(Comparator.comparingDouble(StockChange::getChangePercent)) // Sort ascending
					.limit(20).collect(Collectors.toList());

			List<StockChange> topGainers = stockChanges.stream()
					.sorted(Comparator.comparingDouble(StockChange::getChangePercent).reversed()) // Sort descending
					.limit(20).collect(Collectors.toList());

			String topGainersJsonString=null;
			try {
				topGainersJsonString = new ObjectMapper().writeValueAsString(topGainers);
			} catch (JsonProcessingException e) {
				
				System.err.println("Issue while converting topGainers list to in json string");
			}
			String topLosersJsonString=null;
			try {
				topLosersJsonString = new ObjectMapper().writeValueAsString(topLosers);
			} catch (JsonProcessingException e) {
				System.err.println("Issue while converting topLosers list to in json string");
			}

			topGainerAndLooserStockService.saveTopGainerAndLooser(TRADEConstants.MY_STRATEGIES1,
					TRADEConstants.TYPE_GAINER, TRADEConstants.EXCHANGE, TRADEConstants.TOP20GAINER,
					topGainersJsonString, TRADEConstants.NIFITY50_CANDLE_NOT_CALCULATED,null);

			topGainerAndLooserStockService.saveTopGainerAndLooser(TRADEConstants.MY_STRATEGIES1,
					TRADEConstants.TYPE_LOOSER, TRADEConstants.EXCHANGE, TRADEConstants.TOP20LOOSER,
					topLosersJsonString, TRADEConstants.NIFITY50_CANDLE_NOT_CALCULATED,null);

		}

	}
	
	
	public void loginPortalSmartApi(String totp) {
		String cookiesNameTrade = "cookies.data";
		this.initAngleOneWeb();
		driver.get("https://www.angelone.in/login");
		driver.manage().window().maximize();
		try {
			this.loginAngleOne("R57698459", "0786", cookiesNameTrade);
			//this.closeBrowser();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		 
		
	}
	


	

	/**
	 * 
	 * @param totp
	 * @return
	 */
	public DBTokenDetail loginSmartApi(String totp) {
		DBTokenDetail dbTokenDetail = smartApiLogin.proccessMarketLogin(totp);

	
		return dbTokenDetail;

	}

	public String reLoginSmartApi() {
		return smartApiLogin.proccessReLogin();
	}
	

	

	public void logutSmartApi() {
		smartApiLogin.logout();

		driver.manage().deleteAllCookies();
		File cookieFile1 = new File("cookies.data");
		if (cookieFile1.exists() && cookieFile1.delete()) {
			System.out.println("Cookie file deleted from disk.");
		}

	}


	/**
	 * 
	 */
	public void readTopGainerAndLooserFromAngleOnePoratal() {
		loadCookie("https://www.angelone.in/login");
		try {

			System.err.println("-------------------------------------");
			List<Map<String, String>> topGainer = this.getTopGainer();
			System.out.println(topGainer);

			System.err.println("-------------------------------------");
			List<Map<String, String>> topLooserList = this.getTopLooser();
			System.out.println(topLooserList);

		} catch (InterruptedException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		// close browser
		//this.closeBrowser();

	}
	
	
	public void loadCookie(String url) {
		driver.get(url);
		driver.manage().window().maximize();
		String cookiesNameTrade = "cookies.data";

		File cookieFile = new File(cookiesNameTrade);
		if (cookieFile.exists()) {
			Set<Cookie> cookies = new HashSet<>();
			try {
				cookies = this.loadCookies(driver, cookiesNameTrade);
			} catch (ClassNotFoundException | IOException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}

			for (Cookie cookie : cookies) {
				driver.manage().addCookie(cookie);
			}
			driver.navigate().refresh();
		} else {
			try {
				this.loginAngleOne("R57698459", "0786", cookiesNameTrade);
			} catch (InterruptedException | IOException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
		
	}
	
	
	public void createWatchList(String activeTrade) {
		loadCookie("https://www.angelone.in/trade/watchlist/chart");
		try {
			
			Thread.sleep(1000);
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			WebElement gotItButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='GOT IT']")));
			gotItButton.click();
			Thread.sleep(1000);
			//WebElement notNowButton = driver.findElement(By.xpath("//button[text()='NOT NOW']"));
			//notNowButton.click();
			//Thread.sleep(500);
			
			
			//WebElement input = driver.findElement(By.cssSelector("input#search_header"));
			WebElement input = driver.findElement(By.id("watchlist-search"));
			input.sendKeys(activeTrade);
			
			//Thread.sleep(1000);
			WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
			WebElement container = wait1.until(
			    ExpectedConditions.visibilityOfElementLocated(By.id("wlSearch"))
			);

			// Find all stock‐row elements
			List<WebElement> rows = container.findElements(By.cssSelector("div[id^='watchlist-']"));
			String targetStock   = "COFORGE";
			String targetExchange = "NSE";
			for (WebElement stock : rows) {
	            try {
	                //String name = stock.findElement(By.cssSelector("span.text-skin-bold.text-sm")).getText();
	                //String exchange = stock.findElement(By.cssSelector("span.bg-skin-select")).getText();
	                
	                String name = stock.findElement(By.cssSelector("span.text-base")).getText().trim();
                    String exchange = stock.findElement(By.cssSelector("div.inline-block.content-center")).getText().trim();


	                if (name.equalsIgnoreCase(targetStock) && exchange.equalsIgnoreCase(targetExchange)) {
	                    // Found matching stock, now click its plus button
	                	WebElement plusBtn = wait.until(ExpectedConditions.elementToBeClickable(
	                            By.cssSelector("button > span.icon-add-to-watchlist1")
	                        ));
	                   // WebElement plusBtn = stock.findElement(By.cssSelector("button span.icon-plus"));
	                    plusBtn.click();
	                    System.out.println("Clicked plus button for: " + targetStock + " - " + targetExchange);
	                    break;
	                }
	            } catch (Exception e) {
	                // In case of missing elements, skip
	            	e.printStackTrace();
	                System.out.println("Skipping a non-matching or malformed entry");
	            }
			
			}
			
			
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */

	private List<Map<String, String>> getTopLooser() throws InterruptedException {

		List<Map<String, String>> rowDataList = new ArrayList<>();

		driver.get("https://www.angelone.in/trade/home");
		// this.monitorLog();
		Thread.sleep(500);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0, 1000);");
		Thread.sleep(500);

		driver.get("https://www.angelone.in/trade/markets/equity/market-movers");
		Thread.sleep(500);
		WebElement losersElement = driver.findElement(By.xpath("//li[text()='Losers']"));

		losersElement.click();
		Thread.sleep(500);
		WebElement table = driver.findElement(By.xpath("//table[contains(@class, 'table-auto')]"));

		WebElement headerRow = table.findElement(By.tagName("thead")).findElement(By.tagName("tr"));
		List<WebElement> headerColumns = headerRow.findElements(By.tagName("th"));

		// Create a list of headers for mapping
		List<String> headers = headerColumns.stream().map(WebElement::getText).toList();

		List<WebElement> rows = table.findElements(By.xpath(".//tbody/tr"));

		for (WebElement row : rows) {
			List<WebElement> columns = row.findElements(By.tagName("td"));

			if (columns.size() == headers.size()) {
				Map<String, String> rowData = new HashMap<>();

				// Map each header to the corresponding column value
				for (int i = 0; i < columns.size(); i++) {
					rowData.put(headers.get(i), columns.get(i).getText());

				}

				rowDataList.add(rowData);
			}
		}

		return rowDataList;
	}

	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */

	private List<Map<String, String>> getTopGainer() throws InterruptedException {

		List<Map<String, String>> rowDataList = new ArrayList<>();

		driver.get("https://www.angelone.in/trade/home");
		// mainJava.monitorLog();

		// click popup
		// WebElement gotItButton = driver.findElement(By.cssSelector("bg-skin-royalblue
		// text-skin-white px-8 py-4 rounded font-semibold text-sm
		// leading-[1.063rem]"));
		// gotItButton.click();

		Thread.sleep(1000);
		
		List<WebElement> gotItButtons = driver.findElements(By.xpath("//button[normalize-space()='GOT IT']"));
		closePopup(gotItButtons,"GOT IT");

		Thread.sleep(1000);

		
		List<WebElement> notnowButtons = driver.findElements(By.xpath("//button[text()='NOT NOW']"));
		closePopup(notnowButtons,"NOT NOW");

		Thread.sleep(500);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0, 1000);");
		Thread.sleep(1000);
		
		
		
		List<WebElement> gainersButtons = driver.findElements(By.id("home|market_movers|top_gainers"));

        if (!gainersButtons.isEmpty()) {
            WebElement gainersButton = gainersButtons.get(0);
            if (gainersButton.isDisplayed() && gainersButton.isEnabled()) {
                gainersButton.click();
                System.out.println("Gainers button clicked!");
            } else {
                System.out.println("Gainers button found but not clickable.");
            }
        } else {
            System.out.println("Gainers button not found.");
        }
		
		

		//WebElement topGainersButton1 = driver.findElement(By.xpath("//button[text()='Top Gainers']"));
		//topGainersButton1.click();

		Thread.sleep(1000);
		// this.monitorLog();
		driver.get("https://www.angelone.in/trade/markets/equity/market-movers");
		Thread.sleep(1000);
		WebElement table = driver.findElement(By.xpath("//table[contains(@class, 'table-auto')]"));

		WebElement headerRow = table.findElement(By.tagName("thead")).findElement(By.tagName("tr"));
		List<WebElement> headerColumns = headerRow.findElements(By.tagName("th"));

		// Create a list of headers for mapping
		List<String> headers = headerColumns.stream().map(WebElement::getText).toList();

		List<WebElement> rows = table.findElements(By.xpath(".//tbody/tr"));

		for (WebElement row : rows) {
			List<WebElement> columns = row.findElements(By.tagName("td"));

			if (columns.size() == headers.size()) {
				Map<String, String> rowData = new HashMap<>();

				// Map each header to the corresponding column value
				for (int i = 0; i < columns.size(); i++) {
					rowData.put(headers.get(i), columns.get(i).getText());

				}

				rowDataList.add(rowData);
			}
		}

		return rowDataList;
	}

	/**
	 * 
	 * @param user
	 * @param password
	 * @param cookiesNameTrade
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void loginAngleOne(String user, String password, String cookiesNameTrade)
			throws InterruptedException, IOException {
	try {
		driver.get("https://www.angelone.in/login");
		driver.manage().window().maximize();
		
		System.err.println("1: ======window maxmized========");
		WebElement radioButton = driver.findElement(By.xpath("//input[@name='radio' and @value='2']"));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
		WebElement usernameField = driver.findElement(By.id("login|input|client_id"));
		usernameField.sendKeys(user);
		
		
		WebElement loginButton = driver.findElement(By.id("login|proceed|client_id"));
		loginButton.click();
		System.err.println("2: ======clicked on login btn========");
		
		System.out.println("Login button clicked!");
		Thread.sleep(3000);

		// read otp
		 String otp = this.emailReader();
		 System.err.println("3: ======read otp from mail========");
		 //Thread.sleep(9000);
		 
		 
		// check popup
		 
		 List<WebElement> buttons = driver.findElements(By.id("wzrk-confirm"));
	     closePopup(buttons,"wzrk-confirm");
		 
		 //===========
		 

		System.out.println(" the OTP :" + otp);
		WebElement otpField = driver.findElement(By.id("login|input|verify_otp"));
		otpField.sendKeys(otp);
		System.err.println("4: ======verify_otp otp========");
		/*
		WebElement checkbox = driver.findElement(By.id("login|otp|proceed"));
		if (!checkbox.isSelected()) {
			checkbox.click();
			System.out.println("Checkbox is now checked.");
		} else {
			System.out.println("Checkbox was already checked.");
		}
		*/
		
		//WebElement proceedButton = driver.findElement(By.xpath(
			//	"//button[@type='submit' and @class='bg-skin-royalblue text-skin-white  px-5 py-3 w-full rounded-lg font-medium uppercase text-sm md:mt-8 mt-7']"));
		Thread.sleep(3000);
		WebElement proceedButton = driver.findElement(By.id("login|otp|proceed"));
		proceedButton.click();
		Thread.sleep(3000);

		System.out.println("Login button clicked!");
		String pin = "0786";
		WebElement pinField = driver.findElement(By.id("login|input|mpin"));
		pinField.sendKeys(pin);
		System.out.println("Login pin clicked!");
		
		
		WebElement proceedButton1 = driver.findElement(By.id("login|mpin|proceed"));
		
		//WebElement proceedButton1 = driver.findElement(By.xpath(
			//	"//button[@type='submit' and @class='bg-skin-royalblue text-skin-white  px-5 py-3 w-full rounded-lg font-medium uppercase text-sm  md:mt-10 mt-8']"));
		proceedButton1.click();
		Thread.sleep(5000);
		List<WebElement> gotItButtons = driver.findElements(By.xpath("//button[normalize-space()='GOT IT']"));
		closePopup(gotItButtons,"GOT IT");

		saveCookies(driver, cookiesNameTrade);
	}catch (Exception e) {
		e.printStackTrace();
	}
		

	}

	/**
	 * 
	 * @return
	 */

	private String emailReader() {
		String otp = null;

		// Email credentials
		String host = "imap.gmail.com";
		String username = "renuaryanverma@gmail.com";
		String password = "xudj eccp clfq dzgr";

		try {
			// Set email server properties
			Properties properties = new Properties();
			properties.put("mail.imap.host", host);
			properties.put("mail.imap.port", "993");
			properties.put("mail.imap.ssl.enable", "true");
			properties.put("mail.imap.auth", "true");
			properties.put("mail.imap.connectiontimeout", "5000");
			properties.put("mail.imap.timeout", "10000");

			Session session = Session.getInstance(properties);
			session.setDebug(true);

			// Connect to the email server
			Store store = session.getStore("imap");
			store.connect(host, username, password);

			// Open the inbox folder
			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_WRITE);

			SearchTerm senderFilter = new FromStringTerm("donotreply@angelone.in");
			SearchTerm unreadFilter = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
			SearchTerm combinedFilter = new AndTerm(senderFilter, unreadFilter);

			Message[] messages = inbox.search(combinedFilter);

			System.out.println("Number of unread emails: " + messages.length);

			if (messages.length > 0) {
				// Fetch the latest email (assuming it's the last in the array)
				Message latestMessage = messages[messages.length - 1];

				// Display email details
				System.out.println("Subject: " + latestMessage.getSubject());
				System.out.println("From: " + latestMessage.getFrom()[0]);
				System.out.println("Sent Date: " + latestMessage.getSentDate());

				// Extract the content
				String content = getEmailContent(latestMessage);

				// Extract OTP using regex
				otp = extractOTP(content);
				if (otp != null) {
					latestMessage.setFlag(Flags.Flag.SEEN, true);
					System.out.println("Extracted OTP: " + otp);
				} else {
					System.out.println("OTP not found in the email content.");
				}
			} else {
				System.out.println("No unread emails from donotreply@angelone.in.");
			}

			// Close connections
			inbox.close(false);
			store.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return otp;
	}

	/**
	 * 
	 * @param content
	 * @return
	 */
	private String extractOTP(String content) {
		String otpRegex = "\\b\\d{6}\\b"; // Matches a 6-digit number
		Pattern pattern = Pattern.compile(otpRegex);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	/**
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private String getEmailContent(Message message) throws Exception {
		Object content = message.getContent();
		if (content instanceof String) {
			return (String) content;
		} else if (content instanceof Multipart) {
			Multipart multipart = (Multipart) content;
			StringBuilder contentBuilder = new StringBuilder();
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				contentBuilder.append(bodyPart.getContent().toString());
			}
			return contentBuilder.toString();
		}
		return null;
	}

	/**
	 * 
	 * @param driver
	 * @param cookiesName
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Set<Cookie> loadCookies(WebDriver driver, String cookiesName) throws IOException, ClassNotFoundException {
		Set<Cookie> cookies;
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(cookiesName))) {
			cookies = (Set<Cookie>) in.readObject();
		}
		return cookies;
	}

	/**
	 * 
	 * @param driver
	 * @param cookiesName
	 * @throws IOException
	 */
	private static void saveCookies(WebDriver driver, String cookiesName) throws IOException {
		Set<Cookie> cookies = driver.manage().getCookies();
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(cookiesName))) {
			out.writeObject(cookies);
		}
	}

	/**
	 * 	
	 */
	public void initAngleOneWeb() {
		//ChromeOptions options = new ChromeOptions();
		//options.setExperimentalOption("debuggerAddress", "localhost:9222");
		WebDriverManager.chromedriver().setup();
		//driver = new ChromeDriver(options);
		driver = new ChromeDriver();
		//driver.get("https://www.angelone.in/login");
	}

	private void closeBrowser() {
		driver.quit();
	}
	
	
	public void closePopup(List<WebElement> gotItButtons,String popup)
	{

        if (!gotItButtons.isEmpty()) {
            WebElement gotItButton = gotItButtons.get(0);
            if (gotItButton.isDisplayed() && gotItButton.isEnabled()) {
                gotItButton.click();
                System.out.println(popup+" button clicked!");
            } else {
                System.out.println(popup+" button found but not clickable.");
            }
        } else {
            System.out.println(popup+" button not found.");
        }
		
	}
	
	
	


}
