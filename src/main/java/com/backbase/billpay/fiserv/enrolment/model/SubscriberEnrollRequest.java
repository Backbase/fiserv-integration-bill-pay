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
@XmlRootElement(name = "SubscriberEnrollRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriberEnrollRequest extends AbstractRequest {

    @XmlElement(name = "BankAccountInformation")
    private BankAccountInformation bankAccountInformation;

    @XmlElement(name = "SubscriberInformation")
    private SubscriberInformation subscriberInformation;

    /**
     * Constructor with header from abstract request.
     * @param header Fiserv header including the subscriber id and ip address
     * @param bankAccountInformation Details of the bank account
     * @param subscriberInformation Details of the subscriber
     */
    @Builder
    public SubscriberEnrollRequest(Header header, BankAccountInformation bankAccountInformation,
                    SubscriberInformation subscriberInformation) {
        super(header);
        this.bankAccountInformation = bankAccountInformation;
        this.subscriberInformation = subscriberInformation;
    }

}
