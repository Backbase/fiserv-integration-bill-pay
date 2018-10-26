package com.backbase.billpay.fiserv.payments.model;

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
public class Payee {
    
    @XmlElement(name = "AccountNumber")
    private String accountNumber;
    
    @XmlElement(name = "Name")
    private String name;
    
    @XmlElement(name = "NickName")
    private String nickName;
    
    @XmlElement(name = "PayeeId")
    private Long payeeId;

}
