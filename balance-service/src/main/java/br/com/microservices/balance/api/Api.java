package br.com.microservices.balance.api;

import br.com.microservices.balance.model.Balance;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Api {

	private List<Balance> balances;

	protected Logger logger = Logger.getLogger(Api.class.getName());

	public Api() {
		balances = new ArrayList<>();
        balances.add(new Balance("111111"));
        balances.add(new Balance("222222"));
        balances.add(new Balance("333333"));
        balances.add(new Balance("444444"));
        balances.add(new Balance("555555"));
        balances.add(new Balance("666666"));
        balances.add(new Balance("777777"));
	}

	@RequestMapping("/balances/{number}")
	public Balance findByNumber(@PathVariable("number") String number) {
		logger.info(String.format("Balance.findByNumber(%s)", number));
		return balances.stream().filter(it -> it.getNumber().equals(number)).findFirst().get();
	}

	@RequestMapping("/balances")
	public List<Balance> findAll() {
		logger.info("Balance.findAll()");
		return balances;
	}

}
