package com.backbase.billpay.fiserv.search.model;

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
@XmlRootElement(name="PayeeSearchRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayeeSearchRequest extends AbstractRequest {
    
    @XmlElement(name="Name")
    private String name;
    
    @Builder
    public PayeeSearchRequest(String name, Header header) {
        super(header);
        this.name = name;
    }
    
}
