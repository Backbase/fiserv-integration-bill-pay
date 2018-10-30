package com.backbase.billpay.fiserv.search.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "PayeeSearchResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayeeSearchResponse extends AbstractResponse {

    @XmlElement(name = "PayeeResult")
    private List<PayeeSearchResultInfo> payeeResult;

    @Builder
    public PayeeSearchResponse(List<PayeeSearchResultInfo> payeeResult, ResultType result) {
        super(result);
        this.payeeResult = payeeResult;
    }
}
