package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payees.model.USAddress;
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
public class StandardPaymentAddInfo {
    
    @XmlElement(name = "AllowDuplicatePayments")
    private Boolean allowDuplicatePayments;
    
    @XmlElement(name = "BankAccountId")
    private BankAccountId bankAccountId;
    
    @XmlElement(name = "EbillId")
    private String ebillId;
    
    @XmlElement(name = "ListItemId")
    private Integer listItemId;
    
    @XmlElement(name = "OvernightDeliveryAddress")
    private USAddress overnightDeliveryAddress;
    
    @XmlElement(name = "PayeeId")
    private Long payeeId;
    
    @XmlElement(name = "PaymentAmount")
    private BigDecimal paymentAmount;
    
    @XmlElement(name = "PaymentDate")
    private BldrDate paymentDate;
    
    @XmlElement(name = "PaymentDeliveryMethod")
    private PaymentDelivery paymentDeliveryMethod;
    
    @XmlElement(name = "PaymentFee")
    private BigDecimal paymentFee;
    
    @XmlElement(name = "PaymentMemo")
    private String paymentMemo;
    
    public enum PaymentDelivery {
        REGULAR_PAYMENT("RegularPayment"),
        OVERNIGHT_CHECK("OvernightCheck");
        
        private String name;
        
        private PaymentDelivery(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}