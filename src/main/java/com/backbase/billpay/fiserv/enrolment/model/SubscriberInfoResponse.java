package com.backbase.billpay.fiserv.enrolment.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
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
@XmlRootElement(name = "SubscriberInfoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class SubscriberInfoResponse extends AbstractResponse {
    
    @XmlElement(name = "SubscriberInformation")
    private SubscriberInformation subscriberInformation;

    @Builder
    public SubscriberInfoResponse(SubscriberInformation subscriberInformation, ResultType result) {
        super(result);
        this.subscriberInformation = subscriberInformation;
    }
}
