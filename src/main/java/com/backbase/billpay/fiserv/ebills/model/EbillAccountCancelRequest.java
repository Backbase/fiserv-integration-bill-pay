package com.backbase.billpay.fiserv.ebills.model;

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
@XmlRootElement(name = "EbillAccountCancelRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class EbillAccountCancelRequest extends AbstractRequest {
    
    @XmlElement(name = "PayeeId")
    private Long payeeId;
    
    @Builder
    public EbillAccountCancelRequest(Long payeeId, Header header) {
        super(header);
        this.payeeId = payeeId;
    }

}
