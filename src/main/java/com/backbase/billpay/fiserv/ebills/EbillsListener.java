package com.backbase.billpay.fiserv.ebills;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.utils.AbstractListener;
import com.backbase.billpay.integration.listener.spec.v2.billpay.payees.electronic.id.ebills.BillPayEbillsListener;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.BillPayEbillStatementsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.BillPayEbillsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutResponseBody;
import com.backbase.buildingblocks.backend.communication.event.annotations.RequestListener;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import java.util.Date;
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
    
    @Override
    public RequestProxyWrapper<BillPayEbillsGetResponseBody> getBillPayEbills(RequestProxyWrapper<Void> request,
                    Exchange exchange, String payeeId, String subscriberId, String status, Date startDate, Date endDate,
                    Integer from, Integer size, String orderBy, String direction) {
        logger.debug("Retrieving eBills for payee with id: {}, status: {}, startDate: {}, endDate: {}, from: {}, "
                        + "size: {}, orderBy: {}, direction: {}", payeeId, status, startDate, endDate, from, size,
                        orderBy, direction);
        Header header = createFiservHeader(request, subscriberId);
        BillPayEbillsGetResponseBody response = ebillsService.getEbills(header, payeeId, status, startDate, endDate,
                        from, size, orderBy, direction);
        logger.debug("Retrieved eBills for payee with id: {}, status: {}, startDate: {}, endDate: {}, from: {}, "
                        + "size: {}, orderBy: {}, direction: {}, response: {}", payeeId, status, startDate, endDate,
                        from, size, orderBy, direction, response);
        return createRequestProxyWrapper(request, response);
    }
    
    @Override
    public RequestProxyWrapper<BillPayEbillStatementsGetResponseBody> getBillPayEbillStatements(
                    RequestProxyWrapper<Void> request, Exchange exchange, String payeeId, String ebillId,
                    String subscriberId) {
        logger.debug("Unable to retrieve statement for eBill with id: {} for payee: {}, this functionality is not "
                        + "supported by the Bill Pay provider", ebillId, payeeId);
        throw new UnsupportedOperationException(
                        "Retrieval of eBill statements is not supported by the Bill Pay provider");
    }
    
    @Override
    public RequestProxyWrapper<EbillByIdPutResponseBody> putEbillById(
                    RequestProxyWrapper<EbillByIdPutRequestBody> ebillByIdPutRequestBody, Exchange exchange, String payeeId,
                    String ebillId) {
        EbillByIdPutRequestBody request = ebillByIdPutRequestBody.getRequest().getData();
        logger.debug("Updating eBill with id: {} for payee: {} with request: {}", ebillId, payeeId, request);
        
        if (!request.getPaid()) {
            throw new UnsupportedOperationException("Unfiling an eBill is not supported by the Bill Pay provider");
        }
        
        Header header = createFiservHeader(ebillByIdPutRequestBody, request.getSubscriberID());
        EbillByIdPutResponseBody response = ebillsService.updateEbillStatus(header, request, ebillId);
        logger.debug("Updated eBill with id: {} for payee: {} with request: {} and response: {}", ebillId, payeeId, request, response);
        return createRequestProxyWrapper(ebillByIdPutRequestBody, response);
    }

}
