package com.backbase.billpay.fiserv.enrolment.model;

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
public class BankAccountInformation {

    @XmlElement(name = "AccountId")
    private BankAccountId accountId;

    @XmlElement(name = "AccountNickName")
    private String accountNickName;

    @Builder.Default
    @XmlElement(name = "IsBankingEnabled")
    private boolean bankingEnabled = false;

    @Builder.Default
    @XmlElement(name = "IsBillPaymentEnabled")
    private boolean billPaymentEnabled = true;

    @Builder.Default
    @XmlElement(name = "IsBillingAccount")
    private boolean billingAccount = true;

    @Builder.Default
    @XmlElement(name = "IsBusinessAccount")
    private boolean businessAccount = false;

    @Builder.Default
    @XmlElement(name = "IsPreferredAccount")
    private boolean preferredAccount = true;
}
