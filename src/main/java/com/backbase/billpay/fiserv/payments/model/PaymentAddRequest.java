package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlRootElement(name = "PaymentAddRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentAddRequest extends AbstractRequest {
    
    @XmlElement(name = "PaymentList")
    private List<StandardPaymentAddInfo> paymentList;
    
    @Builder
    public PaymentAddRequest(List<StandardPaymentAddInfo> paymentList, Header header) {
        super(header);
        this.paymentList = paymentList;
    }
}