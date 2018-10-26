package com.backbase.billpay.fiserv.utils;

import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.search.model.PayeeSearchRequest;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

public class FiservClientTest extends AbstractWebServiceTest {
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Autowired
    private FiservClient client;

    @Test
    public void call_soapError() {
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage(ResultType.BAD_REQUEST);
        
        setupWebServiceError();
        client.call(PayeeSearchRequest.builder().build(), "test");
    }
}
