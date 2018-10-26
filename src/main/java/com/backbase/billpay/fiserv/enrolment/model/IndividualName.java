package com.backbase.billpay.fiserv.enrolment.model;

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
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class IndividualName {
    
    @XmlElement(name="Last")
    private String last;
    
    @XmlElement(name="Middle")
    private String middle;
    
    @XmlElement(name="First")
    private String first;
    
    @XmlElement(name="Prefix")
    private String prefix;
}
