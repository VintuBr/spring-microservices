package br.com.microservices.customer.model;

import java.math.BigDecimal;

public class Balance {

	private final String number;
	private final BigDecimal balance;

	public Balance(String number) {
	    this(number, BigDecimal.ZERO);
	}

	public Balance(String number, BigDecimal balance) {
		this.number = number;
		this.balance = balance;
	}

    public String getNumber() {
        return number;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
