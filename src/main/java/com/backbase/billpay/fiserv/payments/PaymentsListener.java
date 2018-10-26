package com.backbase.billpay.fiserv.payments;

import com.backbase.billpay.fiserv.utils.AbstractListener;
import com.backbase.billpay.integration.listener.spec.v2.billpay.payments.BillPayPaymentsListener;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayRecurringPaymentsPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayRecurringPaymentsPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdPutResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdPutResponseBody;
import com.backbase.buildingblocks.backend.communication.event.annotations.RequestListener;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import java.util.Date;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * JMS consumer for Billpay Payments.
 */
@Service
@RequestListener
public class PaymentsListener extends AbstractListener implements BillPayPaymentsListener {
    
    @Autowired
    private PaymentsService paymentsService;
    
    @Override
    public RequestProxyWrapper<Void> deletePaymentById(
            RequestProxyWrapper<Void> requestWrapper, Exchange exchange, String id, String subscriberId) {
        paymentsService.deletePaymentById(createFiservHeader(requestWrapper, subscriberId), id);
        logger.debug("Delete payment with id: {}", id);
        return requestWrapper;
    }
    
    @Override
    public RequestProxyWrapper<BillPayPaymentsGetResponseBody> getBillPayPayments(
            RequestProxyWrapper<Void> requestWrapper, Exchange exchange, 
            String subscriberId, String status, Date startDate, Date endDate, 
            String payeeId, Integer from, Integer size) {
        BillPayPaymentsGetResponseBody payments = 
                paymentsService.getBillPayPayments(createFiservHeader(requestWrapper, subscriberId), 
                                                   status, startDate, endDate, payeeId, from, size);
        logger.debug("Get payments for subscriberId: {}, status: {}, startDate: {}, endDate: {}, payeeID:{}, "
                     + "from: {}, size: {}, found payments: {}", 
                     subscriberId, status, startDate, endDate, payeeId, from, size, payments);
        return createRequestProxyWrapper(requestWrapper, payments);
    }
    
    @Override
    public RequestProxyWrapper<BillPayPaymentsPostResponseBody> postBillPayPayments(
            RequestProxyWrapper<BillPayPaymentsPostRequestBody> requestWrapper, Exchange exchange) {
        BillPayPaymentsPostRequestBody request = requestWrapper.getRequest().getData();
        BillPayPaymentsPostResponseBody response = paymentsService.postBillPayPayments(
                        createFiservHeader(requestWrapper, request.getSubscriberID()), request);
        logger.debug("Post payments with request: {}, response: {}", request, response);
        return createRequestProxyWrapper(requestWrapper, response);
    }
    
    @Override
    public RequestProxyWrapper<PaymentByIdGetResponseBody> getPaymentById(
            RequestProxyWrapper<Void> requestWrapper, Exchange exchange, String id, String subscriberId) {
        PaymentByIdGetResponseBody response = paymentsService.getPaymentById(
                        createFiservHeader(requestWrapper, subscriberId), id);
        logger.debug("Get payment with id: {} for billpay user: {}, response: {}", id, subscriberId, response);
        return createRequestProxyWrapper(requestWrapper, response);
    }
    
    @Override
    public RequestProxyWrapper<PaymentByIdPutResponseBody> putPaymentById(
            RequestProxyWrapper<PaymentByIdPutRequestBody> requestWrapper, Exchange exchange, String id) {
        PaymentByIdPutRequestBody request = requestWrapper.getRequest().getData();
        PaymentByIdPutResponseBody response = paymentsService.putBillPayPayments(
                        createFiservHeader(requestWrapper, request.getSubscriberID()), id, request);
        logger.debug("Put single payment with id: {}, request: {}, response: {}", id, request, response);
        return createRequestProxyWrapper(requestWrapper, response);
    }
    
    @Override
    public RequestProxyWrapper<RecurringPaymentByIdGetResponseBody> getRecurringPaymentById(
            RequestProxyWrapper<Void> requestWrapper, Exchange exchange, String id, String subscriberId) {
        RecurringPaymentByIdGetResponseBody response = paymentsService.getRecurringPaymentById(
                        createFiservHeader(requestWrapper, subscriberId), id);
        logger.debug("Get recurring payment with id: {} for billpay user: {}, response: {}", 
                     id, subscriberId, response);
        return createRequestProxyWrapper(requestWrapper, response);
    }
    
    @Override
    public RequestProxyWrapper<BillPayRecurringPaymentsPostResponseBody> postBillPayRecurringPayments(
            RequestProxyWrapper<BillPayRecurringPaymentsPostRequestBody> requestWrapper, Exchange exchange) {
        BillPayRecurringPaymentsPostRequestBody request = requestWrapper.getRequest().getData();
        BillPayRecurringPaymentsPostResponseBody response = paymentsService.postBillPayRecurringPayments(
                        createFiservHeader(requestWrapper, request.getSubscriberID()), request);
        logger.debug("Post recurring payments with request: {}, response: {}", request, response);
        return createRequestProxyWrapper(requestWrapper, response);
    }

    @Override
    public RequestProxyWrapper<Void> deleteRecurringPaymentById(
            RequestProxyWrapper<Void> requestWrapper, Exchange exchange, String id, String subscriberId) {
        paymentsService.deleteRecurringPaymentById(createFiservHeader(requestWrapper, subscriberId), id);
        logger.debug("Delete recurring payment with id: {}", id);
        return requestWrapper;
    }
    
    @Override
    public RequestProxyWrapper<RecurringPaymentByIdPutResponseBody> putRecurringPaymentById(
            RequestProxyWrapper<RecurringPaymentByIdPutRequestBody> requestWrapper, Exchange exchange, String id) {
        RecurringPaymentByIdPutRequestBody request = requestWrapper.getRequest().getData();
        RecurringPaymentByIdPutResponseBody response = paymentsService.putBillPayRecurringPayments(
                        createFiservHeader(requestWrapper, request.getSubscriberID()), id, request);
        logger.debug("Put recurring payment with id: {}, request: {}, response: {}", id, request, response);
        return createRequestProxyWrapper(requestWrapper, response);
    }
}