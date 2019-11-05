package com.backbase.billpay.fiserv.autopay.model;


import com.backbase.billpay.fiserv.payments.model.BankAccountId;
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
@XmlRootElement(name = "EbillAutoPayListResultInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class EbillAutoPayListResultInfo {

    @XmlElement(name = "AccountId")
    private BankAccountId bankAccountId;

    @XmlElement(name = "AutoPay")
    private AutoPayInfo autoPay;

    @XmlElement(name = "PayeeId")
    private Long payeeId;

    @XmlElement(name = "PaymentScheduledAlert")
    private Boolean paymentScheduledAlert;

    @XmlElement(name = "PaymentSentAlert")
    private Boolean paymentSentAlert;

}
