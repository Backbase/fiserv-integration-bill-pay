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
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlRootElement(name = "PayeeModifyRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayeeModifyRequest extends AbstractRequest {

    @XmlElement(name = "ModifyPendingPayments")
    private Boolean modifyPendingPayments;

    @XmlElement(name = "PayeeAddInfo")
    private PayeeAddInfo payeeAddInfo;

    @XmlElement(name = "PayeeId")
    private Long payeeId;

    @Builder
    public PayeeModifyRequest(Boolean modifyPendingPayments, PayeeAddInfo payeeAddInfo, Long payeeId, Header header) {
        super(header);
        this.modifyPendingPayments = modifyPendingPayments;
        this.payeeAddInfo = payeeAddInfo;
        this.payeeId = payeeId;
    }

}
