package br.com.microservices.customer.intercomm;

import br.com.microservices.customer.model.Account;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class AccountClientFallbackImpl implements AccountClient {

    private static final Logger LOG = Logger.getLogger(AccountClientFallbackImpl.class.getName());

    @Override
    public List<Account> getAccounts(Integer customerId) {
        LOG.info("Looks like: [account-service] is down! Using fallback implementation!");
        return new ArrayList<>();
    }
}

