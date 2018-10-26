package com.backbase.billpay.fiserv.payments.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountId {
    
    @XmlElement(name = "AccountNumber")
    private String accountNumber;
    
    @XmlElement(name = "AccountType")
    private BankAccountType accountType;
    
    @XmlElement(name = "RoutingTransitNumber")
    private String routingTransitNumber;
    
    public enum BankAccountType {
        DDA,
        MMA,
        SDA,
        ILA,
        IRA,
        CLA,
        CCA,
        LOC;
    }

}
