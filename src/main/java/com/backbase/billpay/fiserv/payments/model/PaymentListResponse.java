package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "PaymentListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentListResponse extends AbstractResponse {
    
    @XmlElement(name = "Payments")
    private List<Payment> payments;
    
    @Builder
    public PaymentListResponse(List<Payment> payments, ResultType result) {
        super(result);
        this.payments = payments;
    }

}
