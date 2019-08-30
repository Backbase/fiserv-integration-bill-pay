package com.backbase.billpay.fiserv.ebills.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlRootElement(name = "EbillFileRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class EbillFileRequest extends AbstractRequest {
    
    public enum FileEbillPaymentMethod {
        NONE_SPECIFIED("NoneSpecified"),
        BANK("Bank"),
        CHECK("Check"),
        CASH("Cash"),
        NOT_PAID("NotPaid"),
        OTHER("Other"),
        BILLER_WEBSITE("BillerWebSite"),
        PHONE("Phone"),
        MAIL("Mail"),
        OFFICE("Office"),
        ZERO_BALANCE_BILL("ZeroBalanceBill"),
        CONTESTED_BILL("ContestedBill");
        
        private String name;
        
        private FileEbillPaymentMethod(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    @XmlElement(name = "BillNote")
    private String billNote;
    
    @XmlElement(name = "EbillId")
    private String ebillId;
    
    @XmlElement(name = "FiledBillReason")
    private FileEbillPaymentMethod filedBillReason;
    
    @Builder
    public EbillFileRequest(Header header, String billNote, String ebillId, FileEbillPaymentMethod filedBillReason) {
        super(header);
        this.billNote = billNote;
        this.ebillId = ebillId;
        this.filedBillReason = filedBillReason;
    }

}
