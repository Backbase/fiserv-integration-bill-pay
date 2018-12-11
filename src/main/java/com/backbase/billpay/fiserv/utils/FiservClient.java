package com.backbase.billpay.fiserv.utils;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.common.model.SessionInitiateRequest;
import com.backbase.billpay.fiserv.common.model.SessionInitiateResponse;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.Error;
import java.util.Collections;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.client.core.SoapActionCallback;

@Component
public class FiservClient extends WebServiceGatewaySupport {

    private static final Logger LOG = LoggerFactory.getLogger(FiservClient.class);
    
    public static final Error PROVIDER_UNAVAILABLE_ERROR = new Error().withKey("billpay.api.providerUnavailable")
                    .withMessage("The bill pay provider is not currently available");
    public static final BadRequestException PROVIDER_UNAVAILABLE_EXCEPTION = new BadRequestException()
                    .withMessage("Bad request")
                    .withErrors(Collections.singletonList(PROVIDER_UNAVAILABLE_ERROR));
    
    @Value("${backbase.billpay.provider.url}")
    private String providerUrl;
    
    /**
     * Set up the provider url and marshaller.
     */
    @PostConstruct
    public void setup() {
        setDefaultUri(providerUrl);
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.backbase.billpay.fiserv");
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
    }

    /**
     * Calls the Fiserv Web Service marshaling the given request to the specified SOAP action.
     * @param request the request object to mrshall
     * @param soapAction the soap action
     * @return
     */
    public <T extends AbstractResponse> T call(AbstractRequest request, String soapAction) {
        
        LOG.debug("Request: {} for soap action {}", request, soapAction);
        try {
            @SuppressWarnings("unchecked")
            T response = (T) getWebServiceTemplate()
                               .marshalSendAndReceive(request, new SoapActionCallback(providerUrl + "/" + soapAction));
            LOG.debug("Response: {} for soap action {}", response, soapAction);
            response.getResult().checkForErrors();
            return response;
        } catch (ResourceAccessException e) {
            throw PROVIDER_UNAVAILABLE_EXCEPTION;
        } catch (SoapFaultClientException e) {
            throw ResultType.GENERAL_EXCEPTION;
        }
    }

    /**
     * Some APIs require a session to be active (payments for example).  If an IP address is passed attempt
     * to create a session, otherwise don't.
     * Typically the session integration code is integrated with a caching solution which is not in scope for this demo.
     *    String sessionId = initiateSession(request);
     *    request.getHeader().setSessionCorrelationId(sessionId);
     */
    public String initiateSession(AbstractRequest request) {
        if (request.getHeader().getSubscriberIpAddress() == null) {
            return null;
        } else {
            SessionInitiateRequest initiateRequest = SessionInitiateRequest.builder()
                                                                           .ipAddress(request.getHeader()
                                                                                             .getSubscriberIpAddress())
                                                                           .build();
            SessionInitiateResponse initiateResponse = (SessionInitiateResponse) getWebServiceTemplate()
                            .marshalSendAndReceive(initiateRequest, 
                                                   new SoapActionCallback(providerUrl + "/" + "SessionInitiate"));
            if (initiateResponse.getResult().isSuccess()) {
                return initiateResponse.getSessionCorrelationId();
            } else {
                throw ResultType.GENERAL_EXCEPTION;
            }
        }
    }
}