package com.trade.broker.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	
	@GetMapping("/relogin")
	public ResponseEntity<String> reloginInAPP() {
		String result=intraDayAlgoStagiesHelper.reLoginSmartApi();
		return ResponseEntity.ok(result);
	}





	//Historical │
     // │ Orders     │
     // │ Positions  │
      //│ Holdings 
	
	
	
	
	@GetMapping("/logout")
	public ResponseEntity<String> logoutInAPP() {
		intraDayAlgoStagiesHelper.logutSmartApi();
		return ResponseEntity.ok("logout ..");
	}



	@GetMapping("/ltp")
	public ResponseEntity<String> getLTP(@RequestParam String exchange, @RequestParam String tradingSymbol, @RequestParam String symboltoken) {
	 
		
		JSONObject ltpObject=	smartApiLogin.getLTP(exchange, tradingSymbol, symboltoken);

		
		return ResponseEntity.ok(ltpObject.toString());	
	}





}
