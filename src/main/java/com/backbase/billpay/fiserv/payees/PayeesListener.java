package com.backbase.billpay.fiserv.payees;

import com.backbase.billpay.fiserv.utils.AbstractListener;
import com.backbase.billpay.integration.listener.spec.v2.billpay.payees.BillPayPayeesListener;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayElectronicPayeesPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayElectronicPayeesPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayPayeesPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayPayeesPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdPutResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdPutResponseBody;
import com.backbase.buildingblocks.backend.communication.event.annotations.RequestListener;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * JMS consumer for Billpay Payees.
 * 
 * @deprecated please use {@link PayeesController}. Will be removed in DBS 2.17.0.
 */
@Service
@Deprecated
@RequestListener
@ConditionalOnProperty(name = "backbase.communication.inbound", havingValue = "JMS", matchIfMissing = true)
public class PayeesListener extends AbstractListener implements BillPayPayeesListener {

    @Autowired
    private PayeesService payeesService;
    
    @Override
    public RequestProxyWrapper<BillPayPayeesPostResponseBody> postBillPayPayees(
            RequestProxyWrapper<BillPayPayeesPostRequestBody> requestWrapper, Exchange exchange) {
        
        BillPayPayeesPostRequestBody request = requestWrapper.getRequest().getData();
        BillPayPayeesPostResponseBody response = payeesService.postBillPayPayees(
                        createFiservHeader(requestWrapper, request.getSubscriberID()), request);
        logger.debug("Post payees with request: {}, response: {}", request, response);
        return createRequestProxyWrapper(requestWrapper, response);
    }
    
    @Override
    public RequestProxyWrapper<PayeeByIdGetResponseBody> getPayeeById(
            RequestProxyWrapper<Void> requestWrapper, Exchange exchange, String id, String subscriberId) {
        
        PayeeByIdGetResponseBody response = 
                        payeesService.getBillPayPayee(createFiservHeader(requestWrapper, subscriberId), id);
        logger.debug("Get payee with id: {} for billpay user: {}, response: {}", id, subscriberId, response);
        return createRequestProxyWrapper(requestWrapper, response);
    }
    
    @Override
    public RequestProxyWrapper<PayeeByIdPutResponseBody> putPayeeById(
            RequestProxyWrapper<PayeeByIdPutRequestBody> requestWrapper, Exchange exchange, String id) {
        PayeeByIdPutRequestBody request = requestWrapper.getRequest().getData();
        PayeeByIdPutResponseBody response = payeesService.putBillPayPayees(
                        createFiservHeader(requestWrapper, request.getSubscriberID()), id, request);
        logger.debug("Put payees with id: {}, request: {}, response: {}", id, request, response);
        return createRequestProxyWrapper(requestWrapper, response);
    }

    @Override
    public RequestProxyWrapper<Void> deletePayeeById(
            RequestProxyWrapper<Void> requestWrapper, Exchange exchange, String id, 
            String subscriberId, Boolean cancelPendingPayments) {
        payeesService.deletePayeeById(createFiservHeader(requestWrapper, subscriberId), id, cancelPendingPayments);
        logger.debug("Delete payee with id: {}", id);
        return requestWrapper;
    }

    @Override
    public RequestProxyWrapper<Void> deleteElectronicPayeeById(
            RequestProxyWrapper<Void> requestWrapper, Exchange exchange, String id, String subscriberId, 
            Boolean cancelPendingPayments) {
        payeesService.deleteElectronicPayeeById(createFiservHeader(requestWrapper, subscriberId), 
                                                id, cancelPendingPayments);
        logger.debug("Delete electronic payee with id: {}", id);
        return requestWrapper;
    }

    @Override
    public RequestProxyWrapper<ElectronicPayeeByIdGetResponseBody> getElectronicPayeeById(
            RequestProxyWrapper<Void> requestWrapper, Exchange exchange, String id, String subscriberId) {
        ElectronicPayeeByIdGetResponseBody response = 
                        payeesService.getBillPayElectronicPayee(createFiservHeader(requestWrapper, subscriberId), id);
        logger.debug("Get electronic payee with id: {} for billpay user: {}, response: {}", 
                     id, subscriberId, response);
        return createRequestProxyWrapper(requestWrapper, response);
    }

    @Override
    public RequestProxyWrapper<BillPayElectronicPayeesPostResponseBody> postBillPayElectronicPayees(
            RequestProxyWrapper<BillPayElectronicPayeesPostRequestBody> requestWrapper, Exchange exchange) {
        
        BillPayElectronicPayeesPostRequestBody request = requestWrapper.getRequest().getData();
        BillPayElectronicPayeesPostResponseBody response = payeesService.postBillPayElectronicPayees(
                        createFiservHeader(requestWrapper, request.getSubscriberID()), request);
        logger.debug("Post electronic payees with request: {}, response: {}", request, response);
        return createRequestProxyWrapper(requestWrapper, response);
    }

    @Override
    public RequestProxyWrapper<ElectronicPayeeByIdPutResponseBody> putElectronicPayeeById(
            RequestProxyWrapper<ElectronicPayeeByIdPutRequestBody> requestWrapper, Exchange exchange, String id) {
        ElectronicPayeeByIdPutRequestBody request = requestWrapper.getRequest().getData();
        ElectronicPayeeByIdPutResponseBody response = payeesService.putBillPayElectronicPayees(
                        createFiservHeader(requestWrapper, request.getSubscriberID()), id, request);
        logger.debug("Put electronic payees with id: {}, request: {}, response: {}", id, request, response);
        return createRequestProxyWrapper(requestWrapper, response);
    }
}