package com.trade.broker.controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neovisionaries.ws.client.WebSocketException;
import com.trade.broker.algo.SmartApiLogin;
import com.trade.broker.domain.IntraDayAlgoStagiesHelper;
import com.trade.broker.entity.DBTokenDetail;
import com.trade.broker.service.TokenService;

@RestController
@RequestMapping("/api/angelOne")
public class AngleOneController {

    @Autowired
	private TokenService tokenService;
	
	@Autowired
	private IntraDayAlgoStagiesHelper intraDayAlgoStagiesHelper;

	@Autowired
	private SmartApiLogin smartApiLogin;

	/**
	 * 
	 * @param ttop
	 * @return
	 */
    @PostMapping("/login/byTtop")
	public ResponseEntity<String> loginInAPP(@RequestParam String ttop) {
		DBTokenDetail dBTokenDetail=intraDayAlgoStagiesHelper.loginSmartApi(ttop);
		System.out.println("==getMaccesstoken===="+dBTokenDetail.getAccesstoken());
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
	public ResponseEntity<String> reloginInAPP() {
		String result=intraDayAlgoStagiesHelper.reLoginSmartApi();
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
			return ResponseEntity.ok("Unable to get LTP data. Please re-login to the Smart API.");
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
			return ResponseEntity.ok("Unable to get Market data. Please re-login to the Smart API.");
	}
		
		return ResponseEntity.ok(marketDataObject.toString());	
	}
	

	/**
	 * Subscribes to socket updates for a given stock entity.
	 * @param exchange
	 * @param tradingSymbol
	 * @param mode
	 * @return
	 */
	@PostMapping("/subscribeSocketConnect")
	public ResponseEntity<String> subscribeSocketConnect(@RequestBody List<String> listOfTokens) {

			try {
				smartApiLogin.subcribeToSmartStreamConnect(listOfTokens);
			} catch (WebSocketException e) {
				e.printStackTrace();
				return ResponseEntity.internalServerError().body("Error subscribing to socket updates: " + e.getMessage());
			}
				
		return ResponseEntity.ok("Socket subscription feature is implemented yet.");

	}


}
