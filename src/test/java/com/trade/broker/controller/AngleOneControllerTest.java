package com.trade.broker.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.trade.broker.algo.SmartApiLogin;
import com.trade.broker.domain.IntraDayAlgoStagiesHelper;
import com.trade.broker.service.SubscriptionService;
import com.trade.broker.service.TokenService;

@ExtendWith(MockitoExtension.class)
class AngleOneControllerTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private IntraDayAlgoStagiesHelper intraDayAlgoStagiesHelper;

    @Mock
    private SmartApiLogin smartApiLogin;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private AngleOneController controller;

    @Test
    void getLtpShouldReturnBadGatewayWhenSmartApiReturnsNull() {
        when(smartApiLogin.getLTP("NSE", "SBIN", "3045")).thenReturn(null);

        ResponseEntity<String> response = controller.getLTP("NSE", "SBIN", "3045");

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertTrue(response.getBody().contains("Unable to get LTP data"));
    }

    @Test
    void getMarketDataShouldReturnBadGatewayWhenSmartApiReturnsNull() {
        when(smartApiLogin.getMarketData("full", "3045", "NSE")).thenReturn(null);

        ResponseEntity<String> response = controller.getMarketData("NSE", "3045", "full");

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertTrue(response.getBody().contains("Unable to get Market data"));
    }

    @Test
    void reloginShouldAcceptTtopAndModeAndReturnHelperResponse() {
        when(intraDayAlgoStagiesHelper.reLoginSmartApi("705936", "live")).thenReturn("re-login successfully");

        ResponseEntity<String> response = controller.reloginInAPP("705936", "live");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("re-login successfully", response.getBody());
    }
}
