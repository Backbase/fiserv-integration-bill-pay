package com.backbase.billpay.fiserv.accounts;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.utils.AbstractListener;
import com.backbase.billpay.integration.listener.spec.v2.billpay.accounts.BillPayAccountsListener;
import com.backbase.billpay.integration.rest.spec.v2.billpay.accounts.BillPayAccountsGetResponseBody;
import com.backbase.buildingblocks.backend.communication.event.annotations.RequestListener;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * JMS consumer for Billpay accounts.
 */
@Service
@RequestListener
public class AccountsListener extends AbstractListener implements BillPayAccountsListener {

    @Autowired
    private AccountsService accountsService;
    
    @Override
    public RequestProxyWrapper<BillPayAccountsGetResponseBody> getBillPayAccounts(RequestProxyWrapper<Void> request,
            Exchange exchange, String subscriberId) {
        Header header = createFiservHeader(request, subscriberId);
        BillPayAccountsGetResponseBody accounts = accountsService.getAccounts(header);
        logger.debug("Found accounts for subscriberID: {}, returned accounts: {}", subscriberId, accounts);
        return createRequestProxyWrapper(request, accounts);
    }
}
