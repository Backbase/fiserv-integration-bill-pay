package com.backbase.billpay.fiserv.accounts.model;

import com.backbase.billpay.fiserv.payments.model.BankAccountId;
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
public class BankAccountSummary {
    
    @XmlElement(name="AccountNickName")
    private String accountNickName;
    
    @XmlElement(name="AccountId")
    private BankAccountId accountId;
}
