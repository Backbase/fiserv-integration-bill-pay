package com.backbase.billpay.fiserv.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.search.model.PayeeSearchRequest;
import com.backbase.billpay.fiserv.search.model.PayeeSearchResponse;
import com.backbase.billpay.fiserv.search.model.PayeeSearchResultInfo;
import com.backbase.billpay.fiserv.utils.AbstractWebServiceTest;
import com.backbase.billpay.fiserv.utils.TestUtil;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessearch.BillPaySearchGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessearch.Payee;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchListenerTest extends AbstractWebServiceTest {

    private static final String PAYEE_SEARCH_NAME = "payee1";
    
    @Autowired
    private SearchListener listener;

    @Test
    public void getBillPayPayeesSearch_Successful() {
        
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

        // call the search
        BillPaySearchGetResponseBody response = listener.getBillPaySearch(createRequestWrapper(null), null,
                        SUBSCRIBER_ID, PAYEE_SEARCH_NAME, null, null).getRequest().getData();

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

    @Test(expected = BadRequestException.class)
    public void getBillPayPayeesSearch_Fail() {
        
        // create a Fiserv error
        PayeeSearchResponse response = PayeeSearchResponse
                                            .builder()
                                            .result(TestUtil.createErrorResult("100"))
                                            .build();
        
        // set up mock server
        setupWebServiceResponse(response);
        
        // call the service
        listener.getBillPaySearch(createRequestWrapper(null), null, SUBSCRIBER_ID, PAYEE_SEARCH_NAME, null, null);
    }
}