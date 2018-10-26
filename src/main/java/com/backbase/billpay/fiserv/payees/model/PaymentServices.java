package com.backbase.billpay.fiserv.payees.model;

import java.math.BigDecimal;
import java.util.Date;
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
public class PaymentServices {
    
    @XmlElement(name = "CutOffTime")
    private Date cutOffTime;
    
    @XmlElement(name = "EarliestDate")
    private BldrDate earliestDate;
    
    @XmlElement(name = "Fee")
    private BigDecimal fee;
    
    @XmlElement(name = "IsSponserEnabled")
    private Boolean isSponsorEnabled;
    
    @XmlElement(name = "NextDate")
    private BldrDate nextDate;
    
    @XmlElement(name = "PaymentService")
    private PaymentServiceType paymentService;
    
    public enum PaymentServiceType {
        OVERNIGHT_CHECK("OvernightCheck"),
        EXPEDITED_PAYMENT("ExpeditedPayment");
        
        private String name;
        
        private PaymentServiceType(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }

}
