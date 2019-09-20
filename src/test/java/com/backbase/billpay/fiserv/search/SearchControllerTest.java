package com.backbase.billpay.fiserv.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.search.model.PayeeSearchRequest;
import com.backbase.billpay.fiserv.search.model.PayeeSearchResponse;
import com.backbase.billpay.fiserv.search.model.PayeeSearchResultInfo;
import com.backbase.billpay.fiserv.utils.AbstractHTTPWebServiceTest;
import com.backbase.billpay.fiserv.utils.TestUtil;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessearch.BillPaySearchGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessearch.Payee;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class SearchControllerTest extends AbstractHTTPWebServiceTest {
    
    private static final String URL = "/service-api/v2/bill-pay/payees-search";
    private static final String PAYEE_SEARCH_NAME = "payee1";
    
    @Test
    public void getBillPayPayeesSearch_Successful() throws Exception {
        
        // create the mock response
        PayeeSearchResultInfo searchResultOne = PayeeSearchResultInfo.builder().onlineName("Payee One")
                        .merchantNumber(52).merchantZipRequired(true).isEbillUpsellable(false).isExactMatch(true)
                        .displayName("Payee One Name").build();
        PayeeSearchResultInfo searchResultTwo = PayeeSearchResultInfo.builder().onlineName("Payee Two")
                        .merchantNumber(34).merchantZipRequired(false).isEbillUpsellable(true).isExactMatch(false)
                        .displayName("Payee Two Name").build();
        List<PayeeSearchResultInfo> payeeResults = Arrays.asList(searchResultOne, searchResultTwo);
        PayeeSearchResponse searchResponse = PayeeSearchResponse.builder()
                        .result(ResultType.builder().success(true).build()).payeeResult(payeeResults).build();

        // set up mock server
        setupWebServiceResponse(searchResponse);

        // call the endpoint
        String stringResponse = mockMvc.perform(get(URL)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID)
                        .param("name", PAYEE_SEARCH_NAME))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        BillPaySearchGetResponseBody response =
                        objectMapper.readValue(stringResponse, BillPaySearchGetResponseBody.class);

        // validate the results
        Payee payee = response.getPayees().get(0);
        assertEquals(String.valueOf(searchResultOne.getMerchantNumber()), payee.getMerchantID());
        assertEquals(searchResultOne.getOnlineName(), payee.getName());
        assertEquals(searchResultOne.getMerchantZipRequired(), payee.getZipRequired());
        assertNull(payee.getPhoneNumber());
        assertNull(payee.getAddress());
        Payee payeeTwo = response.getPayees().get(1);
        assertEquals(String.valueOf(searchResultTwo.getMerchantNumber()), payeeTwo.getMerchantID());
        assertEquals(searchResultTwo.getOnlineName(), payeeTwo.getName());
        assertEquals(searchResultTwo.getMerchantZipRequired(), payeeTwo.getZipRequired());
        assertNull(payeeTwo.getPhoneNumber());
        assertNull(payeeTwo.getAddress());

        // validate the request
        PayeeSearchRequest searchRequest = retrieveRequest(PayeeSearchRequest.class);
        assertEquals(PAYEE_SEARCH_NAME, searchRequest.getName());
        
        // validate the header
        assertHeader(SUBSCRIBER_ID, searchRequest.getHeader());
    }

    public void getBillPayPayeesSearch_Fail() throws Exception {
        
        // create a Fiserv error
        PayeeSearchResponse response = PayeeSearchResponse
                                            .builder()
                                            .result(TestUtil.createErrorResult("100"))
                                            .build();
        
        // set up mock server
        setupWebServiceResponse(response);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(get(URL)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID)
                        .param("name", PAYEE_SEARCH_NAME))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        // call the listener method
        BadRequestException exception =
                        objectMapper.readValue(stringResponse, BadRequestException.class);
        assertNull(exception.getMessage());
    }

}
