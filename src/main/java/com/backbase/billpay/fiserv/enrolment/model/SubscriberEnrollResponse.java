package com.backbase.billpay.fiserv.enrolment.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@XmlRootElement(name="SubscriberEnrollResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class SubscriberEnrollResponse extends AbstractResponse {

    @Builder
    public SubscriberEnrollResponse(ResultType result) {
        super(result);
    } 
}