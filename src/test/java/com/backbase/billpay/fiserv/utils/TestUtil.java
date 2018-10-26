package com.backbase.billpay.fiserv.utils;

import com.backbase.billpay.fiserv.common.model.ResultInfo;
import com.backbase.billpay.fiserv.common.model.ResultType;
import java.io.StringWriter;
import java.util.Collections;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class TestUtil {

    /**
     * Create a result type with the specified error.
     * @param errorCode Fiserv error code
     * @return
     */
    public static ResultType createErrorResult(String errorCode) {
        return ResultType.builder()
                         .success(false)
                         .resultInfo(Collections.singletonList(ResultInfo.builder()
                                                                         .code(errorCode)
                                                                         .build()))
                         .build();
    }
    
    /**
     * Creates a SOAP fault error as a string.
     * @return
     */
    public static String createSoapFaultError() {
        String soapError = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                           + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                           + "   <soap:Body>\n"
                           + "      <soap:Fault>\n"
                           + "         <soap:faultcode>soap:Client</soap:faultcode>\n"
                           + "         <soap:faultstring>No service was found matching the request</soap:faultstring>\n"
                           + "      </soap:Fault>\n"
                           + "   </soap:Body>\n"
                           + "</soap:Envelope>";
        return soapError;
    }
    
    /**
     * Creates a SOAP response with the response object as the SOAP body.
     * @param response the object to be used as the SOAP body
     * @return
     */
    public static String createSoapResponseWithObject(Object response) {
        try {
            Marshaller marshaller = JAXBContext.newInstance(response.getClass()).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            StringWriter sw = new StringWriter();
            marshaller.marshal(response, sw);
            String soapMessage =  "<?xml version=\"1.0\" ?>"
                                  + "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body>"
                                  + sw.toString()
                                  + "</S:Body></S:Envelope>";
            return soapMessage;
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
