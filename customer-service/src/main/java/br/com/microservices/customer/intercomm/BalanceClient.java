package br.com.microservices.customer.intercomm;

import br.com.microservices.customer.model.Balance;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "balance-service", fallback = BalanceClientFallbackImpl.class)
public interface BalanceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/balances/{number}")
    Balance getBalance(@PathVariable("number") String account);
}
