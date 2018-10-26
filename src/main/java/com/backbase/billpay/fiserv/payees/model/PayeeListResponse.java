package com.backbase.billpay.fiserv.payees.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import java.util.List;
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
@XmlRootElement(name = "PayeeListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayeeListResponse extends AbstractResponse {

    @XmlElement(name = "Payees")
    private List<PayeeSummary> payees;

    @Builder
    public PayeeListResponse(List<PayeeSummary> payees, ResultType result) {
        super(result);
        this.payees = payees;
    }

}
