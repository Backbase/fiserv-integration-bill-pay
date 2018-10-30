package com.backbase.billpay.fiserv.payeessummary.model;

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
@XmlRootElement(name = "EbillListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class EbillListResponse extends AbstractResponse {
    
    @XmlElement(name = "EbillList")
    private List<Ebill> ebillList;
    
    @Builder
    public EbillListResponse(List<Ebill> ebillList, ResultType result) {
        super(result);
        this.ebillList = ebillList;
    }

}
