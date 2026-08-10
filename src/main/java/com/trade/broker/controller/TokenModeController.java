package com.trade.broker.controller;

import com.trade.broker.entity.DBTokenDetail;
import com.trade.broker.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
public class TokenModeController {

    @Autowired
    private TokenService tokenService;

    @GetMapping("/mode")
    public ResponseEntity<TokenModeResponse> getTokenMode(
            @RequestParam(name = "clientId", required = false) String clientId,
            @RequestParam(name = "appName", required = false, defaultValue = "smartapi") String appName) {

        DBTokenDetail dbTokenDetail = tokenService.getTokenAppName(appName, "N");
        if (dbTokenDetail == null) {
            return ResponseEntity.ok(new TokenModeResponse("live"));
        }

        String mode = dbTokenDetail.getLiveOrBacktest();
        if (mode == null || mode.isBlank()) {
            mode = "live";
        }

        return ResponseEntity.ok(new TokenModeResponse(mode));
    }

    public static class TokenModeResponse {
        private String mode;

        public TokenModeResponse() {
        }

        public TokenModeResponse(String mode) {
            this.mode = mode;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }
    }
}
