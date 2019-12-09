package com.backbase.billpay.fiserv.enrolment;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberEnrollRequest;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberEnrollResponse;
import com.backbase.billpay.fiserv.utils.AbstractWebServiceTest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.BillPayEnrolmentPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.BillPayEnrolmentPostResponseBody;
import org.junit.Test;
import org.springframework.http.MediaType;

public class EnrolmentControllerTest extends AbstractWebServiceTest {
    
    private static final String URL = "/service-api/v2/bill-pay/enrolment";
    
    @Test
    public void postBillPayEnrolment() throws Exception {
        
        // create the mock response
        SubscriberEnrollResponse fiservResponse = SubscriberEnrollResponse.builder()
                                                                          .result(ResultType.builder()
                                                                                            .success(true)
                                                                                            .build())
                                                                          .build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);
        
        // create the request body
        BillPayEnrolmentPostRequestBody requestBody = new BillPayEnrolmentPostRequestBody()
                                                    .withUser(USER_ID);
        
        String stringRequest = objectMapper.writeValueAsString(requestBody);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(post(URL)
                        .with(setRemoteAddress())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString();
        
        BillPayEnrolmentPostResponseBody response =
                        objectMapper.readValue(stringResponse, BillPayEnrolmentPostResponseBody.class);

        // validate the subscriber returned matches the passed ID
        assertEquals("UniqueCustomerNumber", response.getId());

        // validate the request
        SubscriberEnrollRequest request = retrieveRequest(SubscriberEnrollRequest.class);
        assertHeader("UniqueCustomerNumber", request.getHeader());
    }

}
