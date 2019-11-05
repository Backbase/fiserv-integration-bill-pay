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
@XmlRootElement(name = "EbillAutoPayCancelRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class EbillAutoPayCancelRequest extends AbstractRequest {

    @XmlElement
    private Long payeeId;

    @Builder
    public EbillAutoPayCancelRequest(Header header, long payeeId) {
        super(header);
        this.payeeId = payeeId;
    }

}
