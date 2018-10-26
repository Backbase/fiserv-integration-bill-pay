package com.backbase.billpay.fiserv.payees.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@XmlRootElement(name = "PayeeCancelResponse")
public class PayeeCancelResponse extends AbstractResponse {
    
    @Builder
    public PayeeCancelResponse(ResultType result) {
        super(result);
    }

}
