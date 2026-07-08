package com.trade.broker.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentSubscriptionsResponse {
	
	private String exchange;
	private List<String> symbols;
	
}
