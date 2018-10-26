package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payments.model.PaymentDetail.PaymentStatus;
import java.math.BigDecimal;
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
public class Payment {
    
    @XmlElement(name = "ActualPostDate")
    private BldrDate actualPostDate;
    
    @XmlElement(name = "Amount")
    private BigDecimal amount;
    
    @XmlElement(name = "BankAccountId")
    private BankAccountId bankAccountId;
    
    @XmlElement(name = "ConfirmationNumber")
    private String confirmationNumber;
    
    @XmlElement(name = "EbillId")
    private String ebillId;
    
    @XmlElement(name = "ExpectedDeliveryDate")
    private BldrDate expectedDeliveryDate;
    
    @XmlElement(name = "IsEbillAutoPayment")
    private Boolean ebillAutoPayment;
    
    @XmlElement(name = "IsRecurringModelPayment")
    private Boolean recurringModelPayment;
    
    @XmlElement(name = "IsReissued")
    private Boolean isReissued;
    
    @XmlElement(name = "Memo")
    private String memo;
    
    @XmlElement(name = "PaymentId")
    private String paymentId;

    @XmlElement(name = "Payee")
    private Payee payee;
    
    @XmlElement(name = "PaymentDate")
    private BldrDate paymentDate;
    
    @XmlElement(name = "Status")
    private PaymentStatus status;
}