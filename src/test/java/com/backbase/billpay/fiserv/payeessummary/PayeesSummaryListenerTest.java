package com.backbase.billpay.fiserv.payeessummary;

import static org.junit.Assert.assertNotNull;
import com.backbase.billpay.fiserv.utils.AbstractWebServiceTest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.BillPayPayeesSummaryGetResponseBody;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PayeesSummaryListenerTest extends AbstractWebServiceTest {
    
    @Autowired
    private PayeesSummaryListener listener;
    
    @Test
    public void getBillPayPayeesSummary() {
        BillPayPayeesSummaryGetResponseBody response =
                        listener.getBillPayPayeesSummary(createRequestWrapper(null), null, SUBSCRIBER_ID)
                                .getRequest().getData();
        assertNotNull(response);
    }
    
}