package com.backbase.billpay.fiserv.payees;

import static com.backbase.billpay.fiserv.payees.PaymentServicesMapper.CURRENCY;
import static com.backbase.billpay.fiserv.utils.FiservUtils.toZonedDateTime;
import static com.backbase.billpay.fiserv.utils.FiservUtils.todayFiservDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo;
import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo.AutoPayAmount;
import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo.AutoPayDaysBefore;
import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo.AutoPayOn;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListRequest;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResponse;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResultInfo;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payees.model.PayeeAddInfo;
import com.backbase.billpay.fiserv.payees.model.PayeeAddRequest;
import com.backbase.billpay.fiserv.payees.model.PayeeAddResponse;
import com.backbase.billpay.fiserv.payees.model.PayeeCancelRequest;
import com.backbase.billpay.fiserv.payees.model.PayeeCancelResponse;
import com.backbase.billpay.fiserv.payees.model.PayeeListRequest;
import com.backbase.billpay.fiserv.payees.model.PayeeListResponse;
import com.backbase.billpay.fiserv.payees.model.PayeeModifyRequest;
import com.backbase.billpay.fiserv.payees.model.PayeeModifyResponse;
import com.backbase.billpay.fiserv.payees.model.PayeeSummary;
import com.backbase.billpay.fiserv.payees.model.PayeeSummary.EbillActivationStatusServiceType;
import com.backbase.billpay.fiserv.payees.model.PayeeSummary.EbillAutopayStatusType;
import com.backbase.billpay.fiserv.payees.model.PaymentServices;
import com.backbase.billpay.fiserv.payees.model.PaymentServices.PaymentServiceType;
import com.backbase.billpay.fiserv.payees.model.UsAddress;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill.BillType;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill.EbillStatus;
import com.backbase.billpay.fiserv.payeessummary.model.EbillFilter;
import com.backbase.billpay.fiserv.payeessummary.model.EbillFilter.BillTypeFilter;
import com.backbase.billpay.fiserv.payeessummary.model.EbillListRequest;
import com.backbase.billpay.fiserv.payeessummary.model.EbillListResponse;
import com.backbase.billpay.fiserv.payments.model.BankAccountId;
import com.backbase.billpay.fiserv.payments.model.BankAccountId.BankAccountType;
import com.backbase.billpay.fiserv.utils.AbstractWebServiceTest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Address;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Autopay;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayElectronicPayeesPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayElectronicPayeesPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayPayeesPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayPayeesPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdPutResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.LatestBill;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Payee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdPutResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PaymentService;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PostElectronicRequestPayee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PostRequestPayee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PutElectronicRequestPayee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PutRequestPayee;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import org.springframework.http.MediaType;

public class PayeesControllerTest extends AbstractWebServiceTest {

    private static final ZoneId EST = ZoneId.of("America/New_York");;
    private static final String URL = "/service-api/v2/bill-pay/payees";
    private static final String ELECTRONIC_ENDPOINT = "/electronic";
    private static final String ID_ENDPOINT = "/{id}";
    private static final Boolean CANCEL_PENDING_PAYMENTS = true;

    private Random random = new Random();

    @Test
    public void deletePayeeById() throws Exception {
        
        // create the mock response
        PayeeCancelResponse fiservResponse = PayeeCancelResponse.builder()
                                                                .result(createSuccessResult())
                                                                .build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);
        
       // call the endpoint
        mockMvc.perform(delete(URL + ID_ENDPOINT, PAYEE_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID)
                        .param("cancelPendingPayments", CANCEL_PENDING_PAYMENTS.toString()))
                        .andExpect(status().isNoContent());
        
        // validate the request data
        PayeeCancelRequest cancelRequest = retrieveRequest(PayeeCancelRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(cancelRequest.getPayeeId()));
        assertEquals(CANCEL_PENDING_PAYMENTS, cancelRequest.getCancelPayments());
        
        // validate the request header
        assertHeader(SUBSCRIBER_ID, cancelRequest.getHeader());
    }
    
    @Test
    public void postBillPayPayees() throws Exception {
        
        // create the mock response
        PayeeAddResponse fiservResponse = PayeeAddResponse.builder()
                                                          .payeeResultInfo(PayeeSummary.builder()
                                                                                       .payeeId(Long.valueOf(348596))
                                                                                       .build())
                                                          .result(createSuccessResult())
                                                          .build();

        setupWebServiceResponse(fiservResponse);

        BillPayPayeesPostRequestBody request = new BillPayPayeesPostRequestBody()
                        .withPayee(new PostRequestPayee()
                                        .withAccountNumber("accountNumber")
                                        .withAddress(new Address()
                                                        .withAddress1("address1")
                                                        .withAddress2("address2")
                                                        .withCity("city")
                                                        .withPostalCode("PST CDE")
                                                        .withState("ST"))
                                        .withName("payeeName")
                                        .withNickName("payeNickname")
                                        .withPhoneNumber("phoneNumber"))
                        .withSubscriberID(SUBSCRIBER_ID);
        
        String stringRequest = objectMapper.writeValueAsString(request);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(post(URL)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString();
        
        BillPayPayeesPostResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayPayeesPostResponseBody.class);
        assertEquals(fiservResponse.getPayeeResultInfo().getPayeeId(), Long.valueOf(response.getId()));
        
        PayeeAddRequest addRequest = retrieveRequest(PayeeAddRequest.class);
        
        // validate the request header
        assertHeader(SUBSCRIBER_ID, addRequest.getHeader());
        
        PayeeAddInfo addInfo = addRequest.getPayeeAddInfo();
        assertEquals(request.getPayee().getAccountNumber(), addInfo.getAccountNumber());
        assertEquals(request.getPayee().getName(), addInfo.getName());
        assertEquals(request.getPayee().getNickName(), addInfo.getNickName());
        assertEquals(request.getPayee().getPhoneNumber(), addInfo.getPhoneNumber());
        assertNull(addInfo.getMerchant());
        assertNull(addInfo.getOvernightAddress());
        assertFalse(addInfo.getAddressOnFile());
        assertFalse(addInfo.getIsImageCapture());
        
        UsAddress usAddress = addInfo.getAddress();
        assertEquals(request.getPayee().getAddress().getAddress1(), usAddress.getAddress1());
        assertEquals(request.getPayee().getAddress().getAddress2(), usAddress.getAddress2());
        assertEquals(request.getPayee().getAddress().getCity(), usAddress.getCity());
        assertEquals(request.getPayee().getAddress().getState(), usAddress.getState());
        assertEquals(request.getPayee().getAddress().getPostalCode(), usAddress.getZip5());
    }
    
    @Test
    public void getBillPayPayeeById() throws Exception {
        // create the mock response
        PayeeListResponse listResponse = createPayeeListResponse();
        
        // set up mock server to return the response
        setupWebServiceResponse(listResponse);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(get(URL + ID_ENDPOINT, PAYEE_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        PayeeByIdGetResponseBody response =
                        objectMapper.readValue(stringResponse, PayeeByIdGetResponseBody.class);
        
        // validate the response data
        Payee payee = response.getPayee();
        PayeeSummary payeeSummary = listResponse.getPayees().get(0);
        assertEquals(payeeSummary.getPayeeId(), Long.valueOf(payee.getId()));
        assertEquals(payeeSummary.getName(), payee.getName());
        assertEquals(payeeSummary.getNickName(), payee.getNickName());
        assertEquals(payeeSummary.getPhoneNumber(), payee.getPhoneNumber());
        assertEquals(payeeSummary.getAccountNumber(), payee.getAccountNumber());
        
        Address address = payee.getAddress();
        UsAddress usAddress = payeeSummary.getAddress();
        assertEquals(usAddress.getAddress1(), address.getAddress1());
        assertEquals(usAddress.getAddress2(), address.getAddress2());
        assertEquals(usAddress.getCity(), address.getCity());
        assertEquals(usAddress.getState(), address.getState());
        assertEquals(usAddress.getZip5(), address.getPostalCode());
        
        Address overnightDeliveryAddress = payee.getOvernightDeliveryAddress();
        UsAddress deliveryAddress = payeeSummary.getOverNightAddress();
        assertEquals(deliveryAddress.getAddress1(), overnightDeliveryAddress.getAddress1());
        assertEquals(deliveryAddress.getAddress2(), overnightDeliveryAddress.getAddress2());
        assertEquals(deliveryAddress.getCity(), overnightDeliveryAddress.getCity());
        assertEquals(deliveryAddress.getState(), overnightDeliveryAddress.getState());
        assertEquals(deliveryAddress.getZip5(), overnightDeliveryAddress.getPostalCode());
        
        List<PaymentService> paymentServices = payee.getPaymentServices();
        PaymentService regularPaymentService = paymentServices.get(0);
        assertEquals(toZonedDateTime(payeeSummary.getCutoffTime()), regularPaymentService.getCutoffTime()
            .withZoneSameInstant(EST));
        assertEquals("REGULAR_PAYMENT", regularPaymentService.getPaymentServiceType());
        assertEquals(payeeSummary.getLeadDays(), regularPaymentService.getDeliveryDays());
        assertNull(regularPaymentService.getFee());
        
        PaymentService overnightCheckPaymentService = paymentServices.get(1);
        PaymentServices paymentServicesOvernightCheck = payeeSummary.getPaymentServices().get(0);
        assertEquals(toZonedDateTime(paymentServicesOvernightCheck.getCutOffTime()), overnightCheckPaymentService
            .getCutoffTime().withZoneSameInstant(EST));
        assertEquals("OVERNIGHT_CHECK", overnightCheckPaymentService.getPaymentServiceType());
        assertEquals(payeeSummary.getLeadDays(), overnightCheckPaymentService.getDeliveryDays());
        assertEquals(paymentServicesOvernightCheck.getFee(), overnightCheckPaymentService.getFee().getAmount());
        assertEquals(CURRENCY, overnightCheckPaymentService.getFee().getCurrencyCode());
        
        PaymentService expeditedPaymentService = paymentServices.get(2);
        PaymentServices paymentServicesExpeditedPayment = payeeSummary.getPaymentServices().get(1);
        assertEquals(toZonedDateTime(paymentServicesExpeditedPayment.getCutOffTime()), expeditedPaymentService.
            getCutoffTime().withZoneSameInstant(EST));
        assertEquals("EXPEDITED_PAYMENT", expeditedPaymentService.getPaymentServiceType());
        assertEquals(payeeSummary.getLeadDays(), expeditedPaymentService.getDeliveryDays());
        assertEquals(paymentServicesExpeditedPayment.getFee(), expeditedPaymentService.getFee().getAmount());
        assertEquals(CURRENCY, expeditedPaymentService.getFee().getCurrencyCode());
        
        // validate the request data
        PayeeListRequest listRequest = retrieveRequest(PayeeListRequest.class);
        
        // validate the request header
        assertHeader(SUBSCRIBER_ID, listRequest.getHeader());
    }
    
    @Test
    public void getPayeeById_ShouldReturn404() throws Exception {
        // create the mock response
        PayeeSummary payeeSummary = PayeeSummary.builder()
                                                .payeeId(Long.valueOf(654231))
                                                .build();
        PayeeListResponse listResponse = PayeeListResponse.builder()
                                                          .result(createSuccessResult())
                                                          .payees(Arrays.asList(payeeSummary))
                                                          .build();
        
        // set up mock server to return the response
        setupWebServiceResponse(listResponse);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(get(URL + ID_ENDPOINT, PAYEE_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isNotFound())
                        .andReturn().getResponse().getContentAsString();
        
        // validate the response data
        NotFoundException exception =
                        objectMapper.readValue(stringResponse, NotFoundException.class);
        assertEquals("Resource not found", exception.getMessage());
        
        // validate the request data
        PayeeListRequest listRequest = retrieveRequest(PayeeListRequest.class);
        assertEquals(SUBSCRIBER_ID, listRequest.getHeader().getSubscriberId());
    }
    
    @Test
    public void putBillPayPayeesById() throws Exception {
        // create the mock response
        PayeeModifyResponse modifyResponse = 
                        PayeeModifyResponse.builder()
                                           .result(createSuccessResult())
                                           .payeeResultInfo(PayeeSummary.builder()
                                                                        .payeeId(Long.valueOf(486786))
                                                                        .build())
                                           .build();
        
        // set up mock server to return the response
        setupWebServiceResponse(modifyResponse);

        // create the request body
        Address address = new Address()
                        .withAddress1("address1")
                        .withAddress2("address2")
                        .withCity("city")
                        .withPostalCode("PST CDE")
                        .withState("ST");
        PutRequestPayee payee = new PutRequestPayee()
                        .withAccountNumber("accountNumber")
                        .withAddress(address)
                        .withName("payeeName")
                        .withNickName("payeNickname")
                        .withPhoneNumber("phoneNumber")
                        .withModifyPendingPayments(true);
        PayeeByIdPutRequestBody request = new PayeeByIdPutRequestBody()
                        .withPayee(payee)
                        .withSubscriberID(SUBSCRIBER_ID);
        
        String stringRequest = objectMapper.writeValueAsString(request);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(put(URL + ID_ENDPOINT, PAYEE_ID)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        PayeeByIdPutResponseBody response = objectMapper.readValue(stringResponse, PayeeByIdPutResponseBody.class);
        assertEquals(modifyResponse.getPayeeResultInfo().getPayeeId(), Long.valueOf(response.getId()));
        
        // validate the request data
        PayeeModifyRequest modifyRequest = retrieveRequest(PayeeModifyRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(modifyRequest.getPayeeId()));
        assertEquals(request.getPayee().getModifyPendingPayments(), modifyRequest.getModifyPendingPayments());
        
        PayeeAddInfo addInfo = modifyRequest.getPayeeAddInfo();
        assertEquals(payee.getAccountNumber(), addInfo.getAccountNumber());
        assertEquals(payee.getName(), addInfo.getName());
        assertEquals(payee.getNickName(), addInfo.getNickName());
        assertEquals(payee.getPhoneNumber(), addInfo.getPhoneNumber());
        assertNull(addInfo.getMerchant());
        assertNull(addInfo.getOvernightAddress());
        assertNull(addInfo.getAddressOnFile());
        assertNull(addInfo.getIsImageCapture());
        
        UsAddress usAddress = addInfo.getAddress();
        assertEquals(address.getAddress1(), usAddress.getAddress1());
        assertEquals(address.getAddress2(), usAddress.getAddress2());
        assertEquals(address.getCity(), usAddress.getCity());
        assertEquals(address.getState(), usAddress.getState());
        assertEquals(address.getPostalCode(), usAddress.getZip5());
        
        // validate the request header
        assertHeader(SUBSCRIBER_ID, modifyRequest.getHeader());
    }
    
    @Test
    public void deleteElectronicPayeeById() throws Exception {
        // create the mock response
        PayeeCancelResponse cancelResponse = PayeeCancelResponse.builder()
                                                                .result(createSuccessResult())
                                                                .build();
        
        // set up mock server to return the response
        setupWebServiceResponse(cancelResponse);
        
        
        // call the endpoint
        mockMvc.perform(delete(URL + ELECTRONIC_ENDPOINT + ID_ENDPOINT, PAYEE_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID)
                        .param("cancelPendingPayments", CANCEL_PENDING_PAYMENTS.toString()))
                        .andExpect(status().isNoContent());
        
        // validate the request data
        PayeeCancelRequest cancelRequest = retrieveRequest(PayeeCancelRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(cancelRequest.getPayeeId()));
        assertEquals(CANCEL_PENDING_PAYMENTS, cancelRequest.getCancelPayments());
        
        // validate the request header
        assertHeader(SUBSCRIBER_ID, cancelRequest.getHeader());
    }
    
    @Test
    public void postBillPayElectronicPayees() throws Exception {
        // create the mock response
        PayeeSummary payeeSummary = PayeeSummary.builder().payeeId(Long.valueOf(348596)).build();
        PayeeAddResponse addResponse = PayeeAddResponse.builder()
                                                       .result(createSuccessResult())
                                                       .payeeResultInfo(payeeSummary)
                                                       .build();
        
        // set up mock server to return the response
        setupWebServiceResponse(addResponse);

        // create the request body
        PostElectronicRequestPayee payee = new PostElectronicRequestPayee()
                        .withAccountNumber("accountNumber")
                        .withName("payeeName")
                        .withNickName("payeNickname")
                        .withMerchantID("42")
                        .withMerchantZipCode("zipCode");
        BillPayElectronicPayeesPostRequestBody request = new BillPayElectronicPayeesPostRequestBody()
                        .withPayee(payee)
                        .withSubscriberID(SUBSCRIBER_ID);
        
        String stringRequest = objectMapper.writeValueAsString(request);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(post(URL + ELECTRONIC_ENDPOINT)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString();
        
        BillPayElectronicPayeesPostResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayElectronicPayeesPostResponseBody.class);
        assertEquals(addResponse.getPayeeResultInfo().getPayeeId(), Long.valueOf(response.getId()));
        
        // validate the request data
        PayeeAddRequest addRequest = retrieveRequest(PayeeAddRequest.class);
        
        PayeeAddInfo addInfo = addRequest.getPayeeAddInfo();
        assertEquals(payee.getAccountNumber(), addInfo.getAccountNumber());
        assertEquals(payee.getName(), addInfo.getName());
        assertEquals(payee.getNickName(), addInfo.getNickName());
        assertEquals(payee.getMerchantID(), String.valueOf(addInfo.getMerchant().getMerchantNumber()));
        assertEquals(payee.getMerchantZipCode(), addInfo.getMerchant().getZip5());
        assertNull(addInfo.getOvernightAddress());
        assertTrue(addInfo.getAddressOnFile());
        assertNull(addInfo.getIsImageCapture());
        
        // validate the request header
        assertHeader(SUBSCRIBER_ID, addRequest.getHeader());
    }
    
    @Test
    public void getBillPayElectronicPayee() throws Exception {
        // create the mock response
        PaymentServices paymentServicesExpeditedPayment =
                        PaymentServices.builder()
                                       .cutOffTime(new Date())
                                       .earliestDate(createBlrdDate("2020-12-21"))
                                       .nextDate(createBlrdDate("2020-12-22"))
                                       .fee(new BigDecimal(19.99))
                                       .paymentService(PaymentServiceType.EXPEDITED_PAYMENT)
                                       .build();
        PaymentServices paymentServicesOvernightCheck =
                        PaymentServices.builder()
                                       .cutOffTime(new Date())
                                       .earliestDate(createBlrdDate("2020-12-22"))
                                       .nextDate(createBlrdDate("2020-12-23"))
                                       .fee(new BigDecimal(4.99))
                                       .paymentService(PaymentServiceType.OVERNIGHT_CHECK)
                                       .build();
        PayeeSummary payeeSummary = PayeeSummary.builder()
                                                .accountNumber("accountNumber")
                                                .payeeId(Long.valueOf(PAYEE_ID))
                                                .address(createAddress("standard"))
                                                .cutoffTime(new Date())
                                                .leadDays(new Integer(2))
                                                .name("Payee Name")
                                                .nickName("Payee Nickname")
                                                .earliestPaymentDate(createBlrdDate("2020-12-24"))
                                                .nextPaymentDate(createBlrdDate("2020-12-25"))
                                                .overNightAddress(createAddress("overnight"))
                                                .paymentServices(Arrays.asList(paymentServicesOvernightCheck,
                                                                               paymentServicesExpeditedPayment))
                                                .phoneNumber("phoneNumber")
                                                .ebillActivationStatus(EbillActivationStatusServiceType.EBILL_ACTIVE)
                                                .isAutopayEnabled(true)
                                                .ebillAutopayStatus(EbillAutopayStatusType.ENABLED)
                                                .build();
        PayeeListResponse listResponse = PayeeListResponse.builder()
                                                          .result(createSuccessResult())
                                                          .payees(Arrays.asList(payeeSummary))
                                                          .build();

        Ebill ebill = Ebill.builder()
                        .amountDue(new BigDecimal("250"))
                        .balance(new BigDecimal("34.50"))
                        .billReferenceLinkUrl("url")
                        .billType(BillType.FROM_BILLER)
                        .dueDate(new Date())
                        .ebillId("ebillId")
                        .payee(com.backbase.billpay.fiserv.payments.model.Payee.builder()
                                        .payeeId(Long.valueOf(PAYEE_ID))
                                        .build())
                        .status(EbillStatus.UNPAID)
                        .minimumAmountDue(new BigDecimal("175"))
                        .build();
        EbillListResponse ebillResponse =
                        EbillListResponse.builder().ebillList(Arrays.asList(ebill))
                                        .result(createSuccessResult()).build();

        BankAccountId bankAccountId = BankAccountId.builder()
                        .accountNumber("accNum")
                        .accountType(BankAccountType.DDA)
                        .routingTransitNumber("routingTransitNumber")
                        .build();
        AutoPayInfo autoPayInfo = AutoPayInfo.builder()
                        .autoPayAmount(AutoPayAmount.AMOUNT_DUE)
                        .autoPayOn(AutoPayOn.DUE_DATE)
                        .daysBefore(AutoPayDaysBefore.FOUR)
                        .fixedAmount(BigDecimal.ONE)
                        .maxAuthorizedAmount(BigDecimal.TEN)
                        .build();
        EbillAutoPayListResultInfo autopay = EbillAutoPayListResultInfo.builder()
                        .payeeId(Long.valueOf(PAYEE_ID))
                        .bankAccountId(bankAccountId)
                        .autoPay(autoPayInfo)
                        .paymentScheduledAlert(random.nextBoolean())
                        .paymentSentAlert(random.nextBoolean())
                        .build();
        EbillAutoPayListResponse autopayResponse = EbillAutoPayListResponse.builder()
                        .ebillAutoPayList(Arrays.asList(autopay))
                        .result(createSuccessResult()).build();
        
        // set up mock server to return the response
        setupWebServiceResponse(listResponse);
        setupWebServiceResponse(ebillResponse);
        setupWebServiceResponse(autopayResponse);

        
        // call the endpoint
        String stringResponse = mockMvc.perform(get(URL + ELECTRONIC_ENDPOINT + ID_ENDPOINT, PAYEE_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        ElectronicPayeeByIdGetResponseBody response =
                        objectMapper.readValue(stringResponse, ElectronicPayeeByIdGetResponseBody.class);
        
        // validate the response data
        ElectronicPayee payee = response.getPayee();
        assertEquals(payeeSummary.getPayeeId(), Long.valueOf(payee.getId()));
        assertEquals(payeeSummary.getName(), payee.getName());
        assertEquals(payeeSummary.getNickName(), payee.getNickName());
        assertEquals(payeeSummary.getAccountNumber(), payee.getAccountNumber());
        
        List<PaymentService> paymentServices = payee.getPaymentServices();
        PaymentService regularPaymentService = paymentServices.get(0);
        assertEquals(toZonedDateTime(payeeSummary.getCutoffTime()), regularPaymentService.getCutoffTime()
            .withZoneSameInstant(EST));
        assertEquals("REGULAR_PAYMENT", regularPaymentService.getPaymentServiceType());
        assertEquals(payeeSummary.getLeadDays(), regularPaymentService.getDeliveryDays());
        assertNull(regularPaymentService.getFee());
        
        PaymentService overnightCheckPaymentService = paymentServices.get(1);
        assertEquals(toZonedDateTime(paymentServicesOvernightCheck.getCutOffTime()),
            overnightCheckPaymentService.getCutoffTime().withZoneSameInstant(EST));
        assertEquals("OVERNIGHT_CHECK", overnightCheckPaymentService.getPaymentServiceType());
        assertEquals(payeeSummary.getLeadDays(), overnightCheckPaymentService.getDeliveryDays());
        assertEquals(paymentServicesOvernightCheck.getFee(), overnightCheckPaymentService.getFee().getAmount());
        assertEquals(CURRENCY, overnightCheckPaymentService.getFee().getCurrencyCode());
        
        PaymentService expeditedPaymentService = paymentServices.get(2);
        assertEquals(toZonedDateTime(paymentServicesExpeditedPayment.getCutOffTime()), expeditedPaymentService
            .getCutoffTime().withZoneSameInstant(EST));
        assertEquals("EXPEDITED_PAYMENT", expeditedPaymentService.getPaymentServiceType());
        assertEquals(payeeSummary.getLeadDays(), expeditedPaymentService.getDeliveryDays());
        assertEquals(paymentServicesExpeditedPayment.getFee(), expeditedPaymentService.getFee().getAmount());
        assertEquals(CURRENCY, expeditedPaymentService.getFee().getCurrencyCode());

        // validate eBill data
        com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Ebill responseEbill = response.getPayee().getEbill();
        assertEquals(true, responseEbill.getEnabled());
        assertEquals(true, responseEbill.getCapable());
        assertNull(responseEbill.getStatus());
        LatestBill latestBill = responseEbill.getLatestBill();

        assertEquals(ebill.getEbillId(), latestBill.getId());
        assertEquals(toZonedDateTime(ebill.getDueDate()).toLocalDate(), latestBill.getPaymentDate());
        assertEquals(ebill.getAmountDue(), latestBill.getAmount().getAmount());
        assertEquals(CURRENCY, latestBill.getAmount().getCurrencyCode());
        assertEquals(ebill.getMinimumAmountDue(), latestBill.getMinAmountDue().getAmount());
        assertEquals(CURRENCY, latestBill.getMinAmountDue().getCurrencyCode());
        assertEquals(ebill.getBalance(), latestBill.getOutstandingBalance().getAmount());
        assertEquals(CURRENCY, latestBill.getOutstandingBalance().getCurrencyCode());
        assertEquals(ebill.getStatus().name(), latestBill.getStatus());
        assertEquals(ebill.getBillReferenceLinkUrl(), latestBill.getUrl());
        assertFalse(latestBill.getStatementAvailable());

        // validate autopay data
        Autopay responseAutopay = responseEbill.getAutopay();
        assertEquals(autopay.getBankAccountId().getAccountNumber(),
                        responseAutopay.getPaymentAccount().getAccountNumber());
        assertEquals(autopay.getBankAccountId().getAccountType().toString(),
                        responseAutopay.getPaymentAccount().getAccountType());
        assertEquals(autopay.getBankAccountId().getRoutingTransitNumber(),
                        responseAutopay.getPaymentAccount().getRoutingNumber());
        assertEquals("FULL_AMOUNT", responseAutopay.getPayAmount());
        assertEquals("BILL_DUE_DATE", responseAutopay.getPayOn());
        assertEquals(Integer.valueOf(4), responseAutopay.getDaysBeforePayOn());
        assertEquals(autopay.getAutoPay().getMaxAuthorizedAmount(),
                        responseAutopay.getMaxPaymentAmount().getAmount());
        assertEquals(autopay.getPaymentScheduledAlert(), responseAutopay.getPaymentScheduledAlert());
        assertEquals(autopay.getPaymentSentAlert(), responseAutopay.getPaymentSentAlert());

        // validate requests
        PayeeListRequest listRequest = retrieveSpecificRequest(0, PayeeListRequest.class);
        assertHeader(SUBSCRIBER_ID, listRequest.getHeader());

        EbillListRequest ebillRequest = retrieveSpecificRequest(1, EbillListRequest.class);
        assertHeader(SUBSCRIBER_ID, ebillRequest.getHeader());
        EbillFilter filter = ebillRequest.getFilter();
        assertEquals(BillTypeFilter.BILLER, filter.getBillType());
        assertEquals(todayFiservDate().getDate(), filter.getStartingDate().getDate());
        assertEquals(Integer.valueOf("100"), filter.getNumberOfDays());

        EbillAutoPayListRequest autopayRequest = retrieveSpecificRequest(2, EbillAutoPayListRequest.class);
        assertHeader(SUBSCRIBER_ID, autopayRequest.getHeader());
        assertEquals(Long.valueOf(PAYEE_ID), autopayRequest.getFilter().getPayeeId());
    }
    
    @Test
    public void getBillPayElectronicPayee_ShouldReturn404() throws Exception {
        // create the mock response
        PayeeSummary payeeSummary = PayeeSummary.builder().payeeId(Long.valueOf(654231)).build();
        PayeeListResponse listResponse = PayeeListResponse.builder()
                                                          .result(createSuccessResult())
                                                          .payees(Arrays.asList(payeeSummary))
                                                          .build();
        
        // set up mock server to return the response
        setupWebServiceResponse(listResponse);

        // call the endpoint
        String stringResponse = mockMvc.perform(get(URL + ELECTRONIC_ENDPOINT + ID_ENDPOINT, PAYEE_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isNotFound())
                        .andReturn().getResponse().getContentAsString();

        NotFoundException exception = objectMapper.readValue(stringResponse, NotFoundException.class);
        assertEquals("Resource not found", exception.getMessage());

        PayeeListRequest listRequest = retrieveRequest(PayeeListRequest.class);
        assertEquals(SUBSCRIBER_ID, listRequest.getHeader().getSubscriberId());
    }
    
    @Test
    public void putBillPayElectronicPayees() throws Exception {
        // create the mock response
        PayeeModifyResponse modifyResponse = 
                        PayeeModifyResponse.builder()
                                           .result(createSuccessResult())
                                           .payeeResultInfo(PayeeSummary.builder()
                                                                        .payeeId(Long.valueOf(486786))
                                                                        .build())
                                           .build();
        
        // set up mock server to return the response
        setupWebServiceResponse(modifyResponse);
        
        // create the request body
        PutElectronicRequestPayee payee = new PutElectronicRequestPayee()
                        .withAccountNumber("accountNumber")
                        .withName("payeeName")
                        .withNickName("payeNickname")
                        .withModifyPendingPayments(true);
        ElectronicPayeeByIdPutRequestBody request = new ElectronicPayeeByIdPutRequestBody()
                        .withPayee(payee)
                        .withSubscriberID(SUBSCRIBER_ID);
        
        String stringRequest = objectMapper.writeValueAsString(request);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(put(URL + ELECTRONIC_ENDPOINT + ID_ENDPOINT, PAYEE_ID)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        ElectronicPayeeByIdPutResponseBody response =
                        objectMapper.readValue(stringResponse, ElectronicPayeeByIdPutResponseBody.class);
        assertEquals(modifyResponse.getPayeeResultInfo().getPayeeId(), Long.valueOf(response.getId()));
        
        // validate the request data
        PayeeModifyRequest modifyRequest = retrieveRequest(PayeeModifyRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(modifyRequest.getPayeeId()));
        assertEquals(request.getPayee().getModifyPendingPayments(), modifyRequest.getModifyPendingPayments());
        
        PayeeAddInfo addInfo = modifyRequest.getPayeeAddInfo();
        assertEquals(payee.getAccountNumber(), addInfo.getAccountNumber());
        assertEquals(payee.getName(), addInfo.getName());
        assertEquals(payee.getNickName(), addInfo.getNickName());
        assertNull(addInfo.getMerchant());
        assertNull(addInfo.getOvernightAddress());
        assertTrue(addInfo.getAddressOnFile());
        assertNull(addInfo.getIsImageCapture());
        
        // validate the request header
        assertHeader(SUBSCRIBER_ID, modifyRequest.getHeader());
    }
    
    private PayeeListResponse createPayeeListResponse() {
        PaymentServices paymentServicesExpeditedPayment = 
                        PaymentServices.builder()
                                       .cutOffTime(new Date())
                                       .earliestDate(createBlrdDate("2020-12-20"))
                                       .nextDate(createBlrdDate("2020-12-21"))
                                       .fee(new BigDecimal(19.99))
                                       .paymentService(PaymentServiceType.EXPEDITED_PAYMENT)
                                       .build();
        PaymentServices paymentServicesOvernightCheck =
                        PaymentServices.builder()
                                       .cutOffTime(new Date())
                                       .earliestDate(createBlrdDate("2020-12-22"))
                                       .nextDate(createBlrdDate("2020-12-23"))
                                       .fee(new BigDecimal(4.99))
                                       .paymentService(PaymentServiceType.OVERNIGHT_CHECK)
                                       .build();

        PayeeSummary payeeSummary = PayeeSummary.builder()
                                                .accountNumber("accountNumber")
                                                .payeeId(Long.valueOf(PAYEE_ID))
                                                .address(createAddress("standard"))
                                                .cutoffTime(new Date())
                                                .leadDays(new Integer(2))
                                                .name("Payee Name")
                                                .nickName("Payee Nickname")
                                                .overNightAddress(createAddress("overnight"))
                                                .paymentServices(Arrays.asList(paymentServicesOvernightCheck, 
                                                                               paymentServicesExpeditedPayment))
                                                .phoneNumber("phoneNumber")
                                                .earliestPaymentDate(createBlrdDate("2020-12-24"))
                                                .nextPaymentDate(createBlrdDate("2020-12-25"))
                                                .build();

        return PayeeListResponse.builder()
                                .result(createSuccessResult())
                                .payees(Arrays.asList(payeeSummary))
                                .build();
    }
    
    private BldrDate createBlrdDate(String date) {
        return BldrDate.builder()
                       .date(date)
                       .build();
    }
    
    private UsAddress createAddress(String addressPrefix) {
        return UsAddress.builder()
                        .address1(addressPrefix + "1")
                        .address2(addressPrefix + "2")
                        .city(addressPrefix + "City")
                        .state(addressPrefix + "State")
                        .zip5(addressPrefix + "Zip")
                        .build();
    }

}
