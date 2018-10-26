package com.backbase.billpay.fiserv.payees.model;

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
public class MerchantInfo {

    @XmlElement(name = "MerchantNumber")
    private int merchantNumber;

    @XmlElement(name = "Zip4")
    private String zip4;

    @XmlElement(name = "Zip5")
    private String zip5;

}
