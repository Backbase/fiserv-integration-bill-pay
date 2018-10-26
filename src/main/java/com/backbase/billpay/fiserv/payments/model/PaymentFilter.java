package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.payees.model.BldrDate;
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
public class PaymentFilter {
    
    @XmlElement(name = "NumberOfDays")
    private Integer numberOfDays;
    
    @XmlElement(name = "StartingPaymentDate")
    private BldrDate startingPaymentDate;
    
    @XmlElement(name = "StatusFilter")
    private PaymentStatusFilter statusFilter;
    
    public enum PaymentStatusFilter {
        ALL("All"),
        PENDING("Pending"),
        HISTORICAL("Historical");
        
        private String name;
        
        private PaymentStatusFilter(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }

}
