package com.backbase.billpay.fiserv.payees.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@XmlRootElement(name = "PayeeListRequest")
public class PayeeListRequest extends AbstractRequest {
    
    @Builder
    public PayeeListRequest(Header header) {
        super(header);
    }

}
