package com.backbase.billpay.fiserv.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@ActiveProfiles({"it", "http"})
@AutoConfigureMockMvc
public abstract class AbstractHTTPWebServiceTest extends AbstractWebServiceTest {
    
    @Autowired
    protected MockMvc mockMvc;
    
    protected ObjectMapper objectMapper;
    
    @Before
    public void setup() {
        this.objectMapper = new ObjectMapper();
    }
    
    protected RequestPostProcessor setRemoteAddress() {
        return request -> {
            request.setRemoteAddr(TEST_IP_ADDRESS);
            return request;
        };
    }

}
