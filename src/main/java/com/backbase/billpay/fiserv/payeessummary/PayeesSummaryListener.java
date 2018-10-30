package com.backbase.billpay.fiserv.payeessummary;

import com.backbase.billpay.fiserv.utils.AbstractListener;
import com.backbase.billpay.integration.listener.spec.v2.billpay.payeessummary.BillPayPayeesSummaryListener;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.BillPayPayeesSummaryGetResponseBody;
import com.backbase.buildingblocks.backend.communication.event.annotations.RequestListener;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * JMS consumer for Billpay Payee Summaries.
 */
@Service
@RequestListener
public class PayeesSummaryListener extends AbstractListener implements BillPayPayeesSummaryListener {

    @Autowired
    private PayeesSummaryService payeesSummaryService;
    
    @Override
    public RequestProxyWrapper<BillPayPayeesSummaryGetResponseBody> getBillPayPayeesSummary(
            RequestProxyWrapper<Void> request, Exchange exchange, String subscriberId) {
        BillPayPayeesSummaryGetResponseBody payeesSummary =
                        payeesSummaryService.getBillPayPayeesSummary(createFiservHeader(request, subscriberId));
        logger.debug("Get payees summary for subscriberID: {}, found summaries: {}", subscriberId, payeesSummary); 
        return createRequestProxyWrapper(request, payeesSummary);
    }
}
