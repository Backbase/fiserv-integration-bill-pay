package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payees.model.UsAddress;
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
public class PaymentDetail {
    
    @XmlElement(name = "Amount")
    private BigDecimal amount;
    
    @XmlElement(name = "BankAccountId")
    private BankAccountId bankAccountId;
    
    @XmlElement(name = "ConfirmationNumber")
    private String confirmationNumber;
    
    @XmlElement(name = "DeliveryDate")
    private BldrDate deliveryDate;
    
    @XmlElement(name = "EbillId")
    private String ebillId;
    
    @XmlElement(name = "IsEbillAutoPayment")
    private Boolean ebillAutoPayment;
    
    @XmlElement(name = "IsPaymentFee")
    private Boolean paymentFee;
    
    @XmlElement(name = "IsRecurringModelPayment")
    private Boolean recurringModelPayment;
    
    @XmlElement(name = "IsReissued")
    private Boolean reissued;
    
    @XmlElement(name = "Memo")
    private String memo;
    
    @XmlElement(name = "Payee")
    private Payee payee;
    
    @XmlElement(name = "PaymentAddress")
    private UsAddress paymentAddress;
    
    @XmlElement(name = "PaymentDate")
    private BldrDate paymentDate;
    
    @XmlElement(name = "PaymentId")
    private String paymentId;
    
    @XmlElement(name = "Status")
    private PaymentStatus status;
    
    public enum PaymentStatus {
        PENDING("Pending"),
        IN_PROCESS("InProcess"),
        PROCESSED("Processed"),
        CANCELED("Canceled"),
        FAILED("Failed");
        
        private String name;
        
        private PaymentStatus(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    

}
