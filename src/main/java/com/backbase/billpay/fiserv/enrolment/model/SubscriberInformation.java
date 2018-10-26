package com.backbase.billpay.fiserv.enrolment.model;

import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payees.model.USAddress;
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
public class SubscriberInformation {

    @XmlElement(name="USAddress")
    private USAddress usAddress;
    
    @XmlElement(name = "BirthDate")
    private BldrDate birthDate;
    
    @XmlElement(name="DayPhone")
    private String dayPhone;
    
    @XmlElement(name="EmailAddress")
    private String emailAddress;
    
    @XmlElement(name="EveningPhone")
    private String eveningPhone;
    
    @XmlElement(name="TaxId")
    private String taxId;
    
    @XmlElement(name="IsAllowedToSolicit")
    @Builder.Default
    private boolean allowedToSolicit = false;
    
    @XmlElement(name="Name")
    private IndividualName name;
}
