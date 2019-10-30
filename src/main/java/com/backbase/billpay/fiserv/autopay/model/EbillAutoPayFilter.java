package com.backbase.billpay.fiserv.autopay.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement(name = "EbillAutoPayFilter")
@XmlAccessorType(XmlAccessType.FIELD)
public class EbillAutoPayFilter {

    @XmlElement(name = "PayeeId")
    private Long payeeId;

}
