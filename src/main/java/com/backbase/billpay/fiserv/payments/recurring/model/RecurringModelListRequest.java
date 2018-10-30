package com.backbase.billpay.fiserv.payments.recurring.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@XmlRootElement(name = "RecurringModelListRequest")
public class RecurringModelListRequest extends AbstractRequest {
    
    @Builder
    public RecurringModelListRequest(Header header) {
        super(header);
    }

}