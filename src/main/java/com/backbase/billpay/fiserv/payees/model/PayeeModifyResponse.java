package com.backbase.billpay.fiserv.payees.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
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
@XmlRootElement(name = "PayeeModifyResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayeeModifyResponse extends AbstractResponse {

    @XmlElement(name = "PayeeResultInfo")
    private PayeeSummary payeeResultInfo;

    @Builder
    public PayeeModifyResponse(PayeeSummary payeeResultInfo, ResultType result) {
        super(result);
        this.payeeResultInfo = payeeResultInfo;
    }

}
