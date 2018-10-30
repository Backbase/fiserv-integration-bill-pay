package com.backbase.billpay.fiserv.payeessummary.model;

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
public class EbillFilter {
    
    @XmlElement(name = "BillType")
    private BillTypeFilter billType;
    
    @XmlElement(name = "NumberOfDays")
    private Integer numberOfDays;
    
    @XmlElement(name = "StartingDate")
    private BldrDate startingDate;
    
    public enum BillTypeFilter {
        BILLER("FromBiller"),
        SUBSCRIBER("FromSubscriber"),
        ALL("All");
        
        private String name;
        
        private BillTypeFilter(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }

}
