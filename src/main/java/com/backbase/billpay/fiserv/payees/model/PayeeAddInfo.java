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
public class PayeeAddInfo {

    @XmlElement(name = "AccountNumber")
    private String accountNumber;

    @XmlElement(name = "Address")
    private UsAddress address;

    @XmlElement(name = "AddressOnFile")
    private Boolean addressOnFile;

    @XmlElement(name = "IsImageCapture")
    private Boolean isImageCapture;

    @XmlElement(name = "Merchant")
    private MerchantInfo merchant;

    @XmlElement(name = "Name", required = true)
    private String name;

    @XmlElement(name = "NickName")
    private String nickName;

    @XmlElement(name = "OvernightAddress")
    private UsAddress overnightAddress;

    @XmlElement(name = "PhoneNumber")
    private String phoneNumber;

}
