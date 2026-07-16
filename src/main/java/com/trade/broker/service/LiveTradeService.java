package com.trade.broker.service;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.Order;
import com.trade.broker.algo.SmartApiLogin;
import com.trade.broker.algo.indicator.Candle;
import com.trade.broker.algo.indicator.CandleAnalyzer;
import com.trade.broker.algo.indicator.Ta4jSuperTrendCalculator;
import com.trade.broker.constant.TRADEConstants;
import com.trade.broker.domain.IntraDayAlgoStagiesHelper;
import com.trade.broker.entity.FNOStockDetail;
import com.trade.broker.entity.TradeEntryStock;
import com.trade.broker.exception.TradeScheduleBusinessException;
import com.trade.broker.util.DateSelection;
import com.trade.broker.util.INTERVAL;
import com.trade.broker.util.TRADEDateUtil;
import com.trade.broker.util.TradeStatus;
@Service
public class LiveTradeService {
	
	@Autowired
	private TradeEntryStockService tradeEntryStockService;
	
	@Autowired
	private SmartApiLogin smartApiLogin;
	
	@Autowired
	private IntraDayAlgoStagiesHelper intraDayAlgoStagiesHelper;
	
	@Autowired
	private FNOStockService fnoStockService;
	

	@Value("${prev.super.selection.from.time.hour}")
	private int prev_super_selection_from_time_hour;
	
	@Value("${prev.super.selection.from.time.minute}")
	private int prev_super_selection_from_time_minute;
	
	
	@Value("${prev.super.selection.to.time.hour}")
	private int prev_super_selection_to_time_hour;
	
	@Value("${prev.super.selection.to.time.minute}")
	private int prev_super_selection_to_time_minute;
	
	@Value("${atr.period}")
	private int atrPeriod;
	
	@Value("${multiplier}")
	private double multiplier;
	
	
	Map<String,Ta4jSuperTrendCalculator> supertrendmap=new HashMap<>();
	
	Map<String,CandleAnalyzer> candleAnalyzermap=new HashMap<>();
	
	
	
	Map<String,List<Candle>> candlesMap=new HashMap<>();
	Map<String,Ta4jSuperTrendCalculator> newsupertrendmap=new HashMap<>();
	
	
	public String loadTrade(LocalDateTime excutingTime, int counter){
		
		
		//LocalTime now = LocalTime.now(); 
        //LocalTime tenThirty = LocalTime.of(10, 30);
		
		//if (excutingTime.toLocalTime().isBefore(tenThirty)) {
			//For CE checking high point at 9:25 first down then break high again
			//For PE checking low point at 9:25 first up then break low again
        
        
        LocalTime nine_fifteen = LocalTime.of(9, 15); 
		LocalTime three_thrity = LocalTime.of(15, 30);  
		
		if (excutingTime.toLocalTime().isAfter(nine_fifteen) && excutingTime.toLocalTime().isBefore(three_thrity)) {
		}else {
			return "market closed";
		}
        
		//ObjectMapper obj = new ObjectMapper();
        
        List<TradeEntryStock> activeTradeList =tradeEntryStockService.fetchActiveTrade();
		this.getCandleListForMarketTrack(excutingTime, counter, activeTradeList); 
		//printing 
		
		 for(TradeEntryStock activeTrade:activeTradeList) {
			 
			 
				//intraDayAlgoStagiesHelper.initAngleOneWeb();
				//intraDayAlgoStagiesHelper.createWatchList(activeTrade);
			 
			// MySuperTrend mySuperTrend=new MySuperTrend();
			 
			 System.out.println("======activeTrade======"+activeTrade);
			 
			//// List<Candle> existingCandlesList = candlesMap.get(activeTrade.getStocktradingsymbol());
			// SuperTrendCalculator superTrend = new SuperTrendCalculator(atrPeriod, multiplier);
			 //for(Candle candle:existingCandlesList) {
				// superTrend.addCandle(candle);
				 
			 //}
			
			 //supertrendmap.put(activeTrade.getStocktradingsymbol(), superTrend);
			 
			 
			 /*
			 BarSeries series = new BaseBarSeries("my_series");
			 
			 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.ENGLISH);
			 for(Candle candle:existingCandlesList) {
				 OffsetDateTime offsetDateTime = OffsetDateTime.parse(candle.getDate());
			     String formattedDateTime = offsetDateTime.format(dtf);
				 series.addBar(createBar(formattedDateTime , candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVolume(), dtf));
			 }
			
			 
			 
			 SuperTrendWithTrendAndSignal superTrendWithTrendAndSignal=new SuperTrendWithTrendAndSignal(series, atrPeriod, 3);
			    
			
			// ATRIndicator atr = new ATRIndicator(series, atrPeriod);
			 
			 System.out.println("----===----superTrendWithTrendAndSignal==="+superTrendWithTrendAndSignal.getValue(13));
			 
			*/

			 
				
			 /*
			 try {
					System.out.println("existingCandlesList ===>> " + obj.writeValueAsString(existingCandlesList));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				*/
				//System.err.println("existingCandlesList size "+existingCandlesList.size());
				
			// SuperTrendDTO superTrend= mySuperTrend.calculateSuperTrend(existingCandlesList, atrPeriod, multiplier);
			 
			// System.err.println(activeTrade.getStocktradingsymbol()+"===>> "+superTrend);
				
		 }
		
		
        
        
       // Map<String,SuperTrendCalculator> supperTrendMap=this.calculateSupperTrendIndicator(activeTradeList, excutingTime,counter) ;
        /*
        for(TradeEntryStock activeTrade:activeTradeList) {
        	
        	 SuperTrendCalculator superTrend =supertrendmap.get(activeTrade.getStocktradingsymbol());
		        double superTrendValue = superTrend.getLatestSuperTrend();
		        boolean isUptrend = superTrend.isUptrend();
		        
		        System.err.println(
		        	"stock "+activeTrade.getStocktradingsymbol()
		        	+", type "+activeTrade.getGainerorlooser()
		        	+", superTrendValue: "+superTrendValue
		        	+", isUptrend: "+isUptrend
		        	);
		        
		        
		                	
        }
        
       */
        
        
        
       
      // List<TradeEntryStock> openTradeList = tradeEntryStockService.fetchTodayTrade(TradeStatus.OPEN.getValue());
      // this.stockExpiry2DayBeforeAnd2DayAfter(openTradeList);
        System.out.println("====================start  moveStockInprogress========================================");
		//this.moveStockInprogress(openTradeList,excutingTime,supperTrendMap);
			
			//placing order on  stock select
		//	int lot_quantity=1;
		//	this.placingBuyOrderForSeclectedStock(lot_quantity,excutingTime,10);
		//}
		
		
		//LocalTime ten = LocalTime.of(10, 00); 
		//LocalTime three_thrity = LocalTime.of(15, 30);  
		
		//if (excutingTime.toLocalTime().isAfter(ten) && excutingTime.toLocalTime().isBefore(three_thrity)) {
			
			//track exist logic for sell
			//this.existLogicForSellOption(excutingTime,10,supperTrendMap); 
			
			
		//}
		
		if(excutingTime.toLocalTime().isAfter(three_thrity)) {
			// move open status to disqualify
			//dailyCleanUp();
		}
		
		return "done";
	}

	public void getCandleListForMarketTrack(LocalDateTime excutingTime, int counter,
			List<TradeEntryStock> activeTradeList) {
		if (activeTradeList != null) {
			for (TradeEntryStock tradeEntryStock : activeTradeList) {
				
				if (counter == 0) {
					List<Candle> prevDayCandlesList = this.initCandlesList(excutingTime, tradeEntryStock);
					candlesMap.put(tradeEntryStock.getStocktradingsymbol(), prevDayCandlesList);

				} else {
					List<Candle> todayCandlesList = this.repeatedCandleList(excutingTime, tradeEntryStock);
					List<Candle> existingCandlesList = candlesMap.get(tradeEntryStock.getStocktradingsymbol());

					if (existingCandlesList != null && existingCandlesList.size() > 0) {
						for (Candle candle : todayCandlesList) {
							existingCandlesList.add(candle);
						}
						
						if (existingCandlesList.size() > atrPeriod) {
							existingCandlesList.subList(0, existingCandlesList.size() - atrPeriod).clear();
						}
						
					}
				}
				
			}

		} else {
			System.err.println("stock Section Move InProgress : No trade present in db");
		}
	}
	
	
	 private  Bar createBar(String dateTime, double open, double high, double low, double close, double volume, DateTimeFormatter dtf) {
	        
		 
		

	       
	        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dtf);

	     
	        ZoneId zoneId = ZoneId.of("Asia/Kolkata");

	      
	        ZonedDateTime zdt = localDateTime.atZone(zoneId);
		 
		 
		 
		 
		 
		// ZonedDateTime zdt = ZonedDateTime.parse(dateTime, dtf);
	        Duration timePeriod = Duration.ofMinutes(5);
	        return new BaseBar(timePeriod, zdt, DecimalNum.valueOf(open), DecimalNum.valueOf(high), DecimalNum.valueOf(low), DecimalNum.valueOf(close), DecimalNum.valueOf(volume), null);
	    }
	
	private void dailyCleanUp() {
		List<TradeEntryStock> openTradeList = tradeEntryStockService.fetchTodayTrade(TradeStatus.OPEN.getValue());
		if(openTradeList!=null && openTradeList.size()>0) {
			for(TradeEntryStock tradeEntryStock:openTradeList) {
				tradeEntryStock.setTradestatus(TradeStatus.DISQUALIFY.getValue());
				tradeEntryStockService.saveOrUpdate(tradeEntryStock);
			}
		}
		
	}
	
	
	/**
	 * 
	 */
	private void existLogicForSellOption(LocalDateTime excutingTime,int minusMinutes,Map<String,Ta4jSuperTrendCalculator> supperTrendMap) {
		
		List<TradeEntryStock> executedTradeList = tradeEntryStockService.fetchTodayTrade(TradeStatus.EXECUTED.getValue());
		
		if(executedTradeList!=null && executedTradeList.size()>0) {
			
			for(TradeEntryStock tradeEntryStock:executedTradeList) {
				
				
				  Ta4jSuperTrendCalculator superTrend =supperTrendMap.get(tradeEntryStock.getStocktradingsymbol());
			        //double superTrendValue = superTrend.getLatestSuperTrend();
			        boolean istrend = superTrend.isUptrend();
			        
			        String gainerorlooser=tradeEntryStock.getGainerorlooser();
				
				/*
				LocalDateTime from_date=excutingTime.minusMinutes(40);
				String todateString=DateSelection.formatedDateTime(excutingTime);
				String fromdateString=DateSelection.formatedDateTime(from_date);
				
				TRADEDateUtil.dealy(300);
				JSONArray candleData = smartApiLogin.getCandleData(tradeEntryStock.getStocksymboltoken(),INTERVAL.MINUTE_5.getValue(), fromdateString, todateString);
				SimpleMovingAverage simpleMovingAverage=new SimpleMovingAverage();
				 double sma=simpleMovingAverage.calculateSMA(candleData, 8);
				 boolean needExist=this.processCandle(candleData,sma,tradeEntryStock.getGainerorlooser());
				 System.err.println("needExist: "+needExist);
				 */
			        
			        
			        if ("CE".equalsIgnoreCase(gainerorlooser ) && !istrend) {
						
						
						String stockname=tradeEntryStock.getOrginaltradingsymbol();
						long lotsize = fetchLotSize(stockname);
						int lotquantity=tradeEntryStock.getBuylotquantity();
						saveSellOrderAndUpdateStatus(lotquantity, tradeEntryStock, stockname, lotsize, excutingTime,minusMinutes) ;
						
						tradeEntryStockService.saveOrUpdate(tradeEntryStock);
					} else if ("PE".equalsIgnoreCase(gainerorlooser) && istrend) {
						
						String stockname=tradeEntryStock.getOrginaltradingsymbol();
						long lotsize = fetchLotSize(stockname);
						int lotquantity=tradeEntryStock.getBuylotquantity();
						saveSellOrderAndUpdateStatus(lotquantity, tradeEntryStock, stockname, lotsize, excutingTime,minusMinutes) ;
						
						tradeEntryStockService.saveOrUpdate(tradeEntryStock);
					}
			        
				
				
			}
			
		}
		
	}
	
	/**
	 * 
	 * @param lotquantity
	 */
	private void placingBuyOrderForSeclectedStock(int lotquantity,LocalDateTime excutingTime,int minusMinutes) {
		List<TradeEntryStock> inProgressTradeList = tradeEntryStockService.fetchTodayTrade(TradeStatus.INPROGESS.getValue());
		
		if(inProgressTradeList!=null && inProgressTradeList.size()>0) {
			
			//processing for placing order
			
			for(TradeEntryStock tradeEntryStock:inProgressTradeList) {
				
				System.err.println("------inProgress TradeEntryStock inProgress Trade -------->> "+tradeEntryStock);
				//fetching stock for getting lot size
				String stockname=tradeEntryStock.getOrginaltradingsymbol();
				long lotsize = this.fetchLotSize(stockname);
				// placing oder 
				this.saveBuyOrderAndUpdateStatus(lotquantity, tradeEntryStock, stockname, lotsize,excutingTime,minusMinutes);
			}
			
			
			}
	}
	
	/**
	 * 
	 * @param lotquantity
	 * @param tradeEntryStock
	 * @param stockname
	 * @param lotsize
	 */
	private void saveSellOrderAndUpdateStatus(int lotquantity, TradeEntryStock tradeEntryStock, String stockname,long lotsize,LocalDateTime excutingTime,int minusMinutes) {
	
		Order order = null;
		try {
			order = intraDayAlgoStagiesHelper.placeSellOrder(tradeEntryStock, lotsize, lotquantity);
			System.out.println("orderId : " + order.orderId);

		} catch (IOException | SmartAPIException e) {
			System.err.println("issue while placing order for stock: " + stockname + " message :" + e.getMessage());
		}
		
		double ltp =this.getCandleDataBetweenInterval(excutingTime,tradeEntryStock,minusMinutes);

		// save detail for find out profit/loose
		if (order != null && order.orderId != null) {
			// update status
			tradeEntryStock.setTradestatus(TradeStatus.CLOSE.getValue());
			tradeEntryStock.setOderid(order.orderId);
			tradeEntryStock.setSellltp(ltp);
			tradeEntryStock.setSelllotquantity(lotquantity);
			if(TRADEConstants.TRADE_LIVE_OR_BACKTRAKING.equalsIgnoreCase("live"))
				tradeEntryStock.setSellTimestamp(DateSelection.convertLocalDateToTimestamp());
			else
				tradeEntryStock.setSellTimestamp(DateSelection.convertLocalDateToTimestamp(excutingTime));
				
			tradeEntryStockService.saveOrUpdate(tradeEntryStock);
		}
		
	}

/**
 * 
 * @param lotquantity
 * @param tradeEntryStock
 * @param stockname
 * @param lotsize
 */

private void saveBuyOrderAndUpdateStatus(int lotquantity, TradeEntryStock tradeEntryStock, String stockname,long lotsize,LocalDateTime excutingTime,int minusMinutes) {
	Order order = null;
	try {
		order = intraDayAlgoStagiesHelper.placeBuyOrder(tradeEntryStock, lotsize, lotquantity);
		System.out.println("orderId : " + order.orderId);

	} catch (IOException | SmartAPIException e) {
		System.err.println("issue while placing order for stock: " + stockname + " message :" + e.getMessage());
	}
	// getting ltp
	double ltp =this.getCandleDataBetweenInterval( excutingTime,tradeEntryStock,minusMinutes);
	
	// save detail for find out profit/loose
	if (order != null && order.orderId != null) {
		// update status
		tradeEntryStock.setTradestatus(TradeStatus.EXECUTED.getValue());
		tradeEntryStock.setOderid(order.orderId);
		tradeEntryStock.setBuyltp(ltp);
		tradeEntryStock.setBuylotquantity(lotquantity);
		if(TRADEConstants.TRADE_LIVE_OR_BACKTRAKING.equalsIgnoreCase("live"))
			tradeEntryStock.setBuyTimestamp(DateSelection.convertLocalDateToTimestamp());
		else
			tradeEntryStock.setBuyTimestamp(DateSelection.convertLocalDateToTimestamp(excutingTime));
			
		tradeEntryStockService.saveOrUpdate(tradeEntryStock);
	}
}
/**
 * 
 * @param stockname
 * @return
 */
	private long fetchLotSize(String stockname) {
		long lotsize=0;
		try {
			FNOStockDetail fnoStockDetail=fnoStockService.findByStockName(stockname);
			lotsize=fnoStockDetail.getLotsize();
		} catch (TradeScheduleBusinessException e) {
			System.err.println("lotsize is not present for stock: "+stockname+" message :"+e.getMessage());
		}
		return lotsize;
	}
/**
 * 
 */
	private void moveStockInprogress( List<TradeEntryStock> openTradeList,LocalDateTime excutingTime, Map<String,Ta4jSuperTrendCalculator> supperTrendMap) {
		
		for(TradeEntryStock tradeEntryStock:openTradeList) {
			
		    String gainerorlooser=tradeEntryStock.getGainerorlooser();
		    
			Ta4jSuperTrendCalculator superTrend =supperTrendMap.get(tradeEntryStock.getStocktradingsymbol());
		    boolean isUptrend = superTrend.isUptrend();
		    
		    List<Candle> candles= superTrend.getCandles();
		    
		    boolean ceEntry=false;
		    boolean peEntry=false;
		    
		    if(candles.size()>=2) {
		    	Candle secondlastCandle = candles.get(candles.size() - 2);
		    	Candle lastCandle = candles.get(candles.size() - 1);
		    	
		    	if(lastCandle.getHigh()>secondlastCandle.getHigh()) {
		    		ceEntry=true;
		    	}
		    	
		    	
		    	if(lastCandle.getLow()<secondlastCandle.getLow()) {
		    		peEntry=true;
		    	}
		    	
		    	if("CE".equalsIgnoreCase(gainerorlooser )) {
		    		System.out.println("stock "+tradeEntryStock.getStocktradingsymbol()+", gainerorlooser "+gainerorlooser
		    				+", lastCandle high "+lastCandle.getHigh()+", secondlastCandle high"
		    				+secondlastCandle.getHigh()+" isUptrend "+isUptrend);			    	
		    	}else {
		    		System.out.println("stock "+tradeEntryStock.getStocktradingsymbol()+",gainerorlooser "
		    	+gainerorlooser+", lastCandle low "+lastCandle.getLow()+", secondlastCandle low "
		    				+secondlastCandle.getLow()+" isUptrend "+isUptrend);
			    	
		    	}
		    	
		    	
		    	
		    
		    }
		    
		    
		    
	       
			if ("CE".equalsIgnoreCase(gainerorlooser ) && isUptrend && ceEntry) {
				tradeEntryStock.setTradestatus(TradeStatus.INPROGESS.getValue());
				tradeEntryStockService.saveOrUpdate(tradeEntryStock);
			} else if ("PE".equalsIgnoreCase(gainerorlooser) && !isUptrend && peEntry) {
				
				tradeEntryStock.setTradestatus(TradeStatus.INPROGESS.getValue());
				tradeEntryStockService.saveOrUpdate(tradeEntryStock);
			}
		}
		
	}
	
	/**
	 * 
	 * @param tradeList
	 */
	private void stockExpiry2DayBeforeAnd2DayAfter(List<TradeEntryStock> tradeList) {
		if (tradeList != null) {
			for (TradeEntryStock tradeEntryStock : tradeList) {
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
						.appendPattern("ddMMMyyyy").toFormatter(Locale.ENGLISH);

				LocalDate exipryDate = LocalDate.parse(tradeEntryStock.getOptionexpiry(), formatter);
				LocalDate twoDaysBefore = exipryDate.minusDays(2);
				LocalDate twoDaysAfter = exipryDate.plusDays(2);
				LocalDate currentDate=DateSelection.getLocalDate();
				if (currentDate.isEqual(twoDaysBefore) || currentDate.isEqual(twoDaysAfter)) {
					System.err.println("---Disqualify due to expiry----");
					tradeEntryStock.setTradestatus(TradeStatus.DISQUALIFY.getValue());
					tradeEntryStockService.saveOrUpdate(tradeEntryStock);
				}
			}
		} else {
			System.err.println("stockExpiry2DayBeforeAnd2DayAfter : No trade present in db");
		}
	}
	/**
	 * 
	 * @param tradeList
	 * @param excutingTime
	 */
	private Map<String,Ta4jSuperTrendCalculator> calculateSupperTrendIndicator(List<TradeEntryStock> tradeList,LocalDateTime excutingTime,int counter) {
		
		if (tradeList != null) {

			for (TradeEntryStock tradeEntryStock : tradeList) {
					
					if(counter==0) {
						this.initSupperTrend(excutingTime, tradeEntryStock);
					}else {
						this.repeatedSupperTrend(excutingTime, tradeEntryStock);
					}
					
					
				}

		} else {
			System.err.println("stock Section Move InProgress : No trade present in db");
		}
		
		
		return supertrendmap;
	}

	public void repeatedSupperTrend(LocalDateTime excutingTime, TradeEntryStock tradeEntryStock) {
		Ta4jSuperTrendCalculator superTrend =supertrendmap.get(tradeEntryStock.getStocktradingsymbol());
		
		JSONArray today_CandleData = currentTimeCandles(excutingTime, tradeEntryStock);
      
		if(today_CandleData!=null && today_CandleData.length() >0) {
			JSONArray lastCandle = today_CandleData.getJSONArray(today_CandleData.length() - 1);
			String date = lastCandle.getString(0);
			double open = lastCandle.getDouble(1);
	        double high = lastCandle.getDouble(2);
	        double low = lastCandle.getDouble(3);
	        double close = lastCandle.getDouble(4);
	        int volume = lastCandle.getInt(5);
	        Candle candle=new Candle(date,open,high,low,close,volume);
	        superTrend.addCandle(candle);
		}
		
	}
	
	
	public List<Candle> repeatedCandleList(LocalDateTime excutingTime, TradeEntryStock tradeEntryStock) {
		List<Candle>  repeatedCandles=new ArrayList<>();
		
		JSONArray today_CandleData = currentTimeCandles(excutingTime, tradeEntryStock);
      
		if(today_CandleData!=null && today_CandleData.length() >0) {
			JSONArray lastCandle = today_CandleData.getJSONArray(today_CandleData.length() - 1);
			String date = lastCandle.getString(0);
			double open = lastCandle.getDouble(1);
	        double high = lastCandle.getDouble(2);
	        double low = lastCandle.getDouble(3);
	        double close = lastCandle.getDouble(4);
	        int volume = lastCandle.getInt(5);
	        Candle candle=new Candle(date,open,high,low,close,volume);
	        repeatedCandles.add(candle);
		}
		
		return repeatedCandles;
		
	}

	public JSONArray currentTimeCandles(LocalDateTime excutingTime, TradeEntryStock tradeEntryStock) {
		LocalDateTime from_date=excutingTime.minusMinutes(5);
		String todateString=DateSelection.formatedDateTime(excutingTime);
		String fromdateString=DateSelection.formatedDateTime(from_date);
		TRADEDateUtil.dealy(300);
		JSONArray today_CandleData = smartApiLogin.getCandleData(tradeEntryStock.getStocksymboltoken(), INTERVAL.MINUTE_5.getValue(),fromdateString, todateString);
		return today_CandleData;
	}

	public void initSupperTrend(LocalDateTime excutingTime, TradeEntryStock tradeEntryStock) {
		
		Ta4jSuperTrendCalculator superTrend = new Ta4jSuperTrendCalculator(atrPeriod, multiplier);
		
		JSONArray prevdayCandleData = getPreviousDayCandles(tradeEntryStock);
		JSONArray today_CandleData = getTodayCandle(excutingTime, tradeEntryStock);
		JSONArray mergedArray = new JSONArray();
   
		for (int i = 0; i < prevdayCandleData.length(); i++) {
		    mergedArray.put(prevdayCandleData.get(i));
		}
		
		for (int i = 0; i < today_CandleData.length(); i++) {
		    mergedArray.put(today_CandleData.get(i));
		}
		
		 for (int i = 0; i < mergedArray.length(); i++) {
		        JSONArray jsonArray = mergedArray.getJSONArray(i);
		        String date = jsonArray.getString(0);
		        double open = jsonArray.getDouble(1);
		        double high = jsonArray.getDouble(2);
		        double low = jsonArray.getDouble(3);
		        double close = jsonArray.getDouble(4);
		        int volume = jsonArray.getInt(5);
		        
		        Candle candle=new Candle(date,open,high,low,close,volume);
		        superTrend.addCandle(candle);
		        
		    }
		
		 supertrendmap.put(tradeEntryStock.getStocktradingsymbol(), superTrend);
		 
		 
	}
	
	
	
public List<Candle>  initCandlesList(LocalDateTime excutingTime, TradeEntryStock tradeEntryStock) {
		
		List<Candle>  initCandles=new ArrayList<>();
		
		JSONArray prevdayCandleData = getPreviousDayCandles(tradeEntryStock);
		JSONArray today_CandleData = getTodayCandle(excutingTime, tradeEntryStock);
		JSONArray mergedArray = new JSONArray();
   
		for (int i = 0; i < prevdayCandleData.length(); i++) {
		    mergedArray.put(prevdayCandleData.get(i));
		}
		
		for (int i = 0; i < today_CandleData.length(); i++) {
		    mergedArray.put(today_CandleData.get(i));
		}
		
		 for (int i = 0; i < mergedArray.length(); i++) {
		        JSONArray jsonArray = mergedArray.getJSONArray(i);
		        String date = jsonArray.getString(0);
		        double open = jsonArray.getDouble(1);
		        double high = jsonArray.getDouble(2);
		        double low = jsonArray.getDouble(3);
		        double close = jsonArray.getDouble(4);
		        int volume = jsonArray.getInt(5);
		        
		        Candle candle=new Candle(date,open,high,low,close,volume);
		        initCandles.add(candle);
		        
		    }
		 
		 
		 return initCandles;
		
		
		 
		 
	}


	public JSONArray getTodayCandle(LocalDateTime excutingTime, TradeEntryStock tradeEntryStock) {
		TRADEDateUtil.dealy(300);
		LocalDateTime from_date=excutingTime.minusMinutes(11);
		String todateString=DateSelection.formatedDateTime(excutingTime);
		String fromdateString=DateSelection.formatedDateTime(from_date);
		JSONArray today_CandleData = smartApiLogin.getCandleData(tradeEntryStock.getStocksymboltoken(), INTERVAL.MINUTE_5.getValue(),fromdateString, todateString);
		return today_CandleData;
	}

	public JSONArray getPreviousDayCandles(TradeEntryStock tradeEntryStock) {
		TRADEDateUtil.dealy(300);
		String prevday_from = TRADEDateUtil.dynamicPrevDate(prev_super_selection_from_time_hour,prev_super_selection_from_time_minute);
		String prevday_to = TRADEDateUtil.dynamicPrevDate(prev_super_selection_to_time_hour,prev_super_selection_to_time_minute);
		JSONArray prevdayCandleData = smartApiLogin.getCandleData(tradeEntryStock.getStocksymboltoken(),INTERVAL.MINUTE_5.getValue(), prevday_from, prevday_to);
		return prevdayCandleData;
	}

	
	

/**
 * 
 * @param tradeList
 */
	private void stockSectionMoveInProgress(List<TradeEntryStock> tradeList,LocalDateTime excutingTime) {
		if (tradeList != null) {

			for (TradeEntryStock tradeEntryStock : tradeList) {
				
				String tradestatus = tradeEntryStock.getTradestatus();
				if (TradeStatus.OPEN.getValue().equalsIgnoreCase(tradestatus)) {

					String gainerorlooser = tradeEntryStock.getGainerorlooser();

					if ("CE".equalsIgnoreCase(gainerorlooser)) {
						double highat925 = tradeEntryStock.getHighpoint();
						
					
						double lastedClosePrice=this.getCandleDataBetweenInterval(excutingTime, tradeEntryStock,10);

						System.err.println("Stock Name :" + tradeEntryStock.getStocktradingsymbol() + ", highat925 : "+ highat925 + ", lastedClosePrice " + lastedClosePrice);

						if ("No".equalsIgnoreCase(tradeEntryStock.getHighbreakdown())
								&& "No".equalsIgnoreCase(tradeEntryStock.getHighbreakup()) && highat925 > lastedClosePrice) {
							tradeEntryStock.setHighbreakdown("Yes");
							tradeEntryStockService.saveOrUpdate(tradeEntryStock);
						} else if ("Yes".equalsIgnoreCase(tradeEntryStock.getHighbreakdown())
								&& "No".equalsIgnoreCase(tradeEntryStock.getHighbreakup()) && highat925 < lastedClosePrice) {
							tradeEntryStock.setHighbreakup("Yes");
							tradeEntryStockService.saveOrUpdate(tradeEntryStock);
						} else if ("Yes".equalsIgnoreCase(tradeEntryStock.getHighbreakdown())
								&& "Yes".equalsIgnoreCase(tradeEntryStock.getHighbreakup())) {
							tradeEntryStock.setTradestatus(TradeStatus.INPROGESS.getValue());
							tradeEntryStockService.saveOrUpdate(tradeEntryStock);
						}
					} else if ("PE".equalsIgnoreCase(gainerorlooser)) {
						double lowat925 = tradeEntryStock.getLowpoint();
						
						double lastedClosePrice=this.getCandleDataBetweenInterval(excutingTime, tradeEntryStock,10);

						System.err.println("Stock Name :" + tradeEntryStock.getStocktradingsymbol() + ", lowat925 : "+lowat925 + ", lastedClosePrice " + lastedClosePrice);

						if ("No".equalsIgnoreCase(tradeEntryStock.getLowbreakup())
								&& "No".equalsIgnoreCase(tradeEntryStock.getLowbreakup()) && lowat925 < lastedClosePrice) {
							tradeEntryStock.setLowbreakup("Yes");
							tradeEntryStockService.saveOrUpdate(tradeEntryStock);

						} else if ("Yes".equalsIgnoreCase(tradeEntryStock.getLowbreakup())
								&& "No".equalsIgnoreCase(tradeEntryStock.getLowbreakdown()) && lowat925 > lastedClosePrice) {
							tradeEntryStock.setLowbreakdown("Yes");
							tradeEntryStockService.saveOrUpdate(tradeEntryStock);
						} else if ("Yes".equalsIgnoreCase(tradeEntryStock.getLowbreakup())
								&& "Yes".equalsIgnoreCase(tradeEntryStock.getLowbreakdown())) {
							tradeEntryStock.setTradestatus(TradeStatus.INPROGESS.getValue());
							tradeEntryStockService.saveOrUpdate(tradeEntryStock);
						}

					}

				}


				
				
				
			}

		} else {
			System.err.println("stock Section Move InProgress : No trade present in db");
		}
	}


public double getCandleDataBetweenInterval(LocalDateTime excutingTime, TradeEntryStock tradeEntryStock, int minusMinutes) {
	TRADEDateUtil.dealy(300);
	LocalDateTime from_date=excutingTime.minusMinutes(minusMinutes);
	String todateString=DateSelection.formatedDateTime(excutingTime);
	String fromdateString=DateSelection.formatedDateTime(from_date);
	JSONArray candleData = smartApiLogin.getCandleData(tradeEntryStock.getStocksymboltoken(),INTERVAL.MINUTE_5.getValue(), fromdateString, todateString);
	double close=0;
	if (candleData != null) {
		JSONArray lastCandle = candleData.getJSONArray(candleData.length() - 1);
		if (lastCandle != null) {
			// colse is equal to ltp
			 close = lastCandle.getDouble(4);
		} else {
			System.out.println("No last candle Data present");
		}

	} else {
		System.out.println("candle Data is empty for stock: "
				+ tradeEntryStock.getStocksymboltoken() + "between interval fromdate: "
				+ fromdateString + " and todateString: " + todateString);
	}
	return close;
}

	
	

}
