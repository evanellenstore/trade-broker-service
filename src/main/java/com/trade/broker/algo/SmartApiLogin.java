package com.trade.broker.algo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.Order;
import com.angelbroking.smartapi.models.OrderParams;
import com.angelbroking.smartapi.models.TokenSet;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.smartstream.models.Depth;
import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.LTP;
import com.angelbroking.smartapi.smartstream.models.Quote;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.angelbroking.smartapi.smartstream.models.SmartStreamSubsMode;
import com.angelbroking.smartapi.smartstream.models.SnapQuote;
import com.angelbroking.smartapi.smartstream.models.TokenID;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamListener;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamTicker;
import com.angelbroking.smartapi.utils.Constants;
import com.neovisionaries.ws.client.WebSocketException;
import com.trade.broker.constant.TRADEConstants;
import com.trade.broker.entity.DBTokenDetail;
import com.trade.broker.entity.TradeEntryStock;
import com.trade.broker.exception.TradeScheduleBusinessException;
import com.trade.broker.kafka.TickPublisher;
import com.trade.broker.service.TokenService;
import com.trade.broker.util.TRADEDateUtil;

@Component
public class SmartApiLogin {

	private static final Logger log = LoggerFactory.getLogger(SmartApiLogin.class);
	public static final String HISTORICAL = "Historical";
	public static final String MARKET = "Market";
	public static final String TRADING = "Trading";
	public static final String PUBLISHER = "Publisher";
	public static final String CLIENTID = "ClientId";
	public static final String MPIN = "mpin";
	public static final String APIKEY = "APIKEY";

	SmartConnect smartConnect = new SmartConnect();
	User user=new User();
	SmartStreamListener listener=null;
	SmartStreamTicker ticker=null;


	@Autowired
	private TokenService tokenService;

	@Autowired
	private TickPublisher tickPublisher;

	private final ConcurrentHashMap<String, Object> latestQuotes = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> activeSymbols = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> activeSubscriptionIds = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> activeSubscriptionNames = new ConcurrentHashMap<>();
/**
 * 
 * @param listOfTokens
 * @throws WebSocketException 
 */
	

	/**
	 * 
	 * @param listOfTokens
	 * @param exchange
	 * @throws WebSocketException
	 */
public void subcribeToSmartStreamConnect(List<String> listOfTokens, String exchange, Map<String, String> symbols,
		Map<String, String> subscriptionIds, Map<String, String> subscriptionNames) throws WebSocketException {
	activeSymbols.clear();
	activeSymbols.putAll(symbols);
	activeSubscriptionIds.clear();
	activeSubscriptionIds.putAll(subscriptionIds);
	activeSubscriptionNames.clear();
	activeSubscriptionNames.putAll(subscriptionNames);
	latestQuotes.clear();
        String feedToken = user.getFeedToken();

        // --- 2. Define the listener ---
        listener = new SmartStreamListener() {
            @Override
            public void onLTPArrival(LTP ltp) {
                if (ltp != null && ltp.getToken() != null) {
                    String token = ltp.getToken().getToken();
					if (!activeSymbols.containsKey(token)) return;
                    tickPublisher.publish(new JSONObject()
                            .put("event", "LTP")
                            .put("token", token)
							.put("symbol", activeSymbols.get(token))
							.put("subscriptionId", activeSubscriptionIds.get(token))
							.put("subscriptionName", activeSubscriptionNames.get(token))
                            .put("ltp", ltp.getLastTradedPrice())
                            .toString());
                }
                log.info("Received LTP update for token {}", ltp != null ? ltp.getToken() : null);
                System.out.println("Received LTP update for token " + (ltp != null ? ltp.getToken() : null));
            }

            @Override
            public void onQuoteArrival(Quote quote) {
                if (quote != null && quote.getToken() != null) {
                    String token = quote.getToken().getToken();
					if (!activeSymbols.containsKey(token)) return;
                    tickPublisher.publish(new JSONObject()
                            .put("event", "QUOTE")
                            .put("token", token)
							.put("symbol", activeSymbols.get(token))
							.put("subscriptionId", activeSubscriptionIds.get(token))
							.put("subscriptionName", activeSubscriptionNames.get(token))
                            .put("ltp", quote.getLastTradedPrice())
                            .put("volume", quote.getVolumeTradedToday())
                            .toString());
                }
                log.info("Received quote update for token {}", quote != null ? quote.getToken() : null);
                System.out.println("Received quote update for token " + (quote != null ? quote.getToken() : null));
            }

            @Override
            public void onSnapQuoteArrival(SnapQuote snapQuote) {
                if (snapQuote != null && snapQuote.getToken() != null) {
                    String token = snapQuote.getToken().getToken()
                            .replace("\u0000", "")
                            .trim();
								if (!activeSymbols.containsKey(token)) return;

                    latestQuotes.put(token, snapQuote);
                }
            }

            @Override
            public void onDepthArrival(Depth depth) {
				if (depth != null && depth.getToken() != null
						&& activeSymbols.containsKey(depth.getToken().getToken())) {
                    tickPublisher.publish( new JSONObject()
                            .put("event", "DEPTH")
                            .put("token", depth.getToken())
							.put("subscriptionId", activeSubscriptionIds.get(depth.getToken().getToken()))
							.put("subscriptionName", activeSubscriptionNames.get(depth.getToken().getToken()))
                            .toString());
                }
                log.info("Received depth update for token {}", depth != null ? depth.getToken() : null);
                System.out.println("Received depth update for token " + (depth != null ? depth.getToken() : null));
            }

            @Override
            public void onConnected() {
                log.info("SmartAPI WebSocket connected.");
                System.out.println("SmartAPI WebSocket connected.");
            }

            @Override
            public void onDisconnected() {
                log.info("SmartAPI WebSocket disconnected");
                System.out.println("SmartAPI WebSocket disconnected");
            }

            @Override
            public void onError(SmartStreamError error) {
                log.error("Smart stream error received: {}", error);
                System.err.println("Smart stream error received: " + error);
            }

            @Override
            public void onPong() {
                log.info("Heartbeat received.");
                System.out.println("Heartbeat received.");
            }

            @Override
            public SmartStreamError onErrorCustom() {
                log.warn("Custom smart stream error handling invoked");
                System.err.println("Custom smart stream error handling invoked");
                return null;
            }
        };

        // --- 3. Create ticker and connect ---
        ticker = new SmartStreamTicker(getKey().get(SmartApiLogin.CLIENTID), feedToken, listener);
        ticker.connect();

        // --- 4. Subscribe to tokens ---
        Set<TokenID> tokens = new HashSet<>();
        ExchangeType exchangeType = resolveExchangeType(exchange);
        for (String token : listOfTokens) {
            if (token != null && !token.trim().isEmpty()) {
                tokens.add(new TokenID(exchangeType, token.trim()));
            }
        }

        ticker.subscribe(SmartStreamSubsMode.SNAP_QUOTE, tokens);
    }

    private ExchangeType resolveExchangeType(String exchange) {
        if (exchange == null) {
            return ExchangeType.NSE_CM;
        }
        switch (exchange.trim().toUpperCase()) {
            case "BSE":
                return ExchangeType.BSE_CM;
            case "NSE_FO":
                return ExchangeType.NSE_FO;
            case "BSE_FO":
                return ExchangeType.BSE_FO;
            case "NSE":
            default:
                return ExchangeType.NSE_CM;
        }
    }




	/**
	 * 
	 * @param totp
	 * @return
	 */
	public DBTokenDetail proccessMarketLogin(String totp) {
		DBTokenDetail dbTokenDetail = new DBTokenDetail();

		try {

			smartConnect = new SmartConnect(getKey().get(SmartApiLogin.APIKEY));

			user = smartConnect.generateSession(getKey().get(SmartApiLogin.CLIENTID),
					getKey().get(SmartApiLogin.MPIN), totp);

			smartConnect.setAccessToken(user.getAccessToken());
			smartConnect.setRefreshToken(user.getRefreshToken());

			log.debug("Market login completed for client {}", user.getUserId());

			dbTokenDetail.setAccesstoken(user.getAccessToken());
			dbTokenDetail.setRefreshtoken(user.getRefreshToken());
			 dbTokenDetail.setFeedtoken(user.getFeedToken());
			dbTokenDetail.setTokenexpried("N");
			dbTokenDetail.setClientId(user.getUserId());
			dbTokenDetail.setAppName("smartapi");

			log.info("Logged in successfully to Smart API");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dbTokenDetail;
	}

	/**
	 * 
	 * @param tradeEntryStock
	 * @param lotsize
	 * @param lotQunatity
	 * @return
	 */
	public Order placeSellOrder(TradeEntryStock tradeEntryStock, long lotsize, int lotQunatity) {

		// testing purpose manipulating data
		/*
		 * tradeEntryStock.setOptionstrikeprice(1);
		 * tradeEntryStock.setOptionsymboltoken("48576");
		 * tradeEntryStock.setOptionorginaltradingsymbol("171NSETEST27NOV36FUT");
		 * lotsize=50;
		 * lotQunatity=2;
		 */

		Order order = null;
		// int oStrikeprice=tradeEntryStock.getOptionstrikeprice();
		String oSymboltoken = tradeEntryStock.getOptionsymboltoken();
		// String stockname=tradeEntryStock.getOrginaltradingsymbol();
		String optionorginaltradingsymbol = tradeEntryStock.getOptionorginaltradingsymbol();
		// int optionPrice=oStrikeprice;
		// double doubleoOtionPrice = (double) optionPrice;

		OrderParams orderParams = new OrderParams();
		orderParams.variety = Constants.VARIETY_NORMAL;
		orderParams.exchange = "NFO";
		orderParams.tradingsymbol = optionorginaltradingsymbol;
		orderParams.symboltoken = oSymboltoken;
		orderParams.transactiontype = Constants.TRANSACTION_TYPE_SELL;
		orderParams.ordertype = Constants.ORDER_TYPE_MARKET;
		// orderParams.producttype = Constants.PRODUCT_CARRYFORWARD;
		orderParams.duration = Constants.DURATION_DAY;
		int intlotsize = (int) lotsize;
		orderParams.quantity = lotQunatity * intlotsize;

		if (TRADEConstants.TRADE_PLACE_ORDER_LIVE) {
			order = smartConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
		} else {
			order = new Order();
			order.orderId = UUID.randomUUID().toString();
		}

		return order;

	}

	/**
	 * 
	 * @param tradeEntryStock
	 * @param lotsize
	 * @param lotQunatity
	 * @return
	 */
	public Order placeBuyOrder(TradeEntryStock tradeEntryStock, long lotsize, int lotQunatity) {

		// testing purpose manipulating data
		/*
		 * tradeEntryStock.setOptionstrikeprice(1);
		 * tradeEntryStock.setOptionsymboltoken("48576");
		 * tradeEntryStock.setOptionorginaltradingsymbol("171NSETEST27NOV36FUT");
		 * lotsize=50;
		 * lotQunatity=2;
		 */

		Order order = null;
		// int oStrikeprice=tradeEntryStock.getOptionstrikeprice();
		String oSymboltoken = tradeEntryStock.getOptionsymboltoken();
		// String stockname=tradeEntryStock.getOrginaltradingsymbol();
		String optionorginaltradingsymbol = tradeEntryStock.getOptionorginaltradingsymbol();
		// int optionPrice=oStrikeprice;
		// double doubleoOtionPrice = (double) optionPrice;

		OrderParams orderParams = new OrderParams();
		orderParams.variety = Constants.VARIETY_NORMAL;
		orderParams.exchange = "NFO";
		orderParams.tradingsymbol = optionorginaltradingsymbol;
		orderParams.symboltoken = oSymboltoken;
		orderParams.transactiontype = Constants.TRANSACTION_TYPE_BUY;
		// orderParams.ordertype = Constants.ORDER_TYPE_LIMIT;
		orderParams.ordertype = Constants.ORDER_TYPE_MARKET;
		orderParams.producttype = Constants.PRODUCT_CARRYFORWARD;
		orderParams.duration = Constants.DURATION_DAY;
		int intlotsize = (int) lotsize;
		orderParams.quantity = lotQunatity * intlotsize;

		// orderParams.price = doubleoOtionPrice;
		orderParams.squareoff = "0";
		orderParams.stoploss = "0";

		if (TRADEConstants.TRADE_PLACE_ORDER_LIVE) {
			order = smartConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
		} else {
			order = new Order();
			order.orderId = UUID.randomUUID().toString();
		}

		return order;

	}

	/**
	 * 
	 * @param exchange
	 * @param searchscrip
	 * @return
	 * @throws IOException
	 * @throws SmartAPIException
	 */

	public String getSearchScrip(String exchange, String searchscrip) throws IOException, SmartAPIException {
		JSONObject payload = new JSONObject();
		payload.put("exchange", exchange);
		payload.put("searchscrip", searchscrip);

		return smartConnect.getSearchScrip(payload);

		// System.err.println("=======getSearchScrip====="+response);
	}

	/**
	 * 
	 * @param exchange
	 * @param type
	 * @return
	 * @throws IOException
	 * @throws SmartAPIException
	 */
	public JSONObject getGainersLosers(String exchange, String type) throws IOException, SmartAPIException {

		JSONObject params = new JSONObject();
		// params.put("exchange", exchange);
		params.put("datatype", "PercOIGainers");
		params.put("expirytype", "NEAR");
		JSONObject response = smartConnect.gainersLosers(params);
		return response;

	}

	/**
	 * 
	 * @param topGainerTradeEntryStock
	 * @param topLooserTradeEntryStock
	 * @param candleType
	 */
	public void getSocketConnection(List<TradeEntryStock> topGainerTradeEntryStock,
			List<TradeEntryStock> topLooserTradeEntryStock, String candleType) {

		SmartStreamListener smartStreamListener = new SmartStreamListener() {
			@Override
			public void onLTPArrival(LTP ltp) {
				long lastTradedPrice = ltp.getLastTradedPrice();
				double ltpvalue = (double) lastTradedPrice;
				double finalltpvalue = ltpvalue / 100;
				System.out.println("ltp value==========>" + ltp.getExchangeType() + ", LastTradedPrice: "
						+ finalltpvalue + ", " + ", getToken: " + ltp.getToken());
			}

			@Override
			public void onQuoteArrival(Quote quote) {
				// Handle quote arrival if needed
				System.err.println("on Quote Arrival....");
			}

			@Override
			public void onSnapQuoteArrival(SnapQuote snapQuote) {
				// Handle snap quote arrival if needed
				System.err.println("on Snap Quote Arrival....");
			}

			@Override
			public void onDepthArrival(Depth depth) {
				// Handle depth arrival if needed
				System.err.println("on Depth Arrival....");
			}

			@Override
			public void onConnected() {
				System.out.println("Connected successfully");
			}

			@Override
			public void onDisconnected() {
				// Handle disconnection if needed
				System.err.println("on Disconnected....");
			}

			@Override
			public void onError(SmartStreamError smartStreamError) {
				System.err.println("Stream error: " + smartStreamError.getException().getMessage());
			}

			@Override
			public void onPong() {
				// Handle pong response if needed
				System.err.println("on Pong....");
			}

			@Override
			public SmartStreamError onErrorCustom() {
				return null;
			}
		};

		DBTokenDetail dbtoken = tokenService.getTokenAppName(TRADEConstants.SMART_API,
				TRADEConstants.M_TOKEN_EXPREIED_NO);

		TokenSet tokenSet = smartConnect.renewAccessToken(dbtoken.getAccesstoken(), dbtoken.getRefreshtoken());

		SmartStreamTicker smartStreamTicker = new SmartStreamTicker(getKey().get(SmartApiLogin.CLIENTID),
				tokenSet.getFeedToken(), smartStreamListener);
		try {
			smartStreamTicker.connect();
		} catch (WebSocketException e) {

			e.printStackTrace();
		}
		Boolean connection = smartStreamTicker.isConnectionOpen();
		System.out.println("Connection open: " + connection);

		Set<TokenID> tokenIdSet = new HashSet<>();

		if (TRADEConstants.NIFITY50_CANDLE_GREEN_CALCULATED.equalsIgnoreCase(candleType)) {
			if (topGainerTradeEntryStock != null) {
				for (TradeEntryStock tradeEntryStock : topGainerTradeEntryStock) {
					String token = tradeEntryStock.getStocksymboltoken();
					// String exchange=tradeEntryStock.getExchange();
					tokenIdSet.add(new TokenID(ExchangeType.NSE_CM, token));
				}
			}

			if (topLooserTradeEntryStock != null) {
				for (TradeEntryStock tradeEntryStock : topLooserTradeEntryStock) {
					String token = tradeEntryStock.getStocksymboltoken();
					// String exchange=tradeEntryStock.getExchange();
					tokenIdSet.add(new TokenID(ExchangeType.NSE_CM, token));
				}
			}

		} else if (TRADEConstants.NIFITY50_CANDLE_RED_CALCULATED.equalsIgnoreCase(candleType)) {
			if (topLooserTradeEntryStock != null) {
				for (TradeEntryStock tradeEntryStock : topLooserTradeEntryStock) {
					String token = tradeEntryStock.getStocksymboltoken();
					// String exchange=tradeEntryStock.getExchange();
					tokenIdSet.add(new TokenID(ExchangeType.NSE_CM, token));
				}
			}

		}

		for (TokenID tokenID : tokenIdSet) {

			System.err.println(tokenID.getToken());

		}

		smartStreamTicker.subscribe(SmartStreamSubsMode.LTP, tokenIdSet);

	}

	/**
	 * 
	 * @param totp
	 * @return
	 */
	public User proccessHistoryLogin(String totp) {

		User huser = this.getHistorySmartConnect(getKey().get(SmartApiLogin.CLIENTID), getKey().get(SmartApiLogin.MPIN),
				totp);

		System.out.println("Logged in successfully!");
		System.out.println("History Access Token: " + huser.getAccessToken());
		System.out.println("History Refresh Token: " + huser.getRefreshToken());

		return huser;
	}

	/**
	 * 
	 */
	public String proccessReLogin() {
		return proccessReLogin(null, null);
	}

	public String proccessReLogin(String ttop, String mode) {
		String result = "";
		DBTokenDetail dbtoken = tokenService.getTokenAppName(TRADEConstants.SMART_API, TRADEConstants.M_TOKEN_EXPREIED_NO);

		if (dbtoken != null) {
			smartConnect = new SmartConnect(getKey().get(SmartApiLogin.APIKEY));
			TokenSet mTokenSet = smartConnect.renewAccessToken(dbtoken.getAccesstoken(), dbtoken.getRefreshtoken());
			smartConnect.setAccessToken(mTokenSet.getAccessToken());
			smartConnect.setRefreshToken(mTokenSet.getRefreshToken());
			smartConnect.setUserId(mTokenSet.getUserId());

			if (user != null) {
				user.setAccessToken(mTokenSet.getAccessToken());
				user.setRefreshToken(mTokenSet.getRefreshToken());
				user.setFeedToken(mTokenSet.getFeedToken());
			}

			System.err.println("Market Re-Logged in successfully!");

			dbtoken.setAccesstoken(mTokenSet.getAccessToken());
			dbtoken.setRefreshtoken(mTokenSet.getRefreshToken());
			if (mTokenSet.getFeedToken() != null) {
				dbtoken.setFeedtoken(mTokenSet.getFeedToken());
			}
			dbtoken.setTokenexpried("N");
			if (mode != null && !mode.isBlank()) {
				dbtoken.setLiveOrBacktest(mode);
			}
			dbtoken.setUpdTimestamp(TRADEDateUtil.getCurrentJavaSqlTimestamp());
			tokenService.saveToken(dbtoken);
			result = "re-login successfully";
		} else if (ttop != null && !ttop.isBlank()) {
			DBTokenDetail freshToken = proccessMarketLogin(ttop);
			if (freshToken != null && freshToken.getAccesstoken() != null) {
				freshToken.setLiveOrBacktest(mode != null && !mode.isBlank() ? mode : "live");
				tokenService.saveToken(freshToken);
				result = "login successfully";
			} else {
				result = "not re-login! Please login with fresh!";
			}
		} else {
			result = "not re-login! Please login with fresh!";
		}
		return result;
	}

	/**
	 * 
	 * @param token
	 * @param apiKey
	 */
	public void getRestData(String token, String apiKey) {
		try {
			String accessToken = token;
			// Example endpoint (if exposed by AngleOne)
			String endpoint = "https://apiconnect.angelbroking.com/rest/secure/angelbroking/market/v1/topGainersLosers";

			URL url = new URL(endpoint);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-API-KEY", apiKey);
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
			conn.setRequestProperty("Accept", "application/json");

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			System.out.println("Top Gainers/Losers Response: " + response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	public void logout() {
		DBTokenDetail dbtoken = tokenService.getTokenAppName(TRADEConstants.SMART_API, "N");
		if (dbtoken != null) {
			dbtoken.setTokenexpried("Y");
			tokenService.saveToken(dbtoken);
		}
		smartConnect.logout();
	}

	/**
	 * 
	 * @return
	 * @throws SmartAPIException
	 * @throws IOException
	 */
	public JSONArray getCandleData(String symboltoken, String interval, String fromdate, String todate) {
		JSONObject requestObejct = new JSONObject();
		requestObejct.put("exchange", "NSE");
		requestObejct.put("symboltoken", symboltoken);
		requestObejct.put("interval", interval);
		requestObejct.put("fromdate", fromdate);
		requestObejct.put("todate", todate);
		JSONArray response = smartConnect.candleData(requestObejct);
		return response;
	}

	/**
	 * 
	 * @param exchange
	 * @param symboltoken
	 * @param interval
	 * @param fromdate
	 * @param todate
	 */

	public JSONArray getOpenIntrestData(String exchange, String symboltoken, String interval, String fromdate,String todate) {
		JSONObject requestObejct = new JSONObject();
		requestObejct.put("exchange", exchange);
		requestObejct.put("symboltoken", symboltoken);
		requestObejct.put("interval", interval);
		requestObejct.put("fromdate", fromdate);
		requestObejct.put("todate", todate);

		JSONArray oiData = smartConnect.oiData(requestObejct);

		// Process the response
		System.err.println("oiData ==" + oiData.toString());

		return oiData;

	}

	public JSONObject getOptionChain(String stockName, String expirydate) throws TradeScheduleBusinessException {
		JSONObject requestObejct = new JSONObject();
		requestObejct.put("name", stockName);
		requestObejct.put("expirydate", expirydate);
		JSONObject optionGreek = null;
		try {
			optionGreek = smartConnect.optionGreek(requestObejct);
		} catch (IOException | SmartAPIException e) {
			throw new TradeScheduleBusinessException("Issue in getOptionChain whilte fetching optionGreek stockName : "+ stockName + " expirydate: " + expirydate + " " + e.getMessage(), e);
		}

		return optionGreek;
	}

	/**
	 * 
	 * @param exchange
	 * @param tradingSymbol
	 * @param symboltoken
	 * @return
	 * @throws SmartAPIException
	 * @throws IOException
	 */
	public JSONObject getLTP(String exchange, String tradingSymbol, String symboltoken) throws TradeScheduleBusinessException {
		JSONObject ltpData = null;
		try {
			ltpData = smartConnect.getLTP(exchange, tradingSymbol, symboltoken);
		} catch (Exception e) {
			throw new TradeScheduleBusinessException("Issue in getLTP whilte fetching ltpData exchange : " + exchange + ", tradingSymbol : "+ tradingSymbol + ", symboltoken: " + symboltoken + " " + e.getMessage(),e);	
		}

		return ltpData;
	}

	/**
	 * 
	 * @param mode
	 * @param searchData
	 * @return
	 * @throws TradeScheduleBusinessException
	 */
	public JSONObject getMarketData(String mode, String searchData, String exchange)throws TradeScheduleBusinessException {

		JSONObject response = null;
		JSONObject payload = new JSONObject();
		payload.put("mode", mode);
		JSONObject exchangeTokens = new JSONObject();
		JSONArray nseTokens = new JSONArray();
		nseTokens.put(searchData);
		exchangeTokens.put(exchange, nseTokens);
		payload.put("exchangeTokens", exchangeTokens);

		try {
			response = smartConnect.marketData(payload);
		} catch (IOException | SmartAPIException e) {
			throw new TradeScheduleBusinessException("issue in getMarketData while getting market data exchange : "+ exchange + ", searchData : " + searchData + ", mode: " + mode + " " + e.getMessage(), e);
		}

		return response;

	}

	/**
	 * 
	 * @param apiKey
	 * @param clientCode
	 * @param mPin
	 * @param totp
	 * @return
	 */
	
	@SuppressWarnings("unused")
	private SmartConnect getSmartConnect(String apiKey, String clientCode, String mPin, String totp) {
		SmartConnect smartConnect = new SmartConnect(apiKey);
		User user = smartConnect.generateSession(clientCode, mPin, totp);
		smartConnect.setAccessToken(user.getAccessToken());
		smartConnect.setUserId(user.getUserId());
		smartConnect.setRefreshToken(user.getRefreshToken());
		if (apiKey.equals(getKey().get(SmartApiLogin.MARKET))) {
			this.user = user;
		} 

		return smartConnect;

	}

	/**
	 * 
	 * @param clientCode
	 * @param mPin
	 * @param totp
	 * @return
	 */

	private User getHistorySmartConnect(String clientCode, String mPin, String totp) {

		smartConnect = new SmartConnect(getKey().get(SmartApiLogin.HISTORICAL));
		User hUser = smartConnect.generateSession(clientCode, mPin, totp);
		smartConnect.setAccessToken(hUser.getAccessToken());
		smartConnect.setUserId(hUser.getUserId());

		return hUser;

	}

	/**
	 * 
	 * @param tradingSymbol
	 * @param symboltoken
	 * @param fromdate
	 * @param todate
	 * @param interval
	 * @return
	 */
	public JSONObject getHistoricalData(String tradingSymbol,
			String symboltoken,
			String fromdate,
			String todate,
			String interval) {

		JSONArray dataList = this.getCandleData(symboltoken, interval, fromdate, todate);
		return dataList != null ? new JSONObject().put("data", dataList) : null;
	}


	/**
	 * 
	 * @return
	 */

	private Map<String, String> getKey() {

		Map<String, String> mapKEY = new HashMap<>();
		mapKEY.put(SmartApiLogin.CLIENTID, "R57698459");
		mapKEY.put(SmartApiLogin.MPIN, "0786");
		mapKEY.put(SmartApiLogin.APIKEY, "OyGk7SAS");
		return mapKEY;
	}

	private static final long MINUTES = 1 * 60 * 1000L;

	@Scheduled(fixedRate = MINUTES) 
	public void publishLatestQuotes() {
		
		if(smartConnect == null || user == null || user.getFeedToken() == null) {
			System.err.println("SmartConnect or User or FeedToken is null. Skipping publishing latest quotes.");
			return;
		}


		DBTokenDetail dbTokenDetail = tokenService.getTokenAppName(TRADEConstants.SMART_API,TRADEConstants.M_TOKEN_EXPREIED_NO);
		if (dbTokenDetail == null || dbTokenDetail.getLiveOrBacktest() == null
				|| dbTokenDetail.getLiveOrBacktest().equalsIgnoreCase("backtest")) {
			System.err.println("Live/backtest mode is not live. Skipping publishing latest quotes.");
			return;
		}




		System.out.println("********* Publishing latest quotes to Kafka topic **************" );
		System.out.println(latestQuotes.size() + " latest quotes to publish.");

		for (Map.Entry<String, Object> entry : latestQuotes.entrySet()) {

			String token = entry.getKey();
			if (!activeSymbols.containsKey(token) || !(entry.getValue() instanceof SnapQuote)) {
				continue;
			}
			SnapQuote quote = (SnapQuote) latestQuotes.get(token);
			//SnapQuote quote = entry.getValue();

			//Angle One typically sends prices in paise.
			
			JSONObject tick = new JSONObject()
							.put("event", "SNAP_QUOTE")
							.put("token", token)
							.put("symbol", activeSymbols.get(token))
							.put("subscriptionId", activeSubscriptionIds.get(token))
							.put("subscriptionName", activeSubscriptionNames.get(token))
							.put("ltp", quote.getLastTradedPrice() / 100.0)
							.put("open", quote.getOpenPrice() / 100.0)
							.put("high", quote.getHighPrice() / 100.0)
							.put("low", quote.getLowPrice() / 100.0)
							.put("exchange", quote.getToken().getExchangeType().name())
							.put("close", quote.getClosePrice() / 100.0)
							.put("volume", quote.getVolumeTradedToday())
							//.put("timestamp", toLocalDateTime(quote.getExchangeFeedTimeEpochMillis()))
							.put("timestamp", LocalDateTime.now());

				//System.out.println("Tick before publish: " + tick.toString());
				tickPublisher.publish(tick.toString());

			log.info("Published {}", token);
		}
	}

	

}
