package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.payees.model.PayeeSummary;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@XmlRootElement(name = "PaymentAddResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class PaymentAddResponse extends AbstractResponse {
    
    @XmlElement(name = "Payments")
    private List<StandardAddPaymentDetail> payments;
    
    @Builder
    public PaymentAddResponse(List<StandardAddPaymentDetail> payments, ResultType result) {
        super(result);
        this.payments = payments;
    }

}
