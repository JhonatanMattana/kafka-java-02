package br.com.ecommerce;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Order {

	private final String userId;
	private final String orderId;
    private final BigDecimal amount;
    
	public Order(String userId, String orderId, BigDecimal amount) {
		this.userId = userId;
		this.orderId = orderId;
		this.amount = amount;
	}
	
	public BigDecimal getAmount() {
		return this.amount;
	}
	
	public String getUserId() {
		return userId;
	}

	@Override
	public String toString() {
		return "Order [userId=" + userId + ", orderId=" + orderId + ", amount=" + amount.setScale(2, RoundingMode.HALF_UP) + "]";
	}
	
}