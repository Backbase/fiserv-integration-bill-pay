package com.backbase.billpay.fiserv.payeessummary;

import static com.backbase.billpay.fiserv.payees.PaymentServicesMapper.CURRENCY;
import static com.backbase.billpay.fiserv.utils.FiservUtils.fromFiservDate;
import static com.backbase.billpay.fiserv.utils.FiservUtils.toFiservDate;
import static com.backbase.billpay.fiserv.utils.FiservUtils.todayFiservDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo;
import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo.AutoPayAmount;
import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo.AutoPayDaysBefore;
import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo.AutoPayOn;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListRequest;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResponse;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResultInfo;
import com.backbase.billpay.fiserv.payees.model.PayeeListRequest;
import com.backbase.billpay.fiserv.payees.model.PayeeListResponse;
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
import com.backbase.billpay.fiserv.payments.model.Payee;
import com.backbase.billpay.fiserv.payments.model.Payment;
import com.backbase.billpay.fiserv.payments.model.PaymentDetail.PaymentStatus;
import com.backbase.billpay.fiserv.payments.model.PaymentFilter;
import com.backbase.billpay.fiserv.payments.model.PaymentFilter.PaymentStatusFilter;
import com.backbase.billpay.fiserv.payments.model.PaymentListRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentListResponse;
import com.backbase.billpay.fiserv.utils.AbstractWebServiceTest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Address;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Autopay;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.BillPayPayeesSummaryGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.LatestBill;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.NextPayment;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.PaymentService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.Test;

public class PayeesSummaryControllerTest extends AbstractWebServiceTest {

    private Random random = new Random();

    private static final String URL = "/service-api/v2/bill-pay/payees-summary";
    
    @Test
    public void getBillPayPayeesSummary() throws Exception {
        // create and setup the mock responses
        Long payeeId1 = Long.valueOf(1);
        Long payeeId2 = Long.valueOf(2);
        Long payeeId3 = Long.valueOf(3);
        Long payeeId4 = Long.valueOf(4);

        PayeeSummary payeeSummary1 = createPayeeSummary(payeeId1);
        PayeeSummary payeeSummary2 = createPayeeSummary(payeeId2);
        PayeeSummary payeeSummary3 = createPayeeSummary(payeeId3);
        PayeeSummary payeeSummary4 = createPayeeSummary(payeeId4);
        payeeSummary4.setEbillActivationStatus(EbillActivationStatusServiceType.EBILL_NOT_AVAILABLE);
        payeeSummary4.setMerchantId(Integer.valueOf(4));
        PayeeListResponse payeeResponse = PayeeListResponse.builder()
                        .payees(Arrays.asList(payeeSummary1, payeeSummary2, payeeSummary3, payeeSummary4))
                        .result(createSuccessResult()).build();
        setupWebServiceResponse(payeeResponse);

        Ebill ebill1 = createEbill(payeeId1);
        Ebill ebill2 = createEbill(payeeId2);
        Ebill ebill2NotNext = createEbill(payeeId2);
        ebill2NotNext.setDueDate(Date.from(Instant.now().plusSeconds(TimeUnit.DAYS.toSeconds(10))));
        Ebill ebill4 = createEbill(payeeId4);
        ebill4.setStatus(EbillStatus.FILED);
        EbillListResponse ebillResponse =
                        EbillListResponse.builder().ebillList(Arrays.asList(ebill1, ebill2, ebill2NotNext, ebill4))
                                        .result(createSuccessResult()).build();
        setupWebServiceResponse(ebillResponse);

        EbillAutoPayListResultInfo autopay1 = createAutopay(payeeId1);
        EbillAutoPayListResponse autopayResponse = EbillAutoPayListResponse.builder()
                        .ebillAutoPayList(Arrays.asList(autopay1))
                        .result(createSuccessResult()).build();
        setupWebServiceResponse(autopayResponse);

        Payment payment1 = createPayment(payeeId1);
        Payment payment1NotNext = createPayment(payeeId1);
        payment1NotNext.setPaymentDate(toFiservDate(Date.from(Instant.now().plusSeconds(TimeUnit.DAYS.toSeconds(2)))));
        Payment payment3 = createPayment(payeeId3);
        PaymentListResponse paymentListResponse =
                        PaymentListResponse.builder().payments(Arrays.asList(payment1, payment1NotNext, payment3))
                                        .result(createSuccessResult()).build();
        setupWebServiceResponse(paymentListResponse);

        // call the endpoint
        String stringResponse = mockMvc.perform(get(URL)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        BillPayPayeesSummaryGetResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayPayeesSummaryGetResponseBody.class);

        // validate the response data
        assertNotNull(response);
        List<com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.PayeeSummary> responseSummaries =
                        response.getPayeeSummaries();
        assertEquals(payeeResponse.getPayees().size(), responseSummaries.size());
        for (com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.PayeeSummary 
                        payeeSummary : responseSummaries) {
            switch (payeeSummary.getId()) {
                case "1":
                    assertPayeeSummary(ebill1, autopay1, payment1, payeeSummary1, payeeSummary);
                    break;
                case "2":
                    assertPayeeSummary(ebill2, null,  null, payeeSummary2, payeeSummary);
                    break;
                case "3":
                    assertPayeeSummary(null, null, payment3, payeeSummary3, payeeSummary);
                    break;
                case "4":
                    assertPayeeSummary(null, null, null, payeeSummary4, payeeSummary);
                    break;
                default:
                    fail("Unknown payee id in payee summaries");
            }
        }

        // validate the request data
        PayeeListRequest payeeRequest = retrieveSpecificRequest(0, PayeeListRequest.class);
        assertHeader(SUBSCRIBER_ID, payeeRequest.getHeader());

        EbillListRequest ebillRequest = retrieveSpecificRequest(1, EbillListRequest.class);
        assertHeader(SUBSCRIBER_ID, ebillRequest.getHeader());
        EbillFilter filter = ebillRequest.getFilter();
        assertEquals(BillTypeFilter.ALL, filter.getBillType());
        assertEquals(todayFiservDate().getDate(), filter.getStartingDate().getDate());
        assertEquals(Integer.valueOf("100"), filter.getNumberOfDays());

        EbillAutoPayListRequest autopayRequest = retrieveSpecificRequest(2, EbillAutoPayListRequest.class);
        assertHeader(SUBSCRIBER_ID, autopayRequest.getHeader());
        assertNull(autopayRequest.getFilter());

        PaymentListRequest paymentRequest = retrieveSpecificRequest(3, PaymentListRequest.class);
        assertHeader(SUBSCRIBER_ID, paymentRequest.getHeader());
        PaymentFilter paymentFilter = paymentRequest.getFilter();
        assertEquals(PaymentStatusFilter.PENDING, paymentFilter.getStatusFilter());
        assertEquals(todayFiservDate().getDate(), filter.getStartingDate().getDate());
        assertEquals(Integer.valueOf("100"), filter.getNumberOfDays());
    }

    private EbillAutoPayListResultInfo createAutopay(Long payeeId) {
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

        return EbillAutoPayListResultInfo.builder()
                        .payeeId(payeeId)
                        .bankAccountId(bankAccountId)
                        .autoPay(autoPayInfo)
                        .paymentScheduledAlert(random.nextBoolean())
                        .paymentSentAlert(random.nextBoolean())
                        .build();
    }

    private void assertPayeeSummary(Ebill expectedEbill,
                    EbillAutoPayListResultInfo autopay,
                    Payment expectedPayment, PayeeSummary expectedSummary,
                    com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.PayeeSummary responseSummary) {
        assertEquals(String.valueOf(expectedSummary.getPayeeId()), responseSummary.getId());
        assertEquals(expectedSummary.getName(), responseSummary.getName());
        assertEquals(expectedSummary.getNickName(), responseSummary.getNickName());
        assertEquals(expectedSummary.getAccountNumber(), responseSummary.getAccountNumber());
        assertEquals(expectedSummary.getMerchantId() != null, responseSummary.getElectronic());
        assertPaymentServices(expectedSummary, responseSummary.getPaymentServices());
        assertNextPayment(expectedPayment, responseSummary.getNextPayment());
        assertEquals(expectedSummary.getIsRecurringModelEnabled(), responseSummary.getHasRecurringPayments());

        EbillActivationStatusServiceType ebillActivationStatus = expectedSummary.getEbillActivationStatus();
        assertEquals(ebillActivationStatus != EbillActivationStatusServiceType.EBILL_NOT_AVAILABLE,
                        responseSummary.getEbill().getCapable());
        assertEquals(ebillActivationStatus == EbillActivationStatusServiceType.EBILL_ACTIVE
                                        || ebillActivationStatus == EbillActivationStatusServiceType.EBILL_PENDING,
                        responseSummary.getEbill().getEnabled());
        assertLatestBill(expectedEbill, responseSummary.getEbill().getLatestBill());

        assertAutopay(autopay, responseSummary);

        UsAddress expectedAddress = expectedSummary.getOverNightAddress();
        Address address = responseSummary.getOvernightDeliveryAddress();
        assertEquals(expectedAddress.getAddress1(), address.getAddress1());
        assertEquals(expectedAddress.getAddress2(), address.getAddress2());
        assertEquals(expectedAddress.getCity(), address.getCity());
        assertEquals(expectedAddress.getState(), address.getState());
        assertEquals(expectedAddress.getZip5(), address.getPostalCode());
    }

    private void assertAutopay(EbillAutoPayListResultInfo autopay,
                    com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.PayeeSummary responseSummary) {
        if (autopay == null) {
            if (responseSummary.getEbill() != null) {
                assertNull(responseSummary.getEbill().getAutopay());
            }
        } else {
            Autopay responseSummaryAutopay = responseSummary.getEbill().getAutopay();
            assertEquals(String.valueOf(autopay.getPayeeId()), responseSummary.getId());
            assertEquals(autopay.getBankAccountId().getAccountNumber(),
                            responseSummaryAutopay.getPaymentAccount().getAccountNumber());
            assertEquals(autopay.getBankAccountId().getAccountType().toString(),
                            responseSummaryAutopay.getPaymentAccount().getAccountType());
            assertEquals(autopay.getBankAccountId().getRoutingTransitNumber(),
                            responseSummaryAutopay.getPaymentAccount().getRoutingNumber());
            assertEquals("FULL_AMOUNT", responseSummaryAutopay.getPayAmount());
            assertEquals("BILL_DUE_DATE", responseSummaryAutopay.getPayOn());
            assertEquals(Integer.valueOf(4), responseSummaryAutopay.getDaysBeforePayOn());
            assertEquals(autopay.getAutoPay().getMaxAuthorizedAmount(),
                            responseSummaryAutopay.getMaxPaymentAmount().getAmount());
            assertEquals(autopay.getPaymentScheduledAlert(), responseSummaryAutopay.getPaymentScheduledAlert());
            assertEquals(autopay.getPaymentSentAlert(), responseSummaryAutopay.getPaymentSentAlert());
        }
    }


    private Payment createPayment(Long payeeId) {
        return Payment.builder().payee(Payee.builder().payeeId(payeeId).build()).paymentId("paymentId")
                        .amount(new BigDecimal("35")).paymentDate(todayFiservDate()).status(PaymentStatus.PENDING)
                        .ebillAutoPayment(true).build();
    }

    private Ebill createEbill(Long payeeId) {
        return Ebill.builder().amountDue(new BigDecimal("250")).balance(new BigDecimal("34.50"))
                        .billReferenceLinkUrl("url").billType(BillType.FROM_BILLER).dueDate(new Date())
                        .ebillId("ebillId").payee(Payee.builder().payeeId(payeeId).build()).status(EbillStatus.UNPAID)
                        .minimumAmountDue(new BigDecimal("175")).build();
    }

    private PayeeSummary createPayeeSummary(Long payeeId) {
        return PayeeSummary.builder().name("Payee Name").nickName("Payee Nickname").payeeId(payeeId)
                        .accountNumber("Acc" + payeeId)
                        .ebillActivationStatus(EbillActivationStatusServiceType.EBILL_ACTIVE)
                        .cutoffTime(new Date())
                        .earliestPaymentDate(todayFiservDate())
                        .nextPaymentDate(todayFiservDate())
                        .overNightAddress(UsAddress.builder().address1("Address1").address2("Address2").city("City")
                                        .state("State").zip5("ZIPCODE").build())
                        .paymentServices(createPaymentServices())
                        .isAutopayEnabled(true)
                        .ebillAutopayStatus(EbillAutopayStatusType.ENABLED)
                        .isRecurringModelEnabled(true)
                        .build();
    }

    private List<PaymentServices> createPaymentServices() {
        List<PaymentServices> paymentServices = new ArrayList<>();
        paymentServices.add(PaymentServices.builder().cutOffTime(new Date()).earliestDate(todayFiservDate())
                        .nextDate(todayFiservDate()).paymentService(PaymentServiceType.OVERNIGHT_CHECK)
                        .fee(new BigDecimal("9.99")).build());
        paymentServices.add(PaymentServices.builder().cutOffTime(new Date()).earliestDate(todayFiservDate())
                        .nextDate(todayFiservDate()).paymentService(PaymentServiceType.EXPEDITED_PAYMENT)
                        .fee(new BigDecimal("19.98")).build());
        return paymentServices;
    }

    private void assertPaymentServices(PayeeSummary payeeSummary, List<PaymentService> responseServices) {
        for (PaymentService responseService : responseServices) {
            if (StringUtils.equals("REGULAR_PAYMENT", responseService.getPaymentServiceType())) {
                assertEquals(payeeSummary.getCutoffTime(), responseService.getCutoffTime());
                assertEquals(fromFiservDate(payeeSummary.getEarliestPaymentDate()),
                                responseService.getEarliestPaymentDate());
                assertEquals(fromFiservDate(payeeSummary.getNextPaymentDate()), responseService.getNextPaymentDate());
                assertEquals(payeeSummary.getLeadDays(), responseService.getDeliveryDays());
                assertNull(responseService.getFee());
            } else {
                PaymentServices expectedService = StringUtils.equals("OVERNIGHT_CHECK", responseService.getPaymentServiceType())
                                ? payeeSummary.getPaymentServices().get(0)
                                : payeeSummary.getPaymentServices().get(1);
                assertEquals(expectedService.getCutOffTime(), responseService.getCutoffTime());
                assertEquals(fromFiservDate(expectedService.getEarliestDate()),
                                responseService.getEarliestPaymentDate());
                assertEquals(fromFiservDate(expectedService.getNextDate()), responseService.getNextPaymentDate());
                assertEquals(payeeSummary.getLeadDays(), responseService.getDeliveryDays());
                assertEquals(expectedService.getFee(), responseService.getFee().getAmount());
                assertEquals(CURRENCY, responseService.getFee().getCurrencyCode());
            }
        }
    }

    private void assertNextPayment(Payment expectedPayment, NextPayment nextPayment) {
        if (expectedPayment == null) {
            assertNull(nextPayment);
        } else {
            assertEquals(fromFiservDate(expectedPayment.getPaymentDate()), nextPayment.getPaymentDate());
            assertEquals(expectedPayment.getAmount(), nextPayment.getAmount().getAmount());
            assertEquals(CURRENCY, nextPayment.getAmount().getCurrencyCode());
            assertEquals(expectedPayment.getRecurringModelPayment(), nextPayment.getRecurring());
            assertNull(nextPayment.getFee());
            assertEquals(expectedPayment.getEbillAutoPayment(), nextPayment.getAutomaticPayment());
        }

    }

    private void assertLatestBill(Ebill expectedEbill, LatestBill bill) {
        if (expectedEbill == null) {
            assertNull(bill);
        } else {
            assertEquals(expectedEbill.getEbillId(), bill.getId());
            assertEquals(expectedEbill.getDueDate(), bill.getPaymentDate());
            assertEquals(expectedEbill.getAmountDue(), bill.getAmount().getAmount());
            assertEquals(CURRENCY, bill.getAmount().getCurrencyCode());
            assertEquals(expectedEbill.getMinimumAmountDue(), bill.getMinAmountDue().getAmount());
            assertEquals(CURRENCY, bill.getMinAmountDue().getCurrencyCode());
            assertEquals(expectedEbill.getBalance(), bill.getOutstandingBalance().getAmount());
            assertEquals(CURRENCY, bill.getOutstandingBalance().getCurrencyCode());
            assertEquals(expectedEbill.getStatus().name(), bill.getStatus());
            assertEquals(expectedEbill.getBillReferenceLinkUrl(), bill.getUrl());
            assertFalse(bill.getStatementAvailable());
        }
    }

}
