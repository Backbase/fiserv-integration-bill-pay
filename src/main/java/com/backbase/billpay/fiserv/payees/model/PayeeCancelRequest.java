package com.backbase.billpay.fiserv.payees.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@XmlRootElement(name = "PayeeCancelRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayeeCancelRequest extends AbstractRequest {

    @XmlElement(name = "CancelPayments")
    private Boolean cancelPayments;

    @XmlElement(name = "PayeeId")
    private Long payeeId;

    @Builder
    public PayeeCancelRequest(Boolean cancelPayments, Long payeeId, Header header) {
        super(header);
        this.cancelPayments = cancelPayments;
        this.payeeId = payeeId;
    }

}
