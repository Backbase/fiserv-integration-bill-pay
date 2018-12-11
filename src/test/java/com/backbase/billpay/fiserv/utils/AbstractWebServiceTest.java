package com.backbase.billpay.fiserv.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.XPathBody.xpath;

import com.backbase.billpay.fiserv.Application;
import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.buildingblocks.backend.communication.event.proxy.RequestProxyWrapper;
import com.backbase.buildingblocks.backend.internalrequest.DefaultInternalRequestContext;
import com.backbase.buildingblocks.backend.internalrequest.InternalRequest;
import com.google.common.net.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("it")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = TestApplicationContextInitializer.class)
@SpringBootTest(classes = Application.class)
public abstract class AbstractWebServiceTest {

    public static String SUBSCRIBER_ID = "subscriber1";
    public  static final String PAYEE_ID = "123456";
    public static String USER_ID = "user1";
    public static final String TEST_IP_ADDRESS = "192.168.0.1";
    private static final String PATH = "/fiserv";
    private static final String METHOD_POST = "POST";
    private static final int PORT = 1080;
    private static final String HOST = "localhost";
    private ClientAndServer mockServer;

    public AbstractWebServiceTest() {
        super();
    }

    @Before
    public void startServer() {
        mockServer = startClientAndServer(1080);
    }

    @After
    public void stopServer() { 
        mockServer.stop();
    }

    protected void setupWebServiceResponse(Object response) {
        new MockServerClient(HOST, PORT)
               .when(
                    request()
                        .withMethod(METHOD_POST)
                        .withPath(PATH),
                    Times.once())
               .respond(
                    response()
                        .withStatusCode(200)
                        .withBody(TestUtil.createSoapResponseWithObject(response), MediaType.XML_UTF_8));
    }
    
    protected void setupWebServiceError() {
        new MockServerClient(HOST, PORT)
               .when(
                    request()
                        .withMethod(METHOD_POST)
                        .withPath(PATH),
                    Times.once())
               .respond(
                    response()
                        .withStatusCode(500)
                        .withBody(TestUtil.createSoapFaultError(), MediaType.XML_UTF_8));
    }

    protected void verifyRequestWithXpaths(String xpath) throws AssertionError {
        new MockServerClient(HOST, 1080)
            .verify(
                request()
                    .withPath(PATH)
                    .withMethod(METHOD_POST)
                    .withBody(xpath(xpath)),
                VerificationTimes.once());
    }
    
    protected <T> T retrieveRequest(Class<T> requestType) {
        return retrieveSpecificRequest(0, requestType);
    }
    
    protected <T> T retrieveSpecificRequest(int requestNumber, Class<T> requestType) {
        HttpRequest[] requests = mockServer.retrieveRecordedRequests(null);
        HttpRequest request = requests[requestNumber];
        String requestString = request.getBodyAsString();
        return convertMessageStringToObject(requestString, requestType);
    }
    
    @SuppressWarnings("unchecked")
    protected <T> T convertMessageStringToObject(String message, Class<T> objectType) {
        try {
            SOAPMessage soapMessage = MessageFactory.newInstance()
                            .createMessage(null, new ByteArrayInputStream(message.getBytes()));
            Unmarshaller unmarshaller = JAXBContext.newInstance(objectType).createUnmarshaller();
            return (T) unmarshaller.unmarshal(soapMessage.getSOAPBody().extractContentAsDocument());
        } catch (IOException | SOAPException | JAXBException exception) {
            throw new IllegalArgumentException("Cannot convert soap message to object due to: " + exception);
        }
    }
    
    protected void assertHeader(String subscriberId, Header header) {
        assertEquals(subscriberId, header.getSubscriberId());
        assertEquals(TEST_IP_ADDRESS, header.getSubscriberIpAddress());
        assertEquals("billpay", header.getClientAppText());
        assertEquals("1.0", header.getClientAppVersion());
        assertEquals("BankA", header.getSponsorId());
        assertTrue(header.getCorrelationId().length() > 0);
    }
    
    protected <T> RequestProxyWrapper<T> createRequestWrapper(T data) {
        InternalRequest<T> internalRequest = createInternalRequest(data);
        RequestProxyWrapper<T> requestWrapper = new RequestProxyWrapper<>();
        requestWrapper.setRequest(internalRequest);
        return requestWrapper;
    }
    
    private <T> InternalRequest<T> createInternalRequest(T bodyData) {
        InternalRequest<T> request = new InternalRequest<>();
        request.setData(bodyData);
        DefaultInternalRequestContext irc = new DefaultInternalRequestContext();
        irc.setRemoteAddress(TEST_IP_ADDRESS);
        request.setInternalRequestContext(irc);
        return request;
    }
    
    protected ResultType createSuccessResult() {
        return ResultType.builder()
                         .success(true)
                         .build();
    }
    
    protected BldrDate createBldrDate(String date) {
        return BldrDate.builder()
                       .date(date)
                       .build();
    }
}
