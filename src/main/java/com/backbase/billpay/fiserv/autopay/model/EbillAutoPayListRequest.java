package com.backbase.billpay.fiserv.autopay.model;

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
@XmlRootElement(name = "EbillAutoPayListRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class EbillAutoPayListRequest extends AbstractRequest {

    @XmlElement(name = "Filter")
    private EbillAutoPayFilter filter;

    @Builder
    public EbillAutoPayListRequest(Header header, EbillAutoPayFilter filter) {
        super(header);
        this.filter = filter;
    }

}
