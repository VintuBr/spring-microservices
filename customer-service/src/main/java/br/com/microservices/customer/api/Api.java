package br.com.microservices.customer.api;

import static java.util.Optional.ofNullable;

import br.com.microservices.customer.intercomm.AccountClient;
import br.com.microservices.customer.intercomm.BalanceClient;
import br.com.microservices.customer.model.Account;
import br.com.microservices.customer.model.Customer;
import br.com.microservices.customer.model.CustomerType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Api {

	@Autowired
	private AccountClient accountClient;

	@Autowired
    private BalanceClient balanceClient;

	protected Logger logger = Logger.getLogger(Api.class.getName());

	private List<Customer> customers;

	public Api() {
		customers = new ArrayList<>();
		customers.add(new Customer(1, "12345", "Adam Kowalski", CustomerType.INDIVIDUAL));
		customers.add(new Customer(2, "12346", "Anna Malinowska", CustomerType.INDIVIDUAL));
		customers.add(new Customer(3, "12347", "Paweł Michalski", CustomerType.INDIVIDUAL));
		customers.add(new Customer(4, "12348", "Karolina Lewandowska", CustomerType.INDIVIDUAL));
	}

	@RequestMapping("/customers/pesel/{pesel}")
	public Customer findByPesel(@PathVariable("pesel") String pesel) {
		logger.info(String.format("Customer.findByPesel(%s)", pesel));
		return customers.stream().filter(it -> it.getPesel().equals(pesel)).findFirst().get();
	}

	@RequestMapping("/customers")
	public List<Customer> findAll() {
		logger.info("Customer.findAll()");
		return customers;
	}

	@RequestMapping("/customers/{id}")
	public Customer findById(@PathVariable("id") Integer id) {
		logger.info(String.format("Customer.findById(%s)", id));
		Customer customer = customers.stream().filter(it -> it.getId().intValue()==id.intValue()).findFirst().get();
		List<Account> accounts =  accountClient.getAccounts(id);
		customer.setAccounts(accounts);
		setBalanceForAccount(ofNullable(accounts));

		return customer;
	}

	private void setBalanceForAccount(Optional<List<Account>> accountsOpt) {
        accountsOpt.ifPresent(accounts ->
                accounts.forEach(account -> {
                    account.setBalance(balanceClient.getBalance(account.getNumber()));
                }
            )
        );
    }
}
