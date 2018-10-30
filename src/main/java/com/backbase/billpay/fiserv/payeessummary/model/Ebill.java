package com.backbase.billpay.fiserv.payeessummary.model;

import com.backbase.billpay.fiserv.payments.model.Payee;
import java.math.BigDecimal;
import java.util.Date;
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
public class Ebill {
    
    @XmlElement(name = "AmountDue")
    private BigDecimal amountDue;
    
    @XmlElement(name = "Balance")
    private BigDecimal balance;
    
    @XmlElement(name = "BillReferenceLinkURL")
    private String billReferenceLinkUrl;
    
    @XmlElement(name = "BillType")
    private BillType billType;
    
    @XmlElement(name = "DueDate")
    private Date dueDate;
    
    @XmlElement(name = "EbillId")
    private String ebillId;
    
    @XmlElement(name = "IsReplacementEbill")
    private Boolean isReplacementEbill;
    
    @XmlElement(name = "MinimumAmountDue")
    private BigDecimal minimumAmountDue;
    
    @XmlElement(name = "Payee")
    private Payee payee;
    
    @XmlElement(name = "Status")
    private EbillStatus status;
    
    @XmlElement(name = "UseDueDateText")
    private Boolean useDueDateText;
    
    public enum BillType {
        FROM_BILLER("FromBiller"),
        FROM_SUBSCRIBER("FromSubscriber");
        
        private String name;
        
        private BillType(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    public enum EbillStatus {
        PAID("Paid"),
        UNPAID("Unpaid"),
        PAYMENT_FAILED("PaymentFailed"),
        PAYMENT_CANCELLED("PaymentCancelled"),
        FILED("Filed");
        
        private String name;
        
        private EbillStatus(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }

}
