package com.backbase.billpay.fiserv.autopay.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@XmlRootElement(name = "EbillAutoPayAddResponse")
public class EbillAutoPayAddResponse extends AbstractResponse {

    @Builder
    public EbillAutoPayAddResponse(ResultType result) {
        super(result);
    }

}
