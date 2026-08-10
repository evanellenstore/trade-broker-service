package com.trade.broker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trade.broker.domain.IntraDayAlgoStagiesHelper;
import com.trade.broker.entity.DBTokenDetail;
import com.trade.broker.service.TokenService;

@RestController
@RequestMapping("/api")
public class LoginController {


	@Autowired
	private TokenService tokenService;
	@Autowired
	private IntraDayAlgoStagiesHelper intraDayAlgoStagiesHelper;
	

	@PostMapping("/login/byTtop")
	public ResponseEntity<String> loginInAPP(
			@RequestParam String ttop,
			@RequestParam(name = "mode", defaultValue = "live") String mode) {
		DBTokenDetail dBTokenDetail = intraDayAlgoStagiesHelper.loginSmartApi(ttop);
		dBTokenDetail.setLiveOrBacktest(mode);
		System.out.println("==getMaccesstoken====" + dBTokenDetail.getAccesstoken());
		DBTokenDetail result = tokenService.saveToken(dBTokenDetail);
		//intraDayAlgoStagiesHelper.loginPortalSmartApi(ttop);
		if (result != null) {
			return ResponseEntity.ok("login successful in " + mode + " mode!");
		} else {
			return ResponseEntity.ok("unable to login!");
		}
		
	}
	
	@GetMapping("/relogin")
	public ResponseEntity<String> reloginInAPP(
			@RequestParam(name = "ttop", required = false) String ttop,
			@RequestParam(name = "mode", defaultValue = "live") String mode) {
		String result = intraDayAlgoStagiesHelper.reLoginSmartApi(ttop, mode);
		return ResponseEntity.ok(result);
	}
	
	
	
	@GetMapping("/logout")
	public ResponseEntity<String> logoutInAPP() {
		intraDayAlgoStagiesHelper.logutSmartApi();
		return ResponseEntity.ok("logout ..");
	}
	
	
	@GetMapping("/readtopgainerandlooser")
	public ResponseEntity<String> portalLogin() {
		intraDayAlgoStagiesHelper.initAngleOneWeb();
		intraDayAlgoStagiesHelper.readTopGainerAndLooserFromAngleOnePoratal();
		//intraDayAlgoStagiesHelper.createWatchList("coforge-eq");
		return ResponseEntity.ok("done");
	}
	
	
	

}
