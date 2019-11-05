package com.backbase.billpay.fiserv.autopay.model;

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
@XmlRootElement(name = "EbillAutoPayListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class EbillAutoPayListResponse extends AbstractResponse {

    @XmlElement(name = "EbillAutoPayList")
    private List<EbillAutoPayListResultInfo> ebillAutoPayList;

    @Builder
    public EbillAutoPayListResponse(List<EbillAutoPayListResultInfo> ebillAutoPayList,  ResultType result) {
        super(result);
        this.ebillAutoPayList = ebillAutoPayList;
    }

}
