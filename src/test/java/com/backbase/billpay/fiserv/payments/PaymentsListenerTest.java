package com.backbase.billpay.fiserv.payments;

import static org.junit.Assert.assertEquals;

import com.backbase.billpay.fiserv.payments.model.BankAccountId;
import com.backbase.billpay.fiserv.payments.model.BankAccountId.BankAccountType;
import com.backbase.billpay.fiserv.payments.model.Payee;
import com.backbase.billpay.fiserv.payments.model.PaymentAddRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentAddResponse;
import com.backbase.billpay.fiserv.payments.model.PaymentCancelRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentCancelResponse;
import com.backbase.billpay.fiserv.payments.model.PaymentDetail;
import com.backbase.billpay.fiserv.payments.model.PaymentDetail.PaymentStatus;
import com.backbase.billpay.fiserv.payments.model.PaymentDetailRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentDetailResponse;
import com.backbase.billpay.fiserv.payments.model.PaymentListRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentListResponse;
import com.backbase.billpay.fiserv.payments.model.PaymentModifyRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentModifyResponse;
import com.backbase.billpay.fiserv.payments.model.StandardAddPaymentDetail;
import com.backbase.billpay.fiserv.utils.AbstractWebServiceTest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.Payment;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentAccount;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdPutResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentRequest;
import com.backbase.rest.spec.common.types.Currency;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PaymentsListenerTest extends AbstractWebServiceTest {
    
    private static final String PAYMENT_ID = "1234";
    
    @Autowired
    private PaymentsListener listener;
    
    @Test
    public void deletePaymentsById() {
        
        // create the mock response
        PaymentCancelResponse cancelResponse = PaymentCancelResponse.builder()
                                                                    .result(createSuccessResult())
                                                                    .build();
        
        // set up mock server to return the response
        setupWebServiceResponse(cancelResponse);
        
        // delete the payment
        listener.deletePaymentById(createRequestWrapper(null), null, PAYEE_ID, SUBSCRIBER_ID);
        
        // validate the results
        PaymentCancelRequest cancelRequest = retrieveRequest(PaymentCancelRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(cancelRequest.getPaymentId()));
        assertHeader(SUBSCRIBER_ID, cancelRequest.getHeader());
    }
    
    @Test
    public void getPaymentById() {
        
        // create the mock response
        PaymentDetailResponse fiservResponse = createPaymentDetailResponse();
        
        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);
        
        // retrieve the payment
        PaymentByIdGetResponseBody response = listener.getPaymentById(createRequestWrapper(null), null, PAYMENT_ID, SUBSCRIBER_ID)
                                                        .getRequest().getData();
        
        // validate the response
        PaymentDetail fiservPayment = fiservResponse.getPaymentDetailResult().get(0);
        assertEquals(fiservPayment.getAmount(), response.getPayment().getPaymentAmount());
        assertEquals(fiservPayment.getAmount(), response.getPayment().getAmount().getAmount());
        assertEquals("USD", response.getPayment().getAmount().getCurrencyCode());
        assertEquals(fiservPayment.getEbillId(), response.getPayment().getEbillID());
        assertEquals(fiservPayment.getPaymentId(), response.getPayment().getId());
        assertEquals(fiservPayment.getPayee().getAccountNumber(), response.getPayment().getPayeeAccountNumber());
        assertEquals(fiservPayment.getPayee().getPayeeId().toString(), response.getPayment().getPayeeID());
        assertEquals(fiservPayment.getPayee().getName(), response.getPayment().getPayeeName());
        assertEquals(fiservPayment.getBankAccountId().getAccountNumber(), 
                     response.getPayment().getPaymentAccount().getAccountNumber());
        assertEquals(fiservPayment.getBankAccountId().getAccountType().toString(), 
                     response.getPayment().getPaymentAccount().getAccountType());
        assertEquals(fiservPayment.getBankAccountId().getRoutingTransitNumber(), 
                     response.getPayment().getPaymentAccount().getRoutingNumber());
        assertEquals(fiservPayment.getMemo(), response.getPayment().getPaymentMemo());
        assertEquals(fiservPayment.getRecurringModelPayment(), response.getPayment().getRecurring());
        assertEquals("PENDING", response.getPayment().getStatus());
        
        // validate the request
        PaymentDetailRequest request = retrieveRequest(PaymentDetailRequest.class);
        assertEquals(PAYMENT_ID, String.valueOf(request.getPaymentId()));
        assertHeader(SUBSCRIBER_ID, request.getHeader());
    }
    
    private PaymentDetailResponse createPaymentDetailResponse() {
        PaymentDetail payment = PaymentDetail.builder()
                                             .amount(BigDecimal.ONE)
                                             .ebillId("1")
                                             .payee(Payee.builder()
                                                         .accountNumber("acc1")
                                                         .name("name")
                                                         .payeeId(Long.valueOf(PAYEE_ID))
                                                         .build())
                                             .memo("memo")
                                             .recurringModelPayment(false)
                                             .status(PaymentStatus.PENDING)
                                             .paymentDate(createBldrDate("2020-12-25"))
                                             .bankAccountId(BankAccountId.builder()
                                                                         .accountNumber("1234")
                                                                         .accountType(BankAccountType.DDA)
                                                                         .routingTransitNumber("4321")
                                                                         .build())
                                             .build();
        PaymentDetailResponse response = PaymentDetailResponse.builder()
                                                              .result(createSuccessResult())
                                                              .paymentDetailResult(Arrays.asList(payment))
                                                              .build();
        return response;
    }
    
    @Test
    public void deleteRecurringPaymentsById() {
        
        // create the mock response
        PaymentCancelResponse cancelResponse = PaymentCancelResponse.builder()
                                                                    .result(createSuccessResult())
                                                                    .build();
        
        // set up mock server to return the response
        setupWebServiceResponse(cancelResponse);
        
        // delete the payment
        listener.deleteRecurringPaymentById(createRequestWrapper(null), null, PAYEE_ID, SUBSCRIBER_ID);
        
        // validate the results
        PaymentCancelRequest cancelRequest = retrieveRequest(PaymentCancelRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(cancelRequest.getPaymentId()));
        assertHeader(SUBSCRIBER_ID, cancelRequest.getHeader());
    }
    
    @Test
    public void postBillPayPayees() {
        
        // create the mock response
        PaymentAddResponse fiservResponse = 
                        PaymentAddResponse.builder()
                                          .payments(Arrays.asList(StandardAddPaymentDetail.builder()
                                                                                          .paymentId(PAYMENT_ID)
                                                                                          .build()))
                                          .result(createSuccessResult())
                                          .build();

        setupWebServiceResponse(fiservResponse);

        BillPayPaymentsPostRequestBody request = new BillPayPaymentsPostRequestBody()
                        .withSubscriberID(SUBSCRIBER_ID)
                        .withPayments(Arrays.asList(new PaymentRequest()
                                                          .withEbillID("ebill1")
                                                          .withAmount(new Currency()
                                                                          .withAmount(BigDecimal.TEN)
                                                                          .withCurrencyCode("USD"))
                                                          .withPayeeID(PAYEE_ID)
                                                          .withPaymentAccount(new PaymentAccount()
                                                                                  .withAccountNumber("1234")
                                                                                  .withAccountType("DDA")
                                                                                  .withRoutingNumber("4321"))
                                                          .withPaymentDate("2020-12-23")
                                                          .withPaymentMemo("memo1")));
                        
        // add a payment
        BillPayPaymentsPostResponseBody response =
                        listener.postBillPayPayments(createRequestWrapper(request), null).getRequest().getData();
        
        // validate the payment response
        assertEquals(PAYMENT_ID, response.getPayments().get(0).getPaymentID());
        
        // validate the request
        PaymentAddRequest addRequest = retrieveRequest(PaymentAddRequest.class);
        assertEquals("ebill1", addRequest.getPaymentList().get(0).getEbillId());
        assertEquals("1234", addRequest.getPaymentList().get(0).getBankAccountId().getAccountNumber());
        assertEquals("DDA", addRequest.getPaymentList().get(0).getBankAccountId().getAccountType().toString());
        assertEquals("4321", addRequest.getPaymentList().get(0).getBankAccountId().getRoutingTransitNumber());
        assertEquals(Long.valueOf(PAYEE_ID), addRequest.getPaymentList().get(0).getPayeeId());
        assertEquals(BigDecimal.TEN, addRequest.getPaymentList().get(0).getPaymentAmount());
        assertEquals("memo1", addRequest.getPaymentList().get(0).getPaymentMemo());
        assertEquals("2020-12-23", addRequest.getPaymentList().get(0).getPaymentDate().getDate());
        assertHeader(SUBSCRIBER_ID, addRequest.getHeader());
    }
    
    @Test
    public void putPaymentById() {
        
        // create the mock response
        PaymentModifyResponse fiservResponse = 
                        PaymentModifyResponse.builder()
                                             .paymentId(PAYMENT_ID)
                                             .result(createSuccessResult())
                                             .build();

        setupWebServiceResponse(fiservResponse);

        PaymentByIdPutRequestBody request = new PaymentByIdPutRequestBody()
                        .withSubscriberID(SUBSCRIBER_ID)
                        .withPayment(new PaymentRequest()
                                          .withEbillID("ebill1")
                                          .withAmount(new Currency()
                                                          .withAmount(BigDecimal.TEN)
                                                          .withCurrencyCode("USD"))
                                          .withPayeeID(PAYEE_ID)
                                          .withPaymentAccount(new PaymentAccount()
                                                                  .withAccountNumber("1234")
                                                                  .withAccountType("DDA")
                                                                  .withRoutingNumber("4321"))
                                          .withPaymentDate("2020-12-23")
                                          .withPaymentMemo("memo1"));
                        
        // modify a payment
        PaymentByIdPutResponseBody response =
                        listener.putPaymentById(createRequestWrapper(request), null, PAYMENT_ID)
                            .getRequest().getData();
        
        // validate the payment response
        assertEquals(PAYMENT_ID, response.getId());
        
        // validate the request
        PaymentModifyRequest modifyRequest = retrieveRequest(PaymentModifyRequest.class);
        assertEquals("ebill1", modifyRequest.getEbillId());
        assertEquals("1234", modifyRequest.getBankAccountId().getAccountNumber());
        assertEquals("DDA", modifyRequest.getBankAccountId().getAccountType().toString());
        assertEquals("4321", modifyRequest.getBankAccountId().getRoutingTransitNumber());
        assertEquals(PAYMENT_ID, modifyRequest.getPaymentId());
        assertEquals(BigDecimal.TEN, modifyRequest.getPaymentAmount());
        assertEquals("memo1", modifyRequest.getPaymentMemo());
        assertEquals("2020-12-23", modifyRequest.getPaymentDate().getDate());
        assertHeader(SUBSCRIBER_ID, modifyRequest.getHeader());
    }
    
    @Test
    public void getBillPayPayments() {
        
        // create the mock response
        PaymentListResponse fiservResponse = createPaymentListResponse();
        
        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);
        
        // retrieve the payment
        BillPayPaymentsGetResponseBody response = listener.getBillPayPayments(
                        createRequestWrapper(null), null, SUBSCRIBER_ID, "PENDING", null, null, null, null, null)
                    .getRequest().getData();
        
        // validate the response
        com.backbase.billpay.fiserv.payments.model.Payment fiservPayment = fiservResponse.getPayments().get(0);
        Payment servicePayment = response.getPayments().get(0);
        assertEquals(fiservPayment.getAmount(), servicePayment.getPaymentAmount());
        assertEquals(fiservPayment.getAmount(), servicePayment.getAmount().getAmount());
        assertEquals("USD", servicePayment.getAmount().getCurrencyCode());
        assertEquals(fiservPayment.getEbillId(), servicePayment.getEbillID());
        assertEquals(fiservPayment.getPaymentId(), servicePayment.getId());
        assertEquals(fiservPayment.getPayee().getAccountNumber(), servicePayment.getPayeeAccountNumber());
        assertEquals(fiservPayment.getPayee().getPayeeId().toString(), servicePayment.getPayeeID());
        assertEquals(fiservPayment.getPayee().getName(), servicePayment.getPayeeName());
        assertEquals(fiservPayment.getBankAccountId().getAccountNumber(), 
                        servicePayment.getPaymentAccount().getAccountNumber());
        assertEquals(fiservPayment.getBankAccountId().getAccountType().toString(), 
                        servicePayment.getPaymentAccount().getAccountType());
        assertEquals(fiservPayment.getBankAccountId().getRoutingTransitNumber(), 
                        servicePayment.getPaymentAccount().getRoutingNumber());
        assertEquals(fiservPayment.getMemo(), servicePayment.getPaymentMemo());
        assertEquals(fiservPayment.getRecurringModelPayment(), servicePayment.getRecurring());
        assertEquals("PENDING", servicePayment.getStatus());
        
        // validate the request
        PaymentListRequest request = retrieveRequest(PaymentListRequest.class);
        assertEquals("Pending", request.getFilter().getStatusFilter().toString());
        assertEquals(Integer.toString(PaymentsServiceImpl.NEGATIVE_MAX_DAYS), 
                     String.valueOf(request.getFilter().getNumberOfDays()));
//        assertEquals(FiservUtils.todayFiservDate(), String.valueOf(request.getFilter().getStartingPaymentDate()));
        assertHeader(SUBSCRIBER_ID, request.getHeader());
    }
    
    private PaymentListResponse createPaymentListResponse() {
        com.backbase.billpay.fiserv.payments.model.Payment payment = 
        com.backbase.billpay.fiserv.payments.model.Payment.builder()
                                             .amount(BigDecimal.ONE)
                                             .ebillId("1")
                                             .payee(Payee.builder()
                                                         .accountNumber("acc1")
                                                         .name("name")
                                                         .payeeId(Long.valueOf(PAYEE_ID))
                                                         .build())
                                             .memo("memo")
                                             .recurringModelPayment(false)
                                             .status(PaymentStatus.PENDING)
                                             .paymentDate(createBldrDate("2020-12-25"))
                                             .bankAccountId(BankAccountId.builder()
                                                                         .accountNumber("1234")
                                                                         .accountType(BankAccountType.DDA)
                                                                         .routingTransitNumber("4321")
                                                                         .build())
                                             .build();
        return PaymentListResponse.builder()
                                  .payments(Arrays.asList(payment))
                                  .result(createSuccessResult())
                                  .build();
    }
}