package com.backbase.billpay.fiserv.search.model;

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
public class PayeeSearchResultInfo {

    @XmlElement(name = "DisplayName")
    private String displayName;

    @XmlElement(name = "IsEbillUpsellable")
    private Boolean isEbillUpsellable;

    @XmlElement(name = "IsExactMatch")
    private Boolean isExactMatch;

    @XmlElement(name = "MerchantNumber")
    private int merchantNumber;

    @XmlElement(name = "MerchantZipRequired")
    private Boolean merchantZipRequired;

    @XmlElement(name = "OnlineName")
    private String onlineName;

}
