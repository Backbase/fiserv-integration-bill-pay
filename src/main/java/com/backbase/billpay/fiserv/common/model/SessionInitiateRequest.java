package com.backbase.billpay.fiserv.common.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "SessionInitiateRequest")
public class SessionInitiateRequest extends AbstractRequest {

    @XmlElement(name = "IpAddress")
    private String ipAddress;
}