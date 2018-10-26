package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@XmlRootElement(name = "PaymentCancelRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentCancelRequest extends AbstractRequest {
    
    @XmlElement(name = "PaymentId")
    private String paymentId;
    
    @Builder
    public PaymentCancelRequest(String paymentId, Header header) {
        super(header);
        this.paymentId = paymentId;
    }
 
}
