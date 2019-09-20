package com.backbase.billpay.fiserv.payments;

import static com.backbase.billpay.fiserv.payees.PaymentServicesMapper.CURRENCY;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backbase.billpay.fiserv.payments.model.BankAccountId;
import com.backbase.billpay.fiserv.payments.model.Payee;
import com.backbase.billpay.fiserv.payments.model.PaymentAddRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentAddResponse;
import com.backbase.billpay.fiserv.payments.model.PaymentCancelRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentCancelResponse;
import com.backbase.billpay.fiserv.payments.model.PaymentDetail;
import com.backbase.billpay.fiserv.payments.model.PaymentDetailRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentDetailResponse;
import com.backbase.billpay.fiserv.payments.model.PaymentListRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentListResponse;
import com.backbase.billpay.fiserv.payments.model.PaymentModifyRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentModifyResponse;
import com.backbase.billpay.fiserv.payments.model.StandardAddPaymentDetail;
import com.backbase.billpay.fiserv.payments.model.BankAccountId.BankAccountType;
import com.backbase.billpay.fiserv.payments.model.PaymentDetail.PaymentStatus;
import com.backbase.billpay.fiserv.payments.recurring.model.ModelInfo;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModel;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelAddRequest;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelAddResponse;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelCancelRequest;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelCancelResponse;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelListRequest;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelListResponse;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelModifyRequest;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelModifyResponse;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelAddInfo.ModelFrequency;
import com.backbase.billpay.fiserv.utils.AbstractHTTPWebServiceTest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayRecurringPaymentsPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayRecurringPaymentsPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.Payment;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentAccount;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdPutResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentRequest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPayment;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdPutResponseBody;
import com.backbase.rest.spec.common.types.Currency;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Test;
import org.springframework.http.MediaType;

public class PaymentsControllerTest extends AbstractHTTPWebServiceTest {
    
    private static final String URL = "/service-api/v2/bill-pay/payments";
    private static final String RECURRING_ENDPOINT = "/recurring";
    private static final String ID_ENDPOINT = "/{id}";
    private static final String PAYMENT_ID = "1234";
    
    @Test
    public void deletePaymentsById() throws Exception {
        
        // create the mock response
        PaymentCancelResponse cancelResponse = PaymentCancelResponse.builder()
                                                                    .result(createSuccessResult())
                                                                    .build();
        
        // set up mock server to return the response
        setupWebServiceResponse(cancelResponse);
        
        // call the endpoint
        mockMvc.perform(delete(URL + ID_ENDPOINT, PAYMENT_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isNoContent());
        
        // validate the results
        PaymentCancelRequest cancelRequest = retrieveRequest(PaymentCancelRequest.class);
        assertEquals(PAYMENT_ID, String.valueOf(cancelRequest.getPaymentId()));
        assertHeader(SUBSCRIBER_ID, cancelRequest.getHeader());
    }
    
    @Test
    public void getPaymentById() throws Exception {
        
        // create the mock response
        PaymentDetailResponse fiservResponse = createPaymentDetailResponse();
        
        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(get(URL + ID_ENDPOINT, PAYMENT_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        PaymentByIdGetResponseBody response = objectMapper.readValue(stringResponse, PaymentByIdGetResponseBody.class);
        
        // validate the response
        PaymentDetail fiservPayment = fiservResponse.getPaymentDetailResult().get(0);
        assertEquals(fiservPayment.getAmount(), response.getPayment().getPaymentAmount());
        assertEquals(fiservPayment.getAmount(), response.getPayment().getAmount().getAmount());
        assertEquals(CURRENCY, response.getPayment().getAmount().getCurrencyCode());
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
    public void deleteRecurringPaymentsById() throws Exception {
        
        // create the mock response
        RecurringModelCancelResponse cancelResponse = RecurringModelCancelResponse.builder()
                                                                    .result(createSuccessResult())
                                                                    .build();
        
        // set up mock server to return the response
        setupWebServiceResponse(cancelResponse);
        
        // call the listener method
        mockMvc.perform(delete(URL + RECURRING_ENDPOINT + ID_ENDPOINT, PAYMENT_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isNoContent());
        
        // validate the results
        RecurringModelCancelRequest cancelRequest = retrieveRequest(RecurringModelCancelRequest.class);
        assertEquals(Boolean.TRUE, cancelRequest.getCancelPendingPayments());
        assertEquals(PAYMENT_ID, cancelRequest.getRecurringModelId());
        assertHeader(SUBSCRIBER_ID, cancelRequest.getHeader());
    }
    
    @Test
    public void postBillPayPayees() throws Exception {
        
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
                                                                          .withCurrencyCode(CURRENCY))
                                                          .withPayeeID(PAYEE_ID)
                                                          .withPaymentAccount(new PaymentAccount()
                                                                                  .withAccountNumber("1234")
                                                                                  .withAccountType("DDA")
                                                                                  .withRoutingNumber("4321"))
                                                          .withPaymentDate("2020-12-23")
                                                          .withPaymentMemo("memo1")));
                        
        String stringRequest = objectMapper.writeValueAsString(request);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(post(URL)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString();
        
        BillPayPaymentsPostResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayPaymentsPostResponseBody.class);
        
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
    public void putPaymentById() throws Exception {
        
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
                                                          .withCurrencyCode(CURRENCY))
                                          .withPayeeID(PAYEE_ID)
                                          .withPaymentAccount(new PaymentAccount()
                                                                  .withAccountNumber("1234")
                                                                  .withAccountType("DDA")
                                                                  .withRoutingNumber("4321"))
                                          .withPaymentDate("2020-12-23")
                                          .withPaymentMemo("memo1"));
                        
        String stringRequest = objectMapper.writeValueAsString(request);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(put(URL + ID_ENDPOINT, PAYMENT_ID)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        PaymentByIdPutResponseBody response = objectMapper.readValue(stringResponse, PaymentByIdPutResponseBody.class);
        
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
    public void getBillPayPayments() throws Exception {
        
        // create the mock response
        PaymentListResponse fiservResponse = createPaymentListResponse();
        
        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(get(URL)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID)
                        .param("status", "PENDING"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        BillPayPaymentsGetResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayPaymentsGetResponseBody.class);
        
        // validate the response
        assertEquals(Long.valueOf(1), response.getTotalCount());
        assertEquals(1, response.getPayments().size());
        com.backbase.billpay.fiserv.payments.model.Payment fiservPayment = fiservResponse.getPayments().get(0);
        Payment servicePayment = response.getPayments().get(0);
        assertEquals(fiservPayment.getAmount(), servicePayment.getPaymentAmount());
        assertEquals(fiservPayment.getAmount(), servicePayment.getAmount().getAmount());
        assertEquals(CURRENCY, servicePayment.getAmount().getCurrencyCode());
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
    
    @Test
    public void getRecurringPaymentById() throws Exception {
        
        // create the mock response
        RecurringModelListResponse fiservResponse = createRecurringPaymentResponse();
        
        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(get(URL + RECURRING_ENDPOINT + ID_ENDPOINT, PAYMENT_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        RecurringPaymentByIdGetResponseBody response =
                        objectMapper.readValue(stringResponse, RecurringPaymentByIdGetResponseBody.class);
        
        // validate the response
        assertEquals(PAYMENT_ID, response.getPayment().getId());
        assertEquals(PAYEE_ID, response.getPayment().getPayeeID());
        assertEquals(Boolean.TRUE, response.getPayment().getModelExpirationAlert());
        assertEquals(Boolean.TRUE, response.getPayment().getPaymentScheduledAlert());
        assertEquals(Boolean.TRUE, response.getPayment().getPaymentSentAlert());
        assertEquals(BigDecimal.TEN, response.getPayment().getPaymentAmount());
        assertEquals("EVERY_2_WEEKS", response.getPayment().getFrequency());
        assertEquals(Integer.valueOf("100"), response.getPayment().getNumberOfInstances());
        assertEquals("Payment memo", response.getPayment().getPaymentMemo());
        
        // validate the request
        RecurringModelListRequest request = retrieveRequest(RecurringModelListRequest.class);
        assertHeader(SUBSCRIBER_ID, request.getHeader());
    }

    private RecurringModelListResponse createRecurringPaymentResponse() {
        RecurringModel recurringModel = RecurringModel.builder()
                                                      .recurringModelId(PAYMENT_ID)
                                                      .modelExpirationAlert(true)
                                                      .paymentScheduledAlert(true)
                                                      .paymentSentAlert(true)
                                                      .payeeId(PAYEE_ID)
                                                      .recurringModelInfo(ModelInfo.builder()
                                                                              .recurringPaymentAmount(BigDecimal.TEN)
                                                                              .frequency(ModelFrequency.EVERY_2_WEEKS)
                                                                              .numberOfPayments(100)
                                                                              .memo("Payment memo")
                                                                              .build())
                                                      .fundingAccount(BankAccountId.builder()
                                                                         .accountNumber("1234")
                                                                         .accountType(BankAccountType.DDA)
                                                                         .routingTransitNumber("4321")
                                                                         .build())
                                                      .build();
        return RecurringModelListResponse.builder()
                        .recurringPayments(Arrays.asList(recurringModel))
                        .result(createSuccessResult())
                        .build();
    }
    
    @Test
    public void postBillPayRecurringPayments() throws Exception {
        
        // create the mock response
        RecurringModelAddResponse fiservResponse = 
                        RecurringModelAddResponse.builder()
                                          .result(createSuccessResult())
                                          .recurringModelId(PAYMENT_ID)
                                          .build();

        setupWebServiceResponse(fiservResponse);

        BillPayRecurringPaymentsPostRequestBody request = new BillPayRecurringPaymentsPostRequestBody()
                        .withSubscriberID(SUBSCRIBER_ID)
                        .withPayment(new RecurringPayment()
                                          .withAmount(new Currency()
                                                          .withAmount(BigDecimal.TEN)
                                                          .withCurrencyCode("USD"))
                                          .withPayeeID(PAYEE_ID)
                                          .withPaymentAccount(new PaymentAccount()
                                                                  .withAccountNumber("1234")
                                                                  .withAccountType("DDA")
                                                                  .withRoutingNumber("4321"))
                                          .withPaymentDate("2020-12-23")
                                          .withPaymentMemo("Payment memo")
                                          .withModelExpirationAlert(true)
                                          .withPaymentScheduledAlert(true)
                                          .withPaymentSentAlert(true)
                                          .withFrequency("WEEKLY")
                                          .withNumberOfInstances(10));
                        
        String stringRequest = objectMapper.writeValueAsString(request);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(post(URL + RECURRING_ENDPOINT)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString();
        
        BillPayRecurringPaymentsPostResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayRecurringPaymentsPostResponseBody.class);
        
        // validate the payment response
        assertEquals(PAYMENT_ID, response.getId());
        
        // validate the request
        RecurringModelAddRequest addRequest = retrieveRequest(RecurringModelAddRequest.class);
        assertEquals("1234", addRequest.getAccountId().getAccountNumber());
        assertEquals("DDA", addRequest.getAccountId().getAccountType().toString());
        assertEquals("4321", addRequest.getAccountId().getRoutingTransitNumber());
        assertEquals(Boolean.TRUE, addRequest.getModelExpirationAlert());
        assertEquals(Boolean.TRUE, addRequest.getPaymentScheduledAlert());
        assertEquals(Boolean.TRUE, addRequest.getPaymentSentAlert());
        assertEquals(PAYEE_ID, String.valueOf(addRequest.getPayeeId()));
        assertEquals(ModelFrequency.WEEKLY, addRequest.getModelInfo().getFrequency());
        assertEquals(new Integer(10), addRequest.getModelInfo().getNumberOfPayments());
        assertEquals(BigDecimal.TEN, addRequest.getModelInfo().getRecurringPaymentAmount());
        assertEquals("Payment memo", addRequest.getModelInfo().getMemo());
        assertHeader(SUBSCRIBER_ID, addRequest.getHeader());
    }
    
    @Test
    public void putRecurringPaymentById() throws Exception {
        // create the mock response
        RecurringModelModifyResponse fiservResponse = 
                        RecurringModelModifyResponse.builder()
                                          .result(createSuccessResult())
                                          .build();

        setupWebServiceResponse(fiservResponse);

        RecurringPaymentByIdPutRequestBody request = new RecurringPaymentByIdPutRequestBody()
                        .withSubscriberID(SUBSCRIBER_ID)
                        .withPayment(new RecurringPayment()
                                          .withAmount(new Currency()
                                                          .withAmount(BigDecimal.TEN)
                                                          .withCurrencyCode("USD"))
                                          .withPayeeID(PAYEE_ID)
                                          .withPaymentAccount(new PaymentAccount()
                                                                  .withAccountNumber("1234")
                                                                  .withAccountType("DDA")
                                                                  .withRoutingNumber("4321"))
                                          .withPaymentDate("2020-12-23")
                                          .withPaymentMemo("Payment memo")
                                          .withModelExpirationAlert(true)
                                          .withPaymentScheduledAlert(true)
                                          .withPaymentSentAlert(true)
                                          .withFrequency("WEEKLY")
                                          .withNumberOfInstances(10));
                        
        String stringRequest = objectMapper.writeValueAsString(request);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(put(URL + RECURRING_ENDPOINT + ID_ENDPOINT, PAYMENT_ID)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        RecurringPaymentByIdPutResponseBody response =
                        objectMapper.readValue(stringResponse, RecurringPaymentByIdPutResponseBody.class);
        
        // validate the payment response
        assertEquals(PAYMENT_ID, response.getId());
        
        // validate the request
        RecurringModelModifyRequest modifyRequest = retrieveRequest(RecurringModelModifyRequest.class);
        assertEquals("1234", modifyRequest.getAccountId().getAccountNumber());
        assertEquals("DDA", modifyRequest.getAccountId().getAccountType().toString());
        assertEquals("4321", modifyRequest.getAccountId().getRoutingTransitNumber());
        assertEquals(Boolean.TRUE, modifyRequest.getModelExpirationAlert());
        assertEquals(Boolean.TRUE, modifyRequest.getPaymentScheduledAlert());
        assertEquals(Boolean.TRUE, modifyRequest.getPaymentSentAlert());
        assertEquals(ModelFrequency.WEEKLY, modifyRequest.getModelInfo().getFrequency());
        assertEquals(new Integer(10), modifyRequest.getModelInfo().getNumberOfPayments());
        assertEquals(BigDecimal.TEN, modifyRequest.getModelInfo().getRecurringPaymentAmount());
        assertEquals("Payment memo", modifyRequest.getModelInfo().getMemo());
        assertHeader(SUBSCRIBER_ID, modifyRequest.getHeader());
    }

}
