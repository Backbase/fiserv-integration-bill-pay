package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class StandardAddPaymentDetail {
    
    @XmlElement(name = "ConfirmationNumber")
    private String confirmationNumber;
    
    @XmlElement(name = "PaymentId")
    private String paymentId;
    
    @XmlElement(name = "ListItemId")
    private Integer listItemId;
    
    @XmlElement(name = "NextPaymentDate")
    private BldrDate nextPaymentDate;
    
    @XmlElement(name = "PaymentMethod")
    private PaymentMethods paymentMethod;
    
    @XmlElement(name = "Result")
    private ResultType result;
    
    public enum PaymentMethods {
        ELECTRONIC("Electronic"),
        CORPORATE_CHECK("CorporateCheck"),
        DRAFT_CHECK("DraftCheck"),
        UNAVAILABLE("Unavailable");
        
        private String name;
        
        private PaymentMethods(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }

}
