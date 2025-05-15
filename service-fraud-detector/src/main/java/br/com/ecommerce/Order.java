package br.com.ecommerce;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Order {

	private final String orderId;
    private final BigDecimal amount;
	private final String email;
    
	public Order(String orderId, BigDecimal amount, String email) {
		this.orderId = orderId;
		this.amount = amount;
		this.email = email;
	}
	
	public BigDecimal getAmount() {
		return this.amount;
	}
	
	public String getEmail() {
		return email;
	}
	
	@Override
	public String toString() {
		return "Order [email= " + email + ", orderId="  + orderId + ", amount= " + amount.setScale(2, RoundingMode.HALF_UP) + "]";
	}
	
}