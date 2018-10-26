package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@XmlRootElement(name = "ExpeditedPaymentAddResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExpeditedPaymentAddResponse extends AbstractResponse {
    
    @XmlElement(name = "PaymentDetail")
    private PaymentAddDetail paymentDetail;
    
    @Builder
    public ExpeditedPaymentAddResponse(PaymentAddDetail paymentDetail, ResultType result) {
        super(result);
        this.paymentDetail = paymentDetail;
    }

}
