package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlRootElement(name = "PaymentListRequest")
public class PaymentListRequest extends AbstractRequest {
    
    @XmlElement(name = "Filter")
    private PaymentFilter filter;
    
    @Builder
    public PaymentListRequest(PaymentFilter filter, Header header) {
        super(header);
        this.filter = filter;
    }

}
