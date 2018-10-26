package com.backbase.billpay.fiserv.payeessummary;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.BillPayPayeesSummaryGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.PayeeSummary;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import com.backbase.buildingblocks.backend.internalrequest.InternalRequest;
import java.util.List;
import org.apache.camel.Exchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PayeesSummaryListenerTest {
    
    private static final String ID = "1000001";
    private static final String NICKNAME = "Account nickname";
    
    @InjectMocks
    private PayeesSummaryListener listener = new PayeesSummaryListener();
    
    @Mock
    private PayeesSummaryService service;
    
    @Test
    public void getBillPayPayeesSummary_ShouldDelegateToService() {
        BillPayPayeesSummaryGetResponseBody payeesSummaries = createBillPayPayeesSummaryGetResponseBody();
        when(service.getBillPayPayeesSummary(anyString())).thenReturn(payeesSummaries);
        
        Exchange exchange = mock(Exchange.class);
        RequestProxyWrapper<Void> wrapper = new RequestProxyWrapper<>();
        wrapper.setHttpMethod("GET");
        wrapper.setRequest(mock(InternalRequest.class));
        
        RequestProxyWrapper<BillPayPayeesSummaryGetResponseBody> responseBody = 
                        listener.getBillPayPayeesSummary(wrapper, exchange, ID);
        
        verify(service).getBillPayPayeesSummary(eq(ID));
        List<PayeeSummary> expSummaries = payeesSummaries.getPayeeSummaries();
        List<PayeeSummary> actSummaries = responseBody.getRequest().getData().getPayeeSummaries();
        assertEquals(expSummaries.size(), actSummaries.size());
        assertEquals(expSummaries.get(0).getNickName(), actSummaries.get(0).getNickName());
        assertEquals(expSummaries.get(0).getElectronic(), actSummaries.get(0).getElectronic());
    }
    
    private BillPayPayeesSummaryGetResponseBody createBillPayPayeesSummaryGetResponseBody() {
        BillPayPayeesSummaryGetResponseBody payeesSummaries = new BillPayPayeesSummaryGetResponseBody();
        PayeeSummary payeeSummary = new PayeeSummary().withId(ID).withNickName(NICKNAME).withElectronic(true);
        payeesSummaries.getPayeeSummaries().add(payeeSummary);
        return payeesSummaries;
    }
}