package com.backbase.billpay.fiserv.payeessummary.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
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
@XmlRootElement(name = "EbillListRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class EbillListRequest extends AbstractRequest {
    
    @XmlElement(name = "Filter")
    private EbillFilter filter;
    
    @Builder
    public EbillListRequest(EbillFilter filter, Header header) {
        super(header);
        this.filter = filter;
    }

}
