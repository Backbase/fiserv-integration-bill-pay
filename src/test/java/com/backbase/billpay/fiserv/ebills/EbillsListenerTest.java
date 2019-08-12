package com.backbase.billpay.fiserv.ebills;

import static org.junit.Assert.assertEquals;

import com.backbase.billpay.fiserv.ebills.model.EbillAccountCancelRequest;
import com.backbase.billpay.fiserv.ebills.model.EbillAccountCancelResponse;
import com.backbase.billpay.fiserv.utils.AbstractWebServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EbillsListenerTest extends AbstractWebServiceTest {

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

}
