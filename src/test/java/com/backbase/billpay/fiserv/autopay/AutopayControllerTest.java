package com.backbase.billpay.fiserv.autopay;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backbase.billpay.fiserv.autopay.model.AutoPayAddInfo;
import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo;
import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo.AutoPayAmount;
import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo.AutoPayDaysBefore;
import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo.AutoPayOn;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayAddRequest;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayAddResponse;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayCancelRequest;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayCancelResponse;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResponse;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResultInfo;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayModifyRequest;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayModifyResponse;
import com.backbase.billpay.fiserv.payments.model.BankAccountId;
import com.backbase.billpay.fiserv.payments.model.BankAccountId.BankAccountType;
import com.backbase.billpay.fiserv.utils.AbstractHTTPWebServiceTest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.accounts.Account;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Autopay;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.autopay.BillPayAutopayPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.autopay.BillPayAutopayPutResponseBody;
import com.backbase.rest.spec.common.types.Currency;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import org.springframework.http.MediaType;

public class AutopayControllerTest extends AbstractHTTPWebServiceTest {

    private static final String URL = "/service-api/v2/bill-pay/payees/electronic/{id}/autopay";
    private static final String FULL_AMOUNT = "FULL_AMOUNT";
    private static final String BILL_ARRIVAL = "BILL_ARRIVAL";
    private static final String CHECKING_ACCOUNT = "DDA";
    private static final String ACCOUNT_NUM = "accountNum";
    private static final String ROUTING_NUM = "routingNum";
    private static final String CURRENCY_CODE = "USD";
    private static final Currency ACCOUNT_BALANCE = new Currency().withAmount(BigDecimal.ONE)
                    .withCurrencyCode(CURRENCY_CODE);
    private static final Currency MAX_PAYMENT_AMOUNT = new Currency().withAmount(BigDecimal.TEN)
                    .withCurrencyCode(CURRENCY_CODE);
    private static final String NICK_NAME = "nickName";
    private static final String ACCOUNT_ID = "id";

    private Random random = new Random();

    @Test
    public void putBillPayAutopay_AddAutopay_MandatoryFields() throws Exception {
        // create the mock responses
        EbillAutoPayListResponse fiservListResponse = createfiservListResponse();
        EbillAutoPayAddResponse fiservAddResponse = EbillAutoPayAddResponse.builder()
                        .result(createSuccessResult())
                        .build();

        // set up mock server to return the responses
        setupWebServiceResponse(fiservListResponse);
        setupWebServiceResponse(fiservAddResponse);

        // create the request
        BillPayAutopayPutRequestBody request = createAutopayPutRequest();

        String stringRequest = objectMapper.writeValueAsString(request);

        // call the endpoint
        String stringResponse = mockMvc.perform(put(URL, PAYEE_ID)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andReturn().getResponse().getContentAsString();

        // validate the response data
        BillPayAutopayPutResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayAutopayPutResponseBody.class);
        assertEquals(PAYEE_ID, response.getId());

        // validate the request data
        EbillAutoPayAddRequest addRequest = retrieveSpecificRequest(1, EbillAutoPayAddRequest.class);
        assertHeader(SUBSCRIBER_ID, addRequest.getHeader());
        AutoPayAddInfo requestAutoPayInfo = addRequest.getAutopayInfo();
        assertEquals(PAYEE_ID, String.valueOf(requestAutoPayInfo.getPayeeId()));

        // account Info
        BankAccountId fiservAccount = requestAutoPayInfo.getBankAccountId();
        Account billpayAccount = request.getAutopay().getPaymentAccount();
        assertEquals(billpayAccount.getAccountNumber(), fiservAccount.getAccountNumber());
        assertEquals(BankAccountType.valueOf(request.getAutopay().getPaymentAccount().getAccountType()),
                        fiservAccount.getAccountType());
        assertEquals(billpayAccount.getRoutingNumber(), fiservAccount.getRoutingTransitNumber());

        // autopay Info
        AutoPayInfo fiservAutopay = requestAutoPayInfo.getAutoPay();
        assertEquals(AutoPayAmount.AMOUNT_DUE, fiservAutopay.getAutoPayAmount());
        assertEquals(AutoPayOn.UPON_RECEIPT, fiservAutopay.getAutoPayOn());
    }

    @Test
    public void putBillPayAutopay_AddAutopay_AllFields() throws Exception {
        // create the mock responses
        EbillAutoPayListResponse fiservListResponse = createfiservListResponse();
        EbillAutoPayAddResponse fiservAddResponse = EbillAutoPayAddResponse.builder()
                        .result(createSuccessResult())
                        .build();

        // set up mock server to return the responses
        setupWebServiceResponse(fiservListResponse);
        setupWebServiceResponse(fiservAddResponse);

        // create the request
        BillPayAutopayPutRequestBody request = createAutopayPutRequest();
        Autopay autopay = request.getAutopay();
        autopay.setDaysBeforePayOn(5);
        autopay.setMaxPaymentAmount(MAX_PAYMENT_AMOUNT);
        autopay.setPaymentSentAlert(random.nextBoolean());
        autopay.setPaymentScheduledAlert(random.nextBoolean());
        autopay.setPaymentCompleteAlert(random.nextBoolean());
        Account paymentAccount = autopay.getPaymentAccount();
        paymentAccount.setId(ACCOUNT_ID);
        paymentAccount.setAccountNickName(NICK_NAME);
        paymentAccount.setAccountBalance(ACCOUNT_BALANCE);

        String stringRequest = objectMapper.writeValueAsString(request);

        // call the endpoint
        String stringResponse = mockMvc.perform(put(URL, PAYEE_ID)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andReturn().getResponse().getContentAsString();

        // validate the response data
        BillPayAutopayPutResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayAutopayPutResponseBody.class);
        assertEquals(PAYEE_ID, response.getId());

        // validate the request data
        EbillAutoPayAddRequest addRequest = retrieveSpecificRequest(1, EbillAutoPayAddRequest.class);
        assertHeader(SUBSCRIBER_ID, addRequest.getHeader());
        AutoPayAddInfo requestAutoPayInfo = addRequest.getAutopayInfo();
        assertEquals(PAYEE_ID, String.valueOf(requestAutoPayInfo.getPayeeId()));

        // account Info
        BankAccountId fiservAccount = requestAutoPayInfo.getBankAccountId();
        Account billpayAccount = request.getAutopay().getPaymentAccount();
        assertEquals(billpayAccount.getAccountNumber(), fiservAccount.getAccountNumber());
        assertEquals(BankAccountType.valueOf(request.getAutopay().getPaymentAccount().getAccountType()),
                        fiservAccount.getAccountType());
        assertEquals(billpayAccount.getRoutingNumber(), fiservAccount.getRoutingTransitNumber());

        // autopay Info
        AutoPayInfo fiservAutopay = requestAutoPayInfo.getAutoPay();
        Autopay billpayAutopay = request.getAutopay();
        assertEquals(AutoPayAmount.AMOUNT_DUE, fiservAutopay.getAutoPayAmount());
        assertEquals(AutoPayOn.UPON_RECEIPT, fiservAutopay.getAutoPayOn());
        assertEquals(AutoPayDaysBefore.FIVE, fiservAutopay.getDaysBefore());
        assertEquals(billpayAutopay.getMaxPaymentAmount().getAmount(), fiservAutopay.getMaxAuthorizedAmount());
    }

    @Test
    public void putBillPayAutopay_ModifyAutopay_MandatoryFields() throws Exception {
        // create the mock responses
        EbillAutoPayListResponse fiservListResponse = createfiservListResponse_withPopulatedList();
        EbillAutoPayModifyResponse fiservModifyResponse = EbillAutoPayModifyResponse.builder()
                        .result(createSuccessResult())
                        .build();

        // set up mock server to return the responses
        setupWebServiceResponse(fiservListResponse);
        setupWebServiceResponse(fiservModifyResponse);

        // create the request
        BillPayAutopayPutRequestBody request = createAutopayPutRequest();

        String stringRequest = objectMapper.writeValueAsString(request);

        // call the endpoint
        String stringResponse = mockMvc.perform(put(URL, PAYEE_ID)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andReturn().getResponse().getContentAsString();

        // validate the response data
        BillPayAutopayPutResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayAutopayPutResponseBody.class);
        assertEquals(PAYEE_ID, response.getId());

        // validate the request data
        EbillAutoPayModifyRequest modifyRequest = retrieveSpecificRequest(1, EbillAutoPayModifyRequest.class);
        assertHeader(SUBSCRIBER_ID, modifyRequest.getHeader());
        AutoPayAddInfo requestAutoPayInfo = modifyRequest.getAutopayInfo();
        assertEquals(PAYEE_ID, String.valueOf(requestAutoPayInfo.getPayeeId()));

        // account Info
        BankAccountId fiservAccount = requestAutoPayInfo.getBankAccountId();
        Account billpayAccount = request.getAutopay().getPaymentAccount();
        assertEquals(billpayAccount.getAccountNumber(), fiservAccount.getAccountNumber());
        assertEquals(BankAccountType.valueOf(request.getAutopay().getPaymentAccount().getAccountType()),
                        fiservAccount.getAccountType());
        assertEquals(billpayAccount.getRoutingNumber(), fiservAccount.getRoutingTransitNumber());

        // autopay Info
        AutoPayInfo fiservAutopay = requestAutoPayInfo.getAutoPay();
        assertEquals(AutoPayAmount.AMOUNT_DUE, fiservAutopay.getAutoPayAmount());
        assertEquals(AutoPayOn.UPON_RECEIPT, fiservAutopay.getAutoPayOn());
    }

    @Test
    public void putBillPayAutopay_ModifyAutopay_AllFields() throws Exception {
        // create the mock responses
        EbillAutoPayListResponse fiservListResponse = createfiservListResponse_withPopulatedList();
        EbillAutoPayAddResponse fiservModifyResponse = EbillAutoPayAddResponse.builder()
                        .result(createSuccessResult())
                        .build();

        // set up mock server to return the responses
        setupWebServiceResponse(fiservListResponse);
        setupWebServiceResponse(fiservModifyResponse);

        // create the request
        BillPayAutopayPutRequestBody request = createAutopayPutRequest();
        Autopay autopay = request.getAutopay();
        autopay.setDaysBeforePayOn(5);
        autopay.setMaxPaymentAmount(MAX_PAYMENT_AMOUNT);
        autopay.setPaymentSentAlert(random.nextBoolean());
        autopay.setPaymentScheduledAlert(random.nextBoolean());
        autopay.setPaymentCompleteAlert(random.nextBoolean());
        Account paymentAccount = autopay.getPaymentAccount();
        paymentAccount.setId(ACCOUNT_ID);
        paymentAccount.setAccountNickName(NICK_NAME);
        paymentAccount.setAccountBalance(ACCOUNT_BALANCE);

        String stringRequest = objectMapper.writeValueAsString(request);

        // call the endpoint
        String stringResponse = mockMvc.perform(put(URL, PAYEE_ID)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andReturn().getResponse().getContentAsString();

        // validate the response data
        BillPayAutopayPutResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayAutopayPutResponseBody.class);
        assertEquals(PAYEE_ID, response.getId());

        // validate the request data
        EbillAutoPayModifyRequest addRequest = retrieveSpecificRequest(1, EbillAutoPayModifyRequest.class);
        assertHeader(SUBSCRIBER_ID, addRequest.getHeader());
        AutoPayAddInfo requestAutoPayInfo = addRequest.getAutopayInfo();
        assertEquals(PAYEE_ID, String.valueOf(requestAutoPayInfo.getPayeeId()));

        // account Info
        BankAccountId fiservAccount = requestAutoPayInfo.getBankAccountId();
        Account billpayAccount = request.getAutopay().getPaymentAccount();
        assertEquals(billpayAccount.getAccountNumber(), fiservAccount.getAccountNumber());
        assertEquals(BankAccountType.valueOf(request.getAutopay().getPaymentAccount().getAccountType()),
                        fiservAccount.getAccountType());
        assertEquals(billpayAccount.getRoutingNumber(), fiservAccount.getRoutingTransitNumber());

        // autopay Info
        AutoPayInfo fiservAutopay = requestAutoPayInfo.getAutoPay();
        Autopay billpayAutopay = request.getAutopay();
        assertEquals(AutoPayAmount.AMOUNT_DUE, fiservAutopay.getAutoPayAmount());
        assertEquals(AutoPayOn.UPON_RECEIPT, fiservAutopay.getAutoPayOn());
        assertEquals(AutoPayDaysBefore.FIVE, fiservAutopay.getDaysBefore());
        assertEquals(billpayAutopay.getMaxPaymentAmount().getAmount(), fiservAutopay.getMaxAuthorizedAmount());
    }

    @Test
    public void deleteBillPayAutopay() throws Exception {
        // create the mock response
        EbillAutoPayCancelResponse fiservResponse =
                        EbillAutoPayCancelResponse.builder()
                                        .result(createSuccessResult())
                                        .build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // call the endpoint
        mockMvc.perform(delete(URL, PAYEE_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isNoContent());

        // validate the request data
        EbillAutoPayCancelRequest cancelRequest = retrieveRequest(EbillAutoPayCancelRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(cancelRequest.getPayeeId()));

        // validate the request header
        assertHeader(SUBSCRIBER_ID, cancelRequest.getHeader());
    }

    private EbillAutoPayListResponse createfiservListResponse() {
        return EbillAutoPayListResponse.builder()
                        .result(createSuccessResult())
                        .build();
    }

    private EbillAutoPayListResponse createfiservListResponse_withPopulatedList() {
        EbillAutoPayListResultInfo ebillAutoPayListResultInfo = new EbillAutoPayListResultInfo();
        ebillAutoPayListResultInfo.setPayeeId(Long.valueOf(PAYEE_ID));

        return EbillAutoPayListResponse.builder()
                        .ebillAutoPayList(Arrays.asList(ebillAutoPayListResultInfo))
                        .result(createSuccessResult())
                        .build();
    }

    private BillPayAutopayPutRequestBody createAutopayPutRequest() {
        return new BillPayAutopayPutRequestBody()
                        .withSubscriberID(SUBSCRIBER_ID)
                        .withAutopay(new Autopay()
                                        .withPayAmount(FULL_AMOUNT)
                                        .withPayOn(BILL_ARRIVAL)
                                        .withPaymentAccount(new Account()
                                                        .withAccountNumber(ACCOUNT_NUM)
                                                        .withAccountType(CHECKING_ACCOUNT)
                                                        .withRoutingNumber(ROUTING_NUM)));
    }
}