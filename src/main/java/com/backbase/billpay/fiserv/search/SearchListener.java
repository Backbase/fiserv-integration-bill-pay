package com.backbase.billpay.fiserv.search;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.utils.AbstractListener;
import com.backbase.billpay.integration.listener.spec.v2.billpay.payeessearch.BillPaySearchListener;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessearch.BillPaySearchGetResponseBody;
import com.backbase.buildingblocks.backend.communication.event.annotations.RequestListener;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * JMS consumer for payees search.
 * 
 * @deprecated please use {@link SearchController}. Will be removed in DBS 2.17.0.
 */
@Service
@Deprecated
@RequestListener
@ConditionalOnProperty(name = "backbase.communication.inbound", havingValue = "JMS", matchIfMissing = true)
public class SearchListener extends AbstractListener implements BillPaySearchListener {

    @Autowired
    private SearchService searchService;
    
    @Override
    public RequestProxyWrapper<BillPaySearchGetResponseBody> getBillPaySearch(RequestProxyWrapper<Void> request,
            Exchange exchange, String subscriberId, String name, String zipCode, String accountNumber) {
        Header header = createFiservHeader(request, subscriberId);
        BillPaySearchGetResponseBody payees = 
                        searchService.getBillPayPayeesSearch(header, name, zipCode, accountNumber);
        logger.debug("Search payees matching name: {}, zipCode:{}, payeeAccountNumber:{}. Found matching payees: {}", 
                        name, zipCode, accountNumber, payees);
        return createRequestProxyWrapper(request, payees);
    }
}
