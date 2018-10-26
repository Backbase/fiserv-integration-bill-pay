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

@NoArgsConstructor
@XmlRootElement(name = "PaymentDetailRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class PaymentDetailRequest extends AbstractRequest {
    
    @XmlElement(name = "PaymentId")
    private String paymentId;
    
    @Builder
    public PaymentDetailRequest(String paymentId, Header header) {
        super(header);
        this.paymentId = paymentId;
    }

}
