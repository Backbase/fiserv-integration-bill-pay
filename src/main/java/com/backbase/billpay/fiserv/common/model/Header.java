package com.backbase.billpay.fiserv.common.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Header {

    @XmlElement(name = "ClientAppText")
    private String clientAppText;

    @XmlElement(name = "ClientAppVersion")
    private String clientAppVersion;

    @XmlElement(name = "CorrelationId")
    private String correlationId;

    @XmlElement(name = "SessionCorrelationId")
    private String sessionCorrelationId;

    @XmlElement(name = "SponsorId")
    private String sponsorId;

    @XmlElement(name = "SubscriberId")
    private String subscriberId;

    @XmlElement(name = "SubscriberIpAddress")
    private String subscriberIpAddress;
}
