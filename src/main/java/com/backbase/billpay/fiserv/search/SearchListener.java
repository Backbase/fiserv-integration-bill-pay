package com.backbase.billpay.fiserv.search;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.utils.AbstractListener;
import com.backbase.billpay.integration.listener.spec.v2.billpay.payeessearch.BillPaySearchListener;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessearch.BillPaySearchGetResponseBody;
import com.backbase.buildingblocks.backend.communication.event.annotations.RequestListener;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * JMS consumer for payees search.
 */
@Service
@RequestListener
public class SearchListener extends AbstractListener implements BillPaySearchListener {

    @Autowired
    private SearchService searchService;
    
    @Override
    public RequestProxyWrapper<BillPaySearchGetResponseBody> getBillPaySearch(RequestProxyWrapper<Void> request,
            Exchange exchange, String subscriberId, String name, String zipCode, String accountNumber) {
        Header header = createFiservHeader(request, subscriberId);
        BillPaySearchGetResponseBody payees = 
                        searchService.getBillPayPayeesSearch(header, name, zipCode, accountNumber);
        logger.debug("Get payees for name: {}, returned payees: {}", name, payees, zipCode, accountNumber);
        return createRequestProxyWrapper(request, payees);
    }
}
