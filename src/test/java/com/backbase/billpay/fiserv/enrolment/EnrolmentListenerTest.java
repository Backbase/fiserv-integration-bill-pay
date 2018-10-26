package com.backbase.billpay.fiserv.enrolment;

import static org.junit.Assert.assertEquals;

import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberEnrollRequest;
import com.backbase.billpay.fiserv.enrolment.model.SubscriberEnrollResponse;
import com.backbase.billpay.fiserv.utils.AbstractWebServiceTest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.BillPayEnrolmentPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.enrolment.BillPayEnrolmentPostResponseBody;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test class for the enrolment listener.
 */
public class EnrolmentListenerTest extends AbstractWebServiceTest {
    
    @Autowired
    private EnrolmentListener listener;

    @Test
    public void postBillPayEnrolment() {
        
        // create the mock response
        SubscriberEnrollResponse fiservResponse = SubscriberEnrollResponse.builder()
                                                                          .result(ResultType.builder()
                                                                                            .success(true)
                                                                                            .build())
                                                                          .build();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);
        
        // call the enrolment
        RequestProxyWrapper<BillPayEnrolmentPostRequestBody> wrapperRequest = 
                        createRequestWrapper(new BillPayEnrolmentPostRequestBody()
                                                    .withUser(USER_ID));

        BillPayEnrolmentPostResponseBody response = listener.postBillPayEnrolment(wrapperRequest, null)
                                                            .getRequest()
                                                            .getData();

        // validate the subscriber returned matches the passed ID
        assertEquals("UniqueCustomerNumber", response.getId());

        // validate the request
        SubscriberEnrollRequest request = retrieveRequest(SubscriberEnrollRequest.class);
        assertHeader("UniqueCustomerNumber", request.getHeader());
    }
}