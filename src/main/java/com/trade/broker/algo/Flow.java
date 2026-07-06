package com.trade.broker.algo;

public class Flow {
	
	/*
	
	option are 2 type
	1. call option --> when you think price will go up then buy call option
	2. put option ---> when you think price will go down then buy put option
	
	price vs premium
	
	when purchage stock then using price but when you purchare option by using premium
	
	strike price is known as price
	
	
	Moneyness:

	At-The-Money (ATM):

		Options whose strike price is very close to the current price of the underlying asset. ATM options 
		usually have the highest liquidity and delta around 0.5. They’re often chosen by traders who want 
		a balanced risk/reward profile.
		
	In-The-Money (ITM):

		For call options, these have a strike price below the current market price; for puts, above it. 
		ITM options have a higher delta, meaning they move more closely with the underlying asset, but they are also
		more expensive. They’re preferred if you need a higher probability of profit.
		
	Out-Of-The-Money (OTM):

		These options have a strike price further from the current price. They’re cheaper and offer higher leverage, 
		but they come with a lower probability of finishing in the money. They’re often chosen when you expect a strong 
		move in the underlying asset.
	
	Risk/Reward Trade-Off:

		A lower strike (for calls) or a higher strike (for puts) might provide a higher chance of profit (higher delta), but they cost more.

		OTM options are less expensive and can offer high returns if the underlying asset makes a significant move, but they 
		can expire worthless if the move isn’t substantial enough.
		

	Option Greeks:

	Delta: Indicates how much the option price is expected to change with a small change in the underlying asset. 
	A higher delta (closer to 1 for ITM options, around 0.5 for ATM options) generally means a higher probability of expiring in the money.
	
	Gamma: Shows how quickly the delta changes as the underlying price changes. A high gamma near ATM options means small price moves can 
	greatly change the delta.
	
	Theta: Represents time decay. Options with a lower theta will lose value more slowly as expiration approaches.
	
	Vega: Indicates sensitivity to changes in implied volatility. In a volatile market, a strike with higher vega might benefit 
	more from increases in volatility.
	

	
	
	
	
	
	
	 */

}
