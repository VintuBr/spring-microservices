package br.com.microservices.customer.intercomm;

import br.com.microservices.customer.model.Balance;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.logging.Logger;

@Component
public class BalanceClientFallbackImpl implements BalanceClient {

    private static final Logger LOG = Logger.getLogger(BalanceClientFallbackImpl.class.getName());

    @Override
    public Balance getBalance(String account) {
        Balance balance = new Balance(account, BigDecimal.ZERO);

        LOG.info("Looks like: [balance-service] is down! Using fallback implementation! Returnig: [" + balance + "]");
        return balance;
    }
}

