package com.backbase.billpay.fiserv.payments.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentAddDetail {
    
    @XmlElement(name = "ConfirmationNumber")
    private String confirmationNumber;
    
    @XmlElement(name = "PaymentId")
    private String paymentId;

}
