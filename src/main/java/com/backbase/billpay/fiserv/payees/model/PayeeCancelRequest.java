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

    /**
     * Constructor with header from abstract request.
     * @param cancelPayments Whether to cancel pending payments for the payee
     * @param payeeId Id of the payee
     * @param header Fiserv header including the subscriber id and ip address
     */
    @Builder
    public PayeeCancelRequest(Boolean cancelPayments, Long payeeId, Header header) {
        super(header);
        this.cancelPayments = cancelPayments;
        this.payeeId = payeeId;
    }

}
