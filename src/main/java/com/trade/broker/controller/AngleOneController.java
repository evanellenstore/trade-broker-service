package com.trade.broker.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neovisionaries.ws.client.WebSocketException;
import com.trade.broker.algo.SmartApiLogin;
import com.trade.broker.domain.IntraDayAlgoStagiesHelper;
import com.trade.broker.dto.CurrentSubscriptionsResponse;
import com.trade.broker.dto.SubscriptionRequest;
import com.trade.broker.dto.SubscriptionResponse;
import com.trade.broker.entity.DBTokenDetail;
import com.trade.broker.service.SubscriptionService;
import com.trade.broker.service.TokenService;

@RestController
@RequestMapping("/api/angelOne")
public class AngleOneController {

	private static final Logger log = LoggerFactory.getLogger(AngleOneController.class);

    @Autowired
	private TokenService tokenService;
	
	@Autowired
	private IntraDayAlgoStagiesHelper intraDayAlgoStagiesHelper;

	@Autowired
	private SmartApiLogin smartApiLogin;
	
	@Autowired
	private SubscriptionService subscriptionService;

	/**
	 * 
	 * @param ttop
	 * @return
	 */
    @PostMapping("/login/byTtop")
	public ResponseEntity<String> loginInAPP(
			@RequestParam String ttop,
			@RequestParam(name = "mode", defaultValue = "live") String mode) {
		DBTokenDetail dBTokenDetail=intraDayAlgoStagiesHelper.loginSmartApi(ttop);
		dBTokenDetail.setLiveOrBacktest(mode);
		log.debug("Login request received for ttop token");
		DBTokenDetail result=tokenService.saveToken(dBTokenDetail);
		//intraDayAlgoStagiesHelper.loginPortalSmartApi(ttop);
		if(result!=null) {
			return ResponseEntity.ok("login successfully !");
		}else {
			return ResponseEntity.ok("unable to login!");
		}
		
	}

	/**
	 * 
	 * @return
	 */
	@GetMapping("/relogin")
	public ResponseEntity<String> reloginInAPP(
			@RequestParam(name = "ttop", required = false) String ttop,
			@RequestParam(name = "mode", defaultValue = "live") String mode) {
		String result = intraDayAlgoStagiesHelper.reLoginSmartApi(ttop, mode);
		return ResponseEntity.ok(result);
	}





	//Historical │
     // │ Orders     │
     // │ Positions  │
      //│ Holdings 
	
	
	
	/**
	 * 
	 * @return
	 */
	@GetMapping("/logout")
	public ResponseEntity<String> logoutInAPP() {
		intraDayAlgoStagiesHelper.logutSmartApi();
		return ResponseEntity.ok("logout ..");
	}


/**
 * 
 * @param exchange
 * @param tradingSymbol
 * @param symboltoken
 * @return
 */
	@GetMapping("/ltp")
	public ResponseEntity<String> getLTP(@RequestParam String exchange, @RequestParam String tradingSymbol, @RequestParam String symboltoken) {	
		JSONObject ltpObject=	smartApiLogin.getLTP(exchange, tradingSymbol, symboltoken);
		if(ltpObject == null) {
			log.warn("Unable to fetch LTP data for exchange={}, tradingSymbol={}, symbolToken={}", exchange, tradingSymbol, symboltoken);
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body("Unable to get LTP data. Please re-login to the Smart API.");
		}
		
		return ResponseEntity.ok(ltpObject.toString());	
	}

	/**
	 * 
	 * @param tradingSymbol
	 * @param symbolToken
	 * @param fromDate
	 * @param toDate
	 * @param interval
	 * @return
	 */
	@GetMapping("/candlesData")
	public ResponseEntity<?> getCandleData(@RequestParam String tradingSymbol,	@RequestParam String symbolToken,
			@RequestParam String fromDate,@RequestParam String toDate,@RequestParam String interval) {

		try {

			JSONObject response = smartApiLogin.getHistoricalData(tradingSymbol,symbolToken,fromDate,toDate,interval);

			if (response == null) {
				return ResponseEntity.badRequest()
						.body("Unable to fetch historical data. Please login again.");
			}

			return ResponseEntity.ok(response.toString(4));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}


	/**
	 * 
	 * @param exchange
	 * @param symboltoken
	 * @return
	 */
	@GetMapping("/marketData")
	public ResponseEntity<String> getMarketData(@RequestParam String exchange,
		 @RequestParam String symboltoken,@RequestParam String mode) 
	{	
		JSONObject marketDataObject= smartApiLogin.getMarketData(mode,symboltoken,exchange) ; 
		if(marketDataObject == null) {
			log.warn("Unable to fetch market data for exchange={}, symbolToken={}, mode={}", exchange, symboltoken, mode);
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body("Unable to get Market data. Please re-login to the Smart API.");
	}
		
		return ResponseEntity.ok(marketDataObject.toString());	
	}
	



	/**
	 * Subscribe to symbols for a given exchange
	 * POST /api/v1/broker/subscriptions
	 * @param request SubscriptionRequest with exchange and list of symbols
	 * @return SubscriptionResponse with status and subscribed symbols
	 */
	@PostMapping("/subscriptions")
	public ResponseEntity<SubscriptionResponse> subscribe(@RequestBody SubscriptionRequest request) {
		try {
			SubscriptionResponse response = subscriptionService.subscribe(request);
			return ResponseEntity.ok(response);
		} catch (WebSocketException e) {
			log.error("Error subscribing to symbols", e);
			SubscriptionResponse errorResponse = new SubscriptionResponse();
			errorResponse.setStatus("ERROR");
			errorResponse.setMessage("Error subscribing to symbols: " + e.getMessage());
			return ResponseEntity.internalServerError().body(errorResponse);
		} catch (Exception e) {
			log.error("Error subscribing to symbols", e);
			SubscriptionResponse errorResponse = new SubscriptionResponse();
			errorResponse.setStatus("ERROR");
			errorResponse.setMessage("Error subscribing to symbols: " + e.getMessage());
			return ResponseEntity.internalServerError().body(errorResponse);
		}
	}

	/**
	 * Unsubscribe from symbols for a given exchange
	 * DELETE /api/v1/broker/subscriptions
	 * @param request SubscriptionRequest with exchange and list of symbols
	 * @return SubscriptionResponse with status
	 */
	@DeleteMapping("/subscriptions")
	public ResponseEntity<SubscriptionResponse> unsubscribe(@RequestBody SubscriptionRequest request) {
		try {
			SubscriptionResponse response = subscriptionService.unsubscribe(request);
			return ResponseEntity.ok(response);
		} catch (WebSocketException e) {
			log.error("Error unsubscribing from symbols", e);
			SubscriptionResponse errorResponse = new SubscriptionResponse();
			errorResponse.setStatus("ERROR");
			errorResponse.setMessage("Error unsubscribing from symbols: " + e.getMessage());
			return ResponseEntity.internalServerError().body(errorResponse);
		} catch (Exception e) {
			log.error("Error unsubscribing from symbols", e);
			SubscriptionResponse errorResponse = new SubscriptionResponse();
			errorResponse.setStatus("ERROR");
			errorResponse.setMessage("Error unsubscribing from symbols: " + e.getMessage());
			return ResponseEntity.internalServerError().body(errorResponse);
		}
	}

	/**
	 * Get current subscriptions for a given exchange
	 * GET /api/v1/broker/subscriptions?exchange=NSE
	 * @param exchange The exchange name
	 * @return CurrentSubscriptionsResponse with exchange and subscribed symbols
	 */
	@GetMapping("/subscriptions")
	public ResponseEntity<CurrentSubscriptionsResponse> getCurrentSubscriptions(@RequestParam String exchange) {
		try {
			CurrentSubscriptionsResponse response = subscriptionService.getCurrentSubscriptions(exchange);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error fetching current subscriptions", e);
			return ResponseEntity.internalServerError().body(null);
		}
	}

	@PostMapping("/subscriptions/{subscriptionId}/start")
	public ResponseEntity<SubscriptionResponse> startSubscription(@PathVariable String subscriptionId) {
		try {
			return ResponseEntity.ok(subscriptionService.start(subscriptionId));
		} catch (Exception e) {
			log.error("Error starting subscription {}", subscriptionId, e);
			return ResponseEntity.internalServerError().body(null);
		}
	}

	@PutMapping("/subscriptions/{subscriptionId}")
	public ResponseEntity<SubscriptionResponse> updateSubscription(@PathVariable String subscriptionId,
			@RequestBody SubscriptionRequest request) {
		try {
			return ResponseEntity.ok(subscriptionService.update(subscriptionId, request));
		} catch (Exception e) {
			log.error("Error updating subscription {}", subscriptionId, e);
			return ResponseEntity.internalServerError().body(null);
		}
	}

	@DeleteMapping("/subscriptions/{subscriptionId}")
	public ResponseEntity<SubscriptionResponse> deleteSubscription(@PathVariable String subscriptionId) {
		try {
			return ResponseEntity.ok(subscriptionService.deleteBatch(subscriptionId));
		} catch (Exception e) {
			log.error("Error deleting subscription {}", subscriptionId, e);
			return ResponseEntity.internalServerError().body(null);
		}
	}


}
