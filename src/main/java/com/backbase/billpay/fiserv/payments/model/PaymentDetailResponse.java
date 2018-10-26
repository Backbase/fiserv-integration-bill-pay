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

@NoArgsConstructor
@XmlRootElement(name = "PaymentDetailResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@AllArgsConstructor
public class PaymentDetailResponse extends AbstractResponse {
    
    @XmlElement(name = "PaymentDetailResult")
    private List<PaymentDetail> paymentDetailResult;

    @Builder
    public PaymentDetailResponse(List<PaymentDetail> paymentDetailResult, ResultType result) {
        super(result);
        this.paymentDetailResult = paymentDetailResult;
    }
}
