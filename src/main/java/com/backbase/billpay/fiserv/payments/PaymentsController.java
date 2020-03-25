package com.backbase.billpay.fiserv.payments;

import static com.backbase.billpay.fiserv.utils.FiservUtils.fromLocalDate;

import com.backbase.billpay.fiserv.utils.AbstractController;
import com.backbase.billpay.integration.rest.spec.serviceapi.v2.billpay.payments.BillPayPaymentsApi;
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
import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Bill Pay payment operations.
 */
@RestController
public class PaymentsController extends AbstractController implements BillPayPaymentsApi {
    
    private static final String PAYMENT_TYPE = "paymentType";
    
    private PaymentsService paymentsService;

    @Autowired
    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @Override
    public BillPayPaymentsGetResponseBody getBillPayPayments(String subscriberId, String status, LocalDate startDate,
                    LocalDate endDate, String payeeId, Integer from, Integer size, String orderBy, String direction,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String paymentType = httpServletRequest.getParameter(PAYMENT_TYPE);
        BillPayPaymentsGetResponseBody response = 
                paymentsService.getBillPayPayments(fiservUtils.createHeader(httpServletRequest, subscriberId), 
                                                   status, fromLocalDate(startDate), fromLocalDate(endDate), payeeId, paymentType, from, size,
                                                   orderBy, direction);
        logger.debug("Get payments for subscriberId: {}, status: {}, startDate: {}, endDate: {}, payeeID:{}, "
                     + "paymentType: {}, from: {}, size: {}, found payments: {}", 
                     subscriberId, status, startDate, endDate, payeeId, paymentType, from, size, response);
        return response;
    }
    
    @Override
    public BillPayPaymentsPostResponseBody postBillPayPayments(
                    @Valid BillPayPaymentsPostRequestBody request,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        BillPayPaymentsPostResponseBody response = paymentsService.postBillPayPayments(
                        fiservUtils.createHeader(httpServletRequest, request.getSubscriberID()), request);
        logger.debug("Post payments with request: {}, response: {}", request, response);
        return response;
    }
    
    @Override
    public PaymentByIdGetResponseBody getPaymentById(String id, String subscriberId,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        PaymentByIdGetResponseBody response = paymentsService.getPaymentById(
                        fiservUtils.createHeader(httpServletRequest, subscriberId), id);
        logger.debug("Get payment with id: {} for billpay user: {}, response: {}", id, subscriberId, response);
        return response;
    }
    
    @Override
    public PaymentByIdPutResponseBody putPaymentById(@Valid PaymentByIdPutRequestBody request,
                    String id, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        PaymentByIdPutResponseBody response = paymentsService.putBillPayPayments(
                        fiservUtils.createHeader(httpServletRequest, request.getSubscriberID()), id, request);
        logger.debug("Put single payment with id: {}, request: {}, response: {}", id, request, response);
        return response;
    }
    
    @Override
    public void deletePaymentById(String id, String subscriberId, HttpServletRequest httpServletRequest,
                    HttpServletResponse httpServletResponse) {
        paymentsService.deletePaymentById(fiservUtils.createHeader(httpServletRequest, subscriberId), id);
        logger.debug("Delete payment with id: {}", id);
    }
    
    @Override
    public BillPayRecurringPaymentsPostResponseBody postBillPayRecurringPayments(
                    @Valid BillPayRecurringPaymentsPostRequestBody request,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        BillPayRecurringPaymentsPostResponseBody response = paymentsService.postBillPayRecurringPayments(
                        fiservUtils.createHeader(httpServletRequest, request.getSubscriberID()), request);
        logger.debug("Post recurring payments with request: {}, response: {}", request, response);
        return response;
    }
    
    @Override
    public RecurringPaymentByIdGetResponseBody getRecurringPaymentById(String id, String subscriberId,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        RecurringPaymentByIdGetResponseBody response = paymentsService.getRecurringPaymentById(
                        fiservUtils.createHeader(httpServletRequest, subscriberId), id);
        logger.debug("Get recurring payment with id: {} for billpay user: {}, response: {}", 
                     id, subscriberId, response);
        return response;
    }
    
    @Override
    public RecurringPaymentByIdPutResponseBody putRecurringPaymentById(
                    @Valid RecurringPaymentByIdPutRequestBody request, String id,
                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        RecurringPaymentByIdPutResponseBody response = paymentsService.putBillPayRecurringPayments(
                        fiservUtils.createHeader(httpServletRequest, request.getSubscriberID()), id, request);
        logger.debug("Put recurring payment with id: {}, request: {}, response: {}", id, request, response);
        return response;
    }
    
    @Override
    public void deleteRecurringPaymentById(String id, String subscriberId, HttpServletRequest httpServletRequest,
                    HttpServletResponse httpServletResponse) {
        paymentsService.deleteRecurringPaymentById(fiservUtils.createHeader(httpServletRequest, subscriberId), id);
        logger.debug("Delete recurring payment with id: {}", id);
    }
    
}
