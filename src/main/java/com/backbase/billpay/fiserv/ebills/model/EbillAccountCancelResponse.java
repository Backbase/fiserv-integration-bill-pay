package com.backbase.billpay.fiserv.ebills.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@XmlRootElement(name = "EbillAccountCancelResponse")
public class EbillAccountCancelResponse extends AbstractResponse {
    
    @Builder
    public EbillAccountCancelResponse(ResultType result) {
        super(result);
    }

}