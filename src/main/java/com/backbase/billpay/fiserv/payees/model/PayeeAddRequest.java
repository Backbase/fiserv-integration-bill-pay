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
@XmlRootElement(name = "PayeeAddRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayeeAddRequest extends AbstractRequest {
    
    @XmlElement(name = "OverrideAddressValidation", required=true)
    private Boolean overrideAddressValidation;

    @XmlElement(name = "PayeeAddInfo", required=true)
    private PayeeAddInfo payeeAddInfo;
    
    @Builder
    public PayeeAddRequest(Boolean overrideAddressValidation, PayeeAddInfo payeeAddInfo, Header header) {
        super(header);
        this.overrideAddressValidation = overrideAddressValidation;
        this.payeeAddInfo = payeeAddInfo;
    }

}