package com.trade.broker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FNOStockSymbolDto {
    private String symboltoken;
    private String tradingsymbol;
}
