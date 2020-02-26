package com.backbase.billpay.fiserv.enrolment;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.enrolment.model.IndividualName;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberEnrollRequest;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberEnrollResponse;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberInfoRequest;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberInfoResponse;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberInformation;
import com.backbase.billpay.fiserv.payees.model.UsAddress;
import com.backbase.billpay.fiserv.utils.AbstractWebServiceTest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.BillPayEnrolmentPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.BillPayEnrolmentPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enroluser.UserByIdGetResponseBody;
import org.junit.Test;
import org.springframework.http.MediaType;

public class EnrolmentControllerTest extends AbstractWebServiceTest {
    
    private static final String TAX_ID = "453110230";
    private static final String EMAIL = "test@test.com";
    private static final String DAY_PHONE = "6783753000";
    private static final String ZIP = "90200";
    private static final String STATE = "CA";
    private static final String CITY = "Los Angeles";
    private static final String ADDRESS_2 = "Downtown";
    private static final String ADDRESS_1 = "1 1st Avenue";
    private static final String PREFIX = "Mr";
    private static final String LAST = "Doe";
    private static final String FIRST = "John";
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
    
    @Test
    public void getUserById() throws Exception {
        
        // create the mock response
        SubscriberInfoResponse fiservResponse = SubscriberInfoResponse.builder()
            .result(ResultType.builder()
                .success(true)
                .build())
            .subscriberInformation(SubscriberInformation.builder()
                .usAddress(UsAddress.builder()
                    .address1(ADDRESS_1)
                    .address2(ADDRESS_2)
                    .city(CITY)
                    .state(STATE)
                    .zip5(ZIP)
                    .build())
                .dayPhone(DAY_PHONE)
                .emailAddress(EMAIL)
                .name(IndividualName.builder()
                    .first(FIRST)
                    .last(LAST)
                    .prefix(PREFIX)
                    .build())
                .taxId(TAX_ID)
                .build())    
            .build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);
        
        // call the endpoint
        String stringResponse = mockMvc.perform(get(URL + "/" + SUBSCRIBER_ID)
                        .with(setRemoteAddress())
                        .param("subscriberID", SUBSCRIBER_ID))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        
        UserByIdGetResponseBody response =
                        objectMapper.readValue(stringResponse, UserByIdGetResponseBody.class);

        // validate the subscriber returned matches
        assertEquals(FIRST, response.getSubscriber().getFirstName());

        // validate the request
        SubscriberInfoRequest request = retrieveRequest(SubscriberInfoRequest.class);
        assertHeader(SUBSCRIBER_ID, request.getHeader());
    }

}
