package com.backbase.billpay.fiserv.payments.recurring.model;

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
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class RecurringModel {
    
    
    @XmlElement(name = "RecurringModelId")
    private String recurringModelId;

    @XmlElement(name = "PayeeId")
    private String payeeId;
    
    @XmlElement(name = "ModelExpirationAlert")
    private Boolean modelExpirationAlert;
    
    @XmlElement(name = "PaymentScheduledAlert")
    private Boolean paymentScheduledAlert;
    
    @XmlElement(name = "PaymentSentAlert")
    private Boolean paymentSentAlert;
    
    @XmlElement(name = "RecurringModelInfo")
    private ModelInfo recurringModelInfo;
    
    @XmlElement(name = "FundingAccount")
    private BankAccountId fundingAccount;
}