package com.backbase.billpay.fiserv.enrolment.model;

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
@XmlRootElement(name="SubscriberEnrollRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriberEnrollRequest extends AbstractRequest {
    
    @XmlElement(name="BankAccountInformation")
    private BankAccountInformation bankAccountInformation;
    
    @XmlElement(name="SubscriberInformation")
    private SubscriberInformation subscriberInformation;

    @Builder
    public SubscriberEnrollRequest(Header header, BankAccountInformation bankAccountInformation,
                                   SubscriberInformation subscriberInformation) {
        super(header);
        this.bankAccountInformation = bankAccountInformation;
        this.subscriberInformation = subscriberInformation;
    }
    
}
