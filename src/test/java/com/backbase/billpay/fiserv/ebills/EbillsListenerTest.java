package com.backbase.billpay.fiserv.ebills;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.backbase.billpay.fiserv.ebills.model.EbillAccountCancelRequest;
import com.backbase.billpay.fiserv.ebills.model.EbillAccountCancelResponse;
import com.backbase.billpay.fiserv.ebills.model.EbillFileRequest;
import com.backbase.billpay.fiserv.ebills.model.EbillFileRequest.FileEbillPaymentMethod;
import com.backbase.billpay.fiserv.ebills.model.EbillFileResponse;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill.EbillStatus;
import com.backbase.billpay.fiserv.payeessummary.model.EbillFilter;
import com.backbase.billpay.fiserv.payeessummary.model.EbillListRequest;
import com.backbase.billpay.fiserv.payeessummary.model.EbillListResponse;
import com.backbase.billpay.fiserv.payments.model.Payee;
import com.backbase.billpay.fiserv.utils.AbstractWebServiceTest;
import com.backbase.billpay.fiserv.utils.FiservUtils;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.BillPayEbillsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.Ebill;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutResponseBody;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EbillsListenerTest extends AbstractWebServiceTest {

    private static final String USD = "USD";
    private static final String EBILL_ID = "75361";

    @Autowired
    private EbillsListener listener;

    @Test
    public void deleteBillPayEbills() {
        // create the mock response
        EbillAccountCancelResponse fiservResponse =
                        EbillAccountCancelResponse.builder().result(createSuccessResult()).build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // call the listener method
        listener.deleteBillPayEbills(createRequestWrapper(null), null, PAYEE_ID, SUBSCRIBER_ID);

        // validate the request data
        EbillAccountCancelRequest cancelRequest = retrieveRequest(EbillAccountCancelRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(cancelRequest.getPayeeId()));

        // validate the request header
        assertHeader(SUBSCRIBER_ID, cancelRequest.getHeader());
    }

    @Test
    public void getBillPayEbills_MandatoryParameters() {
        // create the mock response
        EbillListResponse fiservResponse = createEbillListResponse();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // call the listener method
        String status = "ALL";
        BillPayEbillsGetResponseBody response = listener.getBillPayEbills(createRequestWrapper(null), null, PAYEE_ID,
                        SUBSCRIBER_ID, status, null, null, null, null, null, null).getRequest().getData();

        // validate the response
        assertEquals(Long.valueOf(fiservResponse.getEbillList().size()), response.getTotalCount());
        assertEquals(fiservResponse.getEbillList().size(), response.getEbills().size());
        for (int i = 0; i < fiservResponse.getEbillList().size(); i++) {
            assertEbill(fiservResponse.getEbillList().get(i), response.getEbills().get(i));
        }

        // validate the request data
        EbillListRequest ebillRequest = retrieveRequest(EbillListRequest.class);
        assertNull(ebillRequest.getFilter());

        // validate the request header
        assertHeader(SUBSCRIBER_ID, ebillRequest.getHeader());
    }

    @Test
    public void getBillPayEbills_AllParameters() {
        // create the mock response
        EbillListResponse fiservResponse = createEbillListResponse();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // call the listener method
        String status = "PAID";
        Date startDate = DateUtils.addDays(new Date(), -10);
        Date endDate = DateUtils.addDays(new Date(), 10);
        Integer from = 2;
        Integer size = 1;
        String orderBy = "paymentDate";
        String direction = "ASC";
        BillPayEbillsGetResponseBody response =
                        listener.getBillPayEbills(createRequestWrapper(null), null, PAYEE_ID, SUBSCRIBER_ID, status,
                                        startDate, endDate, from, size, orderBy, direction).getRequest().getData();

        // validate the response
        assertEquals(Long.valueOf(5), response.getTotalCount());
        assertEquals(1, response.getEbills().size());
        Ebill ebill = response.getEbills().get(0);
        assertEbill(fiservResponse.getEbillList().get(3), ebill);
        assertEquals("PAID", ebill.getStatus());
        assertTrue(ebill.getPaymentDate().after(startDate));
        assertTrue(ebill.getPaymentDate().before(endDate));

        // validate the request data
        EbillListRequest ebillRequest = retrieveRequest(EbillListRequest.class);
        EbillFilter filter = ebillRequest.getFilter();
        assertEquals(FiservUtils.toFiservDate(startDate).getDate(), filter.getStartingDate().getDate());
        assertEquals(Integer.valueOf(20), filter.getNumberOfDays());

        // validate the request header
        assertHeader(SUBSCRIBER_ID, ebillRequest.getHeader());
    }

    @Test
    public void getBillPayEbills_NoEbills() {
        // create the mock response
        EbillListResponse fiservResponse = EbillListResponse.builder().result(createSuccessResult()).build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // call the listener method
        String status = "UNPAID";
        Date startDate = new Date();
        BillPayEbillsGetResponseBody response = listener.getBillPayEbills(createRequestWrapper(null), null, PAYEE_ID,
                        SUBSCRIBER_ID, status, startDate, null, null, null, null, null).getRequest().getData();

        // validate the response
        assertEquals(Long.valueOf(0), response.getTotalCount());
        assertTrue(response.getEbills().isEmpty());

        // validate the request data
        EbillListRequest ebillRequest = retrieveRequest(EbillListRequest.class);
        assertEquals(FiservUtils.toFiservDate(startDate).getDate(),
                        ebillRequest.getFilter().getStartingDate().getDate());
        assertEquals(Integer.valueOf(360), ebillRequest.getFilter().getNumberOfDays());
    }

    @Test
    public void getBillPayEbills_NoEbillsAfterFilter() {
        // create the mock response
        EbillListResponse fiservResponse = EbillListResponse.builder().ebillList(Arrays.asList(createEbill(1)))
                        .result(createSuccessResult()).build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // call the listener method
        String status = "UNPAID";
        Date endDate = new Date();
        BillPayEbillsGetResponseBody response = listener.getBillPayEbills(createRequestWrapper(null), null, PAYEE_ID,
                        SUBSCRIBER_ID, status, null, endDate, null, null, null, null).getRequest().getData();

        // validate the response
        assertEquals(Long.valueOf(0), response.getTotalCount());
        assertTrue(response.getEbills().isEmpty());

        // validate the request data
        EbillListRequest ebillRequest = retrieveRequest(EbillListRequest.class);
        assertEquals(FiservUtils.toFiservDate(endDate).getDate(), ebillRequest.getFilter().getStartingDate().getDate());
        assertEquals(Integer.valueOf(-10000), ebillRequest.getFilter().getNumberOfDays());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getBillPayEbillStatements() {
        listener.getBillPayEbillStatements(createRequestWrapper(null), null, PAYEE_ID, EBILL_ID, SUBSCRIBER_ID);
    }

    @Test
    public void putEbillById() {
        // create the mock response
        EbillFileResponse fiservResponse = EbillFileResponse.builder().result(createSuccessResult()).build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // create the request
        EbillByIdPutRequestBody request = new EbillByIdPutRequestBody().withSubscriberID(SUBSCRIBER_ID).withPaid(true)
                        .withPaymentMethod("CHECK").withNote("Note");

        // call the listener method
        EbillByIdPutResponseBody response = listener
                        .putEbillById(createRequestWrapper(request), null, PAYEE_ID, EBILL_ID).getRequest().getData();

        // validate the response
        assertEquals(EBILL_ID, response.getId());

        // validate the request data
        EbillFileRequest fileRequest = retrieveRequest(EbillFileRequest.class);
        assertEquals(EBILL_ID, fileRequest.getEbillId());
        assertEquals(FileEbillPaymentMethod.CHECK, fileRequest.getFiledBillReason());
        assertEquals(request.getNote(), fileRequest.getBillNote());

        // validate the request header
        assertHeader(SUBSCRIBER_ID, fileRequest.getHeader());
    }

    private void assertEbill(com.backbase.billpay.fiserv.payeessummary.model.Ebill expectedEbill, Ebill actualEbill) {
        assertEquals(expectedEbill.getEbillId(), actualEbill.getId());
        assertEquals(String.valueOf(expectedEbill.getPayee().getPayeeId()), actualEbill.getPayeeID());
        assertEquals(expectedEbill.getDueDate(), actualEbill.getPaymentDate());
        assertEquals(expectedEbill.getAmountDue(), actualEbill.getAmount().getAmount());
        assertEquals(USD, actualEbill.getAmount().getCurrencyCode());
        assertEquals(expectedEbill.getBalance(), actualEbill.getOutstandingBalance().getAmount());
        assertEquals(USD, actualEbill.getOutstandingBalance().getCurrencyCode());
        assertEquals(expectedEbill.getMinimumAmountDue(), actualEbill.getMinAmountDue().getAmount());
        assertEquals(USD, actualEbill.getMinAmountDue().getCurrencyCode());
        assertEquals(toEbillStatus(expectedEbill.getStatus()), actualEbill.getStatus());
        assertEquals(expectedEbill.getBillReferenceLinkUrl(), actualEbill.getUrl());
        assertFalse(actualEbill.getStatementAvailable());
    }

    private EbillListResponse createEbillListResponse() {
        List<com.backbase.billpay.fiserv.payeessummary.model.Ebill> ebills = new ArrayList<>();

        com.backbase.billpay.fiserv.payeessummary.model.Ebill ebill1 = createEbill(1);
        ebill1.setDueDate(DateUtils.addDays(new Date(), 10));
        ebills.add(ebill1);

        com.backbase.billpay.fiserv.payeessummary.model.Ebill ebill2 = createEbill(2);
        ebill2.setDueDate(DateUtils.addDays(new Date(), 7));
        ebill2.setStatus(EbillStatus.UNPAID);
        ebills.add(ebill2);

        com.backbase.billpay.fiserv.payeessummary.model.Ebill ebill3 = createEbill(3);
        ebill3.setDueDate(DateUtils.addDays(new Date(), 5));
        ebills.add(ebill3);

        com.backbase.billpay.fiserv.payeessummary.model.Ebill ebill4 = createEbill(4);
        ebill4.setStatus(EbillStatus.FILED);
        ebills.add(ebill4);

        com.backbase.billpay.fiserv.payeessummary.model.Ebill ebill5 = createEbill(5);
        ebill5.setStatus(EbillStatus.PAYMENT_CANCELLED);
        ebills.add(ebill5);

        com.backbase.billpay.fiserv.payeessummary.model.Ebill ebill6 = createEbill(6);
        ebill6.setDueDate(DateUtils.addDays(new Date(), -5));
        ebills.add(ebill6);

        com.backbase.billpay.fiserv.payeessummary.model.Ebill ebill7 = createEbill(7);
        ebill7.setDueDate(DateUtils.addDays(new Date(), -9));
        ebills.add(ebill7);

        com.backbase.billpay.fiserv.payeessummary.model.Ebill ebill8 = createEbill(8);
        ebill8.setDueDate(DateUtils.addDays(new Date(), -10));
        ebill8.setStatus(EbillStatus.PAYMENT_FAILED);
        ebills.add(ebill8);

        return EbillListResponse.builder().ebillList(ebills).result(createSuccessResult()).build();
    }

    private com.backbase.billpay.fiserv.payeessummary.model.Ebill createEbill(int id) {
        return com.backbase.billpay.fiserv.payeessummary.model.Ebill.builder().ebillId("ebill" + id)
                        .payee(Payee.builder().payeeId(Long.valueOf(PAYEE_ID)).build()).dueDate(new Date())
                        .amountDue(new BigDecimal(30)).balance(new BigDecimal(500)).minimumAmountDue(new BigDecimal(5))
                        .status(EbillStatus.PAID).billReferenceLinkUrl("www.bill.com").build();
    }

    private String toEbillStatus(EbillStatus status) {
        switch (status) {
            case PAID:
            case FILED:
                return "PAID";
            case UNPAID:
                return "UNPAID";
            case PAYMENT_CANCELLED:
                return "PAYMENT_CANCELLED";
            case PAYMENT_FAILED:
                return "PAYMENT_FAILED";
            default:
                fail("Unexpected status " + status + " returned");
                return null;
        }
    }

}
