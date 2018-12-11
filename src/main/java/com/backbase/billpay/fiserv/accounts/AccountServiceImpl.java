package com.backbase.billpay.fiserv.accounts;

import com.backbase.billpay.fiserv.accounts.model.BankAccountListRequest;
import com.backbase.billpay.fiserv.accounts.model.BankAccountListResponse;
import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.search.SearchServiceImpl;
import com.backbase.billpay.fiserv.utils.FiservClient;
import com.backbase.billpay.integration.rest.spec.v2.billpay.accounts.BillPayAccountsGetResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Account Service.
 */
@Service
public class AccountServiceImpl implements AccountsService {

    private static final String SOAP_ACTION = "BankAccountList";
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);
    private final AccountsMapper mapper;
    private final FiservClient client;
    
    /**
     * Setup client and mapper.
     * @param mapper mapper for this service
     * @param client provider client service
     */
    @Autowired
    public AccountServiceImpl(AccountsMapper mapper, FiservClient client) {
        this.mapper = mapper;
        this.client = client;
    }
    
    @Override
    public BillPayAccountsGetResponseBody getAccounts(Header header) {
        BankAccountListRequest request = BankAccountListRequest.builder().header(header).build();
        LOGGER.debug("Created request: {}", request);
        BankAccountListResponse response = client.call(request, SOAP_ACTION);
        LOGGER.debug("Received response: {}", response);
        return mapper.toBillPayAccountsGetResponseBody(response);
    }
}