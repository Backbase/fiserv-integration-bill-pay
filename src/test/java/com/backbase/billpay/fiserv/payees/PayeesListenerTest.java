package com.backbase.billpay.fiserv.payees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import com.backbase.billpay.fiserv.payees.model.PaymentServices;
import com.backbase.billpay.fiserv.payees.model.PaymentServices.PaymentServiceType;
import com.backbase.billpay.fiserv.payees.model.USAddress;
import com.backbase.billpay.fiserv.utils.AbstractWebServiceTest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Address;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayElectronicPayeesPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayElectronicPayeesPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayPayeesPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayPayeesPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdPutResponseBody;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PayeesListenerTest extends AbstractWebServiceTest {
    
    private static final Boolean CANCEL_PENDING_PAYMENTS = true;
    
    @Autowired
    private PayeesListener listener;
    
    @Test
    public void deletePayeeById() {
        
        // create the mock response
        PayeeCancelResponse fiservResponse = PayeeCancelResponse.builder()
                                                                .result(createSuccessResult())
                                                                .build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);
        
        // call the listener method
        listener.deletePayeeById(createRequestWrapper(null), null, PAYEE_ID, SUBSCRIBER_ID, CANCEL_PENDING_PAYMENTS);
        
        // validate the request data
        PayeeCancelRequest cancelRequest = retrieveRequest(PayeeCancelRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(cancelRequest.getPayeeId()));
        assertEquals(CANCEL_PENDING_PAYMENTS, cancelRequest.getCancelPayments());
        
        // validate the request header
        assertHeader(SUBSCRIBER_ID, cancelRequest.getHeader());
    }
    
    @Test
    public void postBillPayPayees() {
        
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
                                                        .withPostalCode("postalCode")
                                                        .withState("state"))
                                        .withName("payeeName")
                                        .withNickName("payeNickname")
                                        .withPhoneNumber("phoneNumber"))
                        .withSubscriberID(SUBSCRIBER_ID);
        
        BillPayPayeesPostResponseBody response =
                        listener.postBillPayPayees(createRequestWrapper(request), null).getRequest().getData();
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
        
        USAddress usAddress = addInfo.getAddress();
        assertEquals(request.getPayee().getAddress().getAddress1(), usAddress.getAddress1());
        assertEquals(request.getPayee().getAddress().getAddress2(), usAddress.getAddress2());
        assertEquals(request.getPayee().getAddress().getCity(), usAddress.getCity());
        assertEquals(request.getPayee().getAddress().getState(), usAddress.getState());
        assertEquals(request.getPayee().getAddress().getPostalCode(), usAddress.getZip5());
    }
    
    @Test
    public void getBillPayPayeeById() {

        PayeeListResponse listResponse = createPayeeListResponse();
        
        setupWebServiceResponse(listResponse);
        
        PayeeByIdGetResponseBody response = listener
                        .getPayeeById(createRequestWrapper(null), null, PAYEE_ID, SUBSCRIBER_ID).getRequest().getData();
        
        Payee payee = response.getPayee();
        PayeeSummary payeeSummary = listResponse.getPayees().get(0);
        assertEquals(payeeSummary.getPayeeId(), Long.valueOf(payee.getId()));
        assertEquals(payeeSummary.getName(), payee.getName());
        assertEquals(payeeSummary.getNickName(), payee.getNickName());
        assertEquals(payeeSummary.getPhoneNumber(), payee.getPhoneNumber());
        assertEquals(payeeSummary.getAccountNumber(), payee.getAccountNumber());
        
        Address address = payee.getAddress();
        USAddress usAddress = payeeSummary.getAddress();
        assertEquals(usAddress.getAddress1(), address.getAddress1());
        assertEquals(usAddress.getAddress2(), address.getAddress2());
        assertEquals(usAddress.getCity(), address.getCity());
        assertEquals(usAddress.getState(), address.getState());
        assertEquals(usAddress.getZip5(), address.getPostalCode());
        
        Address overnightDeliveryAddress = payee.getOvernightDeliveryAddress();
        USAddress deliveryAddress = payeeSummary.getOverNightAddress();
        assertEquals(deliveryAddress.getAddress1(), overnightDeliveryAddress.getAddress1());
        assertEquals(deliveryAddress.getAddress2(), overnightDeliveryAddress.getAddress2());
        assertEquals(deliveryAddress.getCity(), overnightDeliveryAddress.getCity());
        assertEquals(deliveryAddress.getState(), overnightDeliveryAddress.getState());
        assertEquals(deliveryAddress.getZip5(), overnightDeliveryAddress.getPostalCode());
        
        List<PaymentService> paymentServices = payee.getPaymentServices();
        PaymentService regularPaymentService = paymentServices.get(0);
        assertEquals(payeeSummary.getCutoffTime(), regularPaymentService.getCutoffTime());
        assertEquals("REGULAR_PAYMENT", regularPaymentService.getPaymentServiceType());
        assertEquals(payeeSummary.getLeadDays(), regularPaymentService.getDeliveryDays());
        assertNull(regularPaymentService.getPaymentFee());
        assertNull(regularPaymentService.getFee());
        
        PaymentService overnightCheckPaymentService = paymentServices.get(1);
        PaymentServices paymentServicesOvernightCheck = payeeSummary.getPaymentServices().get(0);
        assertEquals(paymentServicesOvernightCheck.getCutOffTime(), overnightCheckPaymentService.getCutoffTime());
        assertEquals("OVERNIGHT_CHECK", overnightCheckPaymentService.getPaymentServiceType());
        assertEquals(payeeSummary.getLeadDays(), overnightCheckPaymentService.getDeliveryDays());
        assertEquals(paymentServicesOvernightCheck.getFee(), overnightCheckPaymentService.getPaymentFee());
        assertEquals(paymentServicesOvernightCheck.getFee(), overnightCheckPaymentService.getFee().getAmount());
        assertEquals("USD", overnightCheckPaymentService.getFee().getCurrencyCode());
        
        PaymentService expeditedPaymentService = paymentServices.get(2);
        PaymentServices paymentServicesExpeditedPayment = payeeSummary.getPaymentServices().get(1);
        assertEquals(paymentServicesExpeditedPayment.getCutOffTime(), expeditedPaymentService.getCutoffTime());
        assertEquals("EXPEDITED_PAYMENT", expeditedPaymentService.getPaymentServiceType());
        assertEquals(payeeSummary.getLeadDays(), expeditedPaymentService.getDeliveryDays());
        assertEquals(paymentServicesExpeditedPayment.getFee(), expeditedPaymentService.getPaymentFee());
        assertEquals(paymentServicesExpeditedPayment.getFee(), expeditedPaymentService.getFee().getAmount());
        assertEquals("USD", expeditedPaymentService.getFee().getCurrencyCode());
        
        PayeeListRequest listRequest = retrieveRequest(PayeeListRequest.class);
        
        // validate the request header
        assertHeader(SUBSCRIBER_ID, listRequest.getHeader());
    }
    
    @Test
    public void getPayeeById_ShouldReturn404() {

        PayeeSummary payeeSummary = PayeeSummary.builder()
                                                .payeeId(Long.valueOf(654231))
                                                .build();
        PayeeListResponse listResponse = PayeeListResponse.builder()
                                                          .result(createSuccessResult())
                                                          .payees(Arrays.asList(payeeSummary))
                                                          .build();
        
        setupWebServiceResponse(listResponse);
        
        try {
            listener.getPayeeById(createRequestWrapper(null), null, PAYEE_ID, SUBSCRIBER_ID);
            fail("Expected NotFoundException to be thrown");
        } catch (NotFoundException exception) {
            assertEquals("Resource not found", exception.getMessage());
            
            PayeeListRequest listRequest = retrieveRequest(PayeeListRequest.class);
            assertEquals(SUBSCRIBER_ID, listRequest.getHeader().getSubscriberId());
        }
    }
    
    @Test
    public void putBillPayPayeesById() {
        PayeeModifyResponse modifyResponse = 
                        PayeeModifyResponse.builder()
                                           .result(createSuccessResult())
                                           .payeeResultInfo(PayeeSummary.builder()
                                                                        .payeeId(Long.valueOf(486786))
                                                                        .build())
                                           .build();
        
        setupWebServiceResponse(modifyResponse);

        Address address = new Address()
                        .withAddress1("address1")
                        .withAddress2("address2")
                        .withCity("city")
                        .withPostalCode("postalCode")
                        .withState("state");
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
        
        PayeeByIdPutResponseBody response =
                        listener.putPayeeById(createRequestWrapper(request), null, PAYEE_ID).getRequest().getData();
        assertEquals(modifyResponse.getPayeeResultInfo().getPayeeId(), Long.valueOf(response.getId()));
        
        PayeeModifyRequest modifyRequest = retrieveRequest(PayeeModifyRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(modifyRequest.getPayeeId()));
        assertEquals(request.getPayee().getModifyPendingPayments(), modifyRequest.getModifyPendingPayments());
        
        // validate the request header
        assertHeader(SUBSCRIBER_ID, modifyRequest.getHeader());
        
        PayeeAddInfo addInfo = modifyRequest.getPayeeAddInfo();
        assertEquals(payee.getAccountNumber(), addInfo.getAccountNumber());
        assertEquals(payee.getName(), addInfo.getName());
        assertEquals(payee.getNickName(), addInfo.getNickName());
        assertEquals(payee.getPhoneNumber(), addInfo.getPhoneNumber());
        assertNull(addInfo.getMerchant());
        assertNull(addInfo.getOvernightAddress());
        assertNull(addInfo.getAddressOnFile());
        assertNull(addInfo.getIsImageCapture());
        
        USAddress usAddress = addInfo.getAddress();
        assertEquals(address.getAddress1(), usAddress.getAddress1());
        assertEquals(address.getAddress2(), usAddress.getAddress2());
        assertEquals(address.getCity(), usAddress.getCity());
        assertEquals(address.getState(), usAddress.getState());
        assertEquals(address.getPostalCode(), usAddress.getZip5());
    }
    
    @Test
    public void deleteElectronicPayeeById() {
        PayeeCancelResponse cancelResponse = PayeeCancelResponse.builder()
                                                                .result(createSuccessResult())
                                                                .build();
        
        setupWebServiceResponse(cancelResponse);
        
        listener.deleteElectronicPayeeById(createRequestWrapper(null), null, PAYEE_ID, 
                                           SUBSCRIBER_ID, CANCEL_PENDING_PAYMENTS);
        
        PayeeCancelRequest cancelRequest = retrieveRequest(PayeeCancelRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(cancelRequest.getPayeeId()));
        assertEquals(CANCEL_PENDING_PAYMENTS, cancelRequest.getCancelPayments());
        
        // validate the request header
        assertHeader(SUBSCRIBER_ID, cancelRequest.getHeader());
    }
    
    @Test
    public void postBillPayElectronicPayees() {
        PayeeSummary payeeSummary = PayeeSummary.builder().payeeId(Long.valueOf(348596)).build();
        PayeeAddResponse addResponse = PayeeAddResponse.builder()
                                                       .result(createSuccessResult())
                                                       .payeeResultInfo(payeeSummary)
                                                       .build();
        
        setupWebServiceResponse(addResponse);

        PostElectronicRequestPayee payee = new PostElectronicRequestPayee()
                        .withAccountNumber("accountNumber")
                        .withName("payeeName")
                        .withNickName("payeNickname")
                        .withMerchantID("42")
                        .withMerchantZipCode("zipCode");
        BillPayElectronicPayeesPostRequestBody request = new BillPayElectronicPayeesPostRequestBody()
                        .withPayee(payee)
                        .withSubscriberID(SUBSCRIBER_ID);
        
        BillPayElectronicPayeesPostResponseBody response =
                        listener.postBillPayElectronicPayees(createRequestWrapper(request), null)
                            .getRequest().getData();
        assertEquals(addResponse.getPayeeResultInfo().getPayeeId(), Long.valueOf(response.getId()));
        
        PayeeAddRequest addRequest = retrieveRequest(PayeeAddRequest.class);
        
        // validate the request header
        assertHeader(SUBSCRIBER_ID, addRequest.getHeader());
        
        PayeeAddInfo addInfo = addRequest.getPayeeAddInfo();
        assertEquals(payee.getAccountNumber(), addInfo.getAccountNumber());
        assertEquals(payee.getName(), addInfo.getName());
        assertEquals(payee.getNickName(), addInfo.getNickName());
        assertEquals(payee.getMerchantID(), String.valueOf(addInfo.getMerchant().getMerchantNumber()));
        assertEquals(payee.getMerchantZipCode(), addInfo.getMerchant().getZip5());
        assertNull(addInfo.getOvernightAddress());
        assertTrue(addInfo.getAddressOnFile());
        assertNull(addInfo.getIsImageCapture());
    }
    
    @Test
    public void getBillPayElectronicPayee() {
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
                                                .build();
        PayeeListResponse listResponse = PayeeListResponse.builder()
                                                          .result(createSuccessResult())
                                                          .payees(Arrays.asList(payeeSummary))
                                                          .build();
        
        setupWebServiceResponse(listResponse);
        
        ElectronicPayeeByIdGetResponseBody response = listener
                        .getElectronicPayeeById(createRequestWrapper(null), null, PAYEE_ID, SUBSCRIBER_ID)
                            .getRequest().getData();
        
        ElectronicPayee payee = response.getPayee();
        assertEquals(payeeSummary.getPayeeId(), Long.valueOf(payee.getId()));
        assertEquals(payeeSummary.getName(), payee.getName());
        assertEquals(payeeSummary.getNickName(), payee.getNickName());
        assertEquals(payeeSummary.getAccountNumber(), payee.getAccountNumber());
        
        List<PaymentService> paymentServices = payee.getPaymentServices();
        PaymentService regularPaymentService = paymentServices.get(0);
        assertEquals(payeeSummary.getCutoffTime(), regularPaymentService.getCutoffTime());
        assertEquals("REGULAR_PAYMENT", regularPaymentService.getPaymentServiceType());
        assertEquals(payeeSummary.getLeadDays(), regularPaymentService.getDeliveryDays());
        assertNull(regularPaymentService.getPaymentFee());
        assertNull(regularPaymentService.getFee());
        
        PaymentService overnightCheckPaymentService = paymentServices.get(1);
        assertEquals(paymentServicesOvernightCheck.getCutOffTime(), overnightCheckPaymentService.getCutoffTime());
        assertEquals("OVERNIGHT_CHECK", overnightCheckPaymentService.getPaymentServiceType());
        assertEquals(payeeSummary.getLeadDays(), overnightCheckPaymentService.getDeliveryDays());
        assertEquals(paymentServicesOvernightCheck.getFee(), overnightCheckPaymentService.getPaymentFee());
        assertEquals(paymentServicesOvernightCheck.getFee(), overnightCheckPaymentService.getFee().getAmount());
        assertEquals("USD", overnightCheckPaymentService.getFee().getCurrencyCode());
        
        PaymentService expeditedPaymentService = paymentServices.get(2);
        assertEquals(paymentServicesExpeditedPayment.getCutOffTime(), expeditedPaymentService.getCutoffTime());
        assertEquals("EXPEDITED_PAYMENT", expeditedPaymentService.getPaymentServiceType());
        assertEquals(payeeSummary.getLeadDays(), expeditedPaymentService.getDeliveryDays());
        assertEquals(paymentServicesExpeditedPayment.getFee(), expeditedPaymentService.getPaymentFee());
        assertEquals(paymentServicesExpeditedPayment.getFee(), expeditedPaymentService.getFee().getAmount());
        assertEquals("USD", expeditedPaymentService.getFee().getCurrencyCode());
        
        PayeeListRequest listRequest = retrieveRequest(PayeeListRequest.class);
        // validate the request header
        assertHeader(SUBSCRIBER_ID, listRequest.getHeader());
    }
    
    @Test
    public void getBillPayElectronicPayee_ShouldReturn404() {
        
        PayeeSummary payeeSummary = PayeeSummary.builder().payeeId(Long.valueOf(654231)).build();
        PayeeListResponse listResponse = PayeeListResponse.builder()
                                                          .result(createSuccessResult())
                                                          .payees(Arrays.asList(payeeSummary))
                                                          .build();
        
        setupWebServiceResponse(listResponse);
        
        try {
            listener.getElectronicPayeeById(createRequestWrapper(null), null, PAYEE_ID, SUBSCRIBER_ID);
            fail("Expected NotFoundException to be thrown");
        } catch (NotFoundException exception) {
            assertEquals("Resource not found", exception.getMessage());
            
            PayeeListRequest listRequest = retrieveRequest(PayeeListRequest.class);
            assertEquals(SUBSCRIBER_ID, listRequest.getHeader().getSubscriberId());
        }
    }
    
    @Test
    public void putBillPayElectronicPayees() {
        PayeeModifyResponse modifyResponse = 
                        PayeeModifyResponse.builder()
                                           .result(createSuccessResult())
                                           .payeeResultInfo(PayeeSummary.builder()
                                                                        .payeeId(Long.valueOf(486786))
                                                                        .build())
                                           .build();
        
        setupWebServiceResponse(modifyResponse);
        
        PutElectronicRequestPayee payee = new PutElectronicRequestPayee()
                        .withAccountNumber("accountNumber")
                        .withName("payeeName")
                        .withNickName("payeNickname")
                        .withModifyPendingPayments(true);
        ElectronicPayeeByIdPutRequestBody request = new ElectronicPayeeByIdPutRequestBody()
                        .withPayee(payee)
                        .withSubscriberID(SUBSCRIBER_ID);
        
        ElectronicPayeeByIdPutResponseBody response =
                        listener.putElectronicPayeeById(createRequestWrapper(request), null, PAYEE_ID)
                            .getRequest().getData();
        assertEquals(modifyResponse.getPayeeResultInfo().getPayeeId(), Long.valueOf(response.getId()));
        
        PayeeModifyRequest modifyRequest = retrieveRequest(PayeeModifyRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(modifyRequest.getPayeeId()));
        assertEquals(request.getPayee().getModifyPendingPayments(), modifyRequest.getModifyPendingPayments());

        // validate the request header
        assertHeader(SUBSCRIBER_ID, modifyRequest.getHeader());
        
        PayeeAddInfo addInfo = modifyRequest.getPayeeAddInfo();
        assertEquals(payee.getAccountNumber(), addInfo.getAccountNumber());
        assertEquals(payee.getName(), addInfo.getName());
        assertEquals(payee.getNickName(), addInfo.getNickName());
        assertNull(addInfo.getMerchant());
        assertNull(addInfo.getOvernightAddress());
        assertTrue(addInfo.getAddressOnFile());
        assertNull(addInfo.getIsImageCapture());
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
    
    private USAddress createAddress(String addressPrefix) {
        return USAddress.builder()
                        .address1(addressPrefix + "1")
                        .address2(addressPrefix + "2")
                        .city(addressPrefix + "City")
                        .state(addressPrefix + "State")
                        .zip5(addressPrefix + "Zip")
                        .build();
    }
}