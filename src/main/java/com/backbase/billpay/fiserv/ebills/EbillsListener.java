package com.backbase.billpay.fiserv.ebills;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.utils.AbstractListener;
import com.backbase.billpay.integration.listener.spec.v2.billpay.payees.electronic.id.ebills.BillPayEbillsListener;
import com.backbase.buildingblocks.backend.communication.event.annotations.RequestListener;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * JMS consumer for Bill Pay eBills.
 */
@Service
@RequestListener
public class EbillsListener extends AbstractListener implements BillPayEbillsListener {
    
    @Autowired
    private EbillsService ebillsService;
    
    @Override
    public RequestProxyWrapper<Void> deleteBillPayEbills(RequestProxyWrapper<Void> request, Exchange exchange,
                    String payeeId, String subscriberId) {
        Header header = createFiservHeader(request, subscriberId);
        ebillsService.disableEbills(header, payeeId);
        logger.debug("Disable eBills for payee with id: {}", payeeId);
        return request;
    }

}
