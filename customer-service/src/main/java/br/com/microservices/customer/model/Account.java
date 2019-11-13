package br.com.microservices.customer.model;

public class Account {

	private Integer id;
	private String number;
    private Balance balance;

	public Account() {
	}

	public Account(Integer id, String number) {
		this.id = id;
		this.number = number;
	}

    public Account(Integer id, String number, Balance balance) {
        this.id = id;
        this.number = number;
        this.balance = balance;
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }
}
