package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@XmlRootElement(name = "PaymentCancelResponse")
public class PaymentCancelResponse extends AbstractResponse {
    
    @Builder
    public PaymentCancelResponse(ResultType result) {
        super(result);
    }

}
