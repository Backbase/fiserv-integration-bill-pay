package com.backbase.billpay.fiserv.ebills;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.backbase.billpay.fiserv.utils.AbstractHTTPWebServiceTest;
import com.backbase.billpay.fiserv.utils.FiservUtils;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.BillPayEbillsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.Ebill;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutResponseBody;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

public class EbillsControllerTest extends AbstractHTTPWebServiceTest {
    
    private static final String URL = "/service-api/v2/bill-pay/payees/electronic/{id}/ebills";
    private static final String STATEMENTS_ENDPOINT = "/statements";
    private static final String ID_ENDPOINT = "/{ebillId}";
    private static final String USD = "USD";
    private static final String EBILL_ID = "75361";
    
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    
    @Test
    public void deleteBillPayEbills() throws Exception {
        // create the mock response
        EbillAccountCancelResponse fiservResponse =
                        EbillAccountCancelResponse.builder().result(createSuccessResult()).build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // call the endpoint
        mockMvc.perform(delete(URL, PAYEE_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isNoContent());

        // validate the request data
        EbillAccountCancelRequest cancelRequest = retrieveRequest(EbillAccountCancelRequest.class);
        assertEquals(PAYEE_ID, String.valueOf(cancelRequest.getPayeeId()));

        // validate the request header
        assertHeader(SUBSCRIBER_ID, cancelRequest.getHeader());
    }

    @Test
    public void getBillPayEbills_MandatoryParameters() throws Exception {
        // create the mock response
        EbillListResponse fiservResponse = createEbillListResponse();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // call the endpoint
        String stringResponse = mockMvc.perform(get(URL, PAYEE_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID)
                        .param("status", "ALL"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        BillPayEbillsGetResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayEbillsGetResponseBody.class);
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
    public void getBillPayEbills_AllParameters() throws Exception {
        // create the mock response
        EbillListResponse fiservResponse = createEbillListResponse();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // call the endpoint
        String status = "PAID";
        Date startDate = DateUtils.addDays(new Date(), -10);
        Date endDate = DateUtils.addDays(new Date(), 10);
        Integer from = 2;
        Integer size = 1;
        String orderBy = "paymentDate";
        String direction = "ASC";
        
        String stringResponse = mockMvc.perform(get(URL, PAYEE_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID)
                        .param("status", status)
                        .param("startDate", formatter.format(startDate))
                        .param("endDate", formatter.format(endDate))
                        .param("from", from.toString())
                        .param("size",size.toString())
                        .param("orderBy", orderBy)
                        .param("direction", direction))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        BillPayEbillsGetResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayEbillsGetResponseBody.class);
        
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
    public void getBillPayEbills_NoEbills() throws Exception {
        // create the mock response
        EbillListResponse fiservResponse = EbillListResponse.builder().result(createSuccessResult()).build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // call the endpoint
        String status = "UNPAID";
        Date startDate = new Date();
        
        String stringResponse = mockMvc.perform(get(URL, PAYEE_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID)
                        .param("status", status)
                        .param("startDate", formatter.format(startDate)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        BillPayEbillsGetResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayEbillsGetResponseBody.class);

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
    public void getBillPayEbills_NoEbillsAfterFilter() throws Exception {
        // create the mock response
        EbillListResponse fiservResponse = EbillListResponse.builder().ebillList(Arrays.asList(createEbill(1)))
                        .result(createSuccessResult()).build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // call the endpoint
        String status = "UNPAID";
        Date endDate = new Date();
        
        String stringResponse = mockMvc.perform(get(URL, PAYEE_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID)
                        .param("status", status)
                        .param("endDate", formatter.format(endDate)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        BillPayEbillsGetResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayEbillsGetResponseBody.class);
        
        // validate the response
        assertEquals(Long.valueOf(0), response.getTotalCount());
        assertTrue(response.getEbills().isEmpty());

        // validate the request data
        EbillListRequest ebillRequest = retrieveRequest(EbillListRequest.class);
        assertEquals(FiservUtils.toFiservDate(endDate).getDate(), ebillRequest.getFilter().getStartingDate().getDate());
        assertEquals(Integer.valueOf(-10000), ebillRequest.getFilter().getNumberOfDays());
    }

    public void getBillPayEbillStatements() throws Exception {
        String stringResponse = mockMvc.perform(get(URL + ID_ENDPOINT + STATEMENTS_ENDPOINT, PAYEE_ID, EBILL_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        UnsupportedOperationException response =
                        objectMapper.readValue(stringResponse, UnsupportedOperationException.class);
        assertNull(response.getMessage());
    }

    @Test
    public void putEbillById() throws Exception {
        // create the mock response
        EbillFileResponse fiservResponse = EbillFileResponse.builder().result(createSuccessResult()).build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // create the request
        EbillByIdPutRequestBody request = new EbillByIdPutRequestBody().withSubscriberID(SUBSCRIBER_ID).withPaid(true)
                        .withPaymentMethod("CHECK").withNote("Note");

        String stringRequest = objectMapper.writeValueAsString(request);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(put(URL + ID_ENDPOINT, PAYEE_ID, EBILL_ID)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        EbillByIdPutResponseBody response =
                        objectMapper.readValue(stringResponse, EbillByIdPutResponseBody.class);

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
