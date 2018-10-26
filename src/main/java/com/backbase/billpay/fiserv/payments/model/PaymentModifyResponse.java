package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.common.model.AbstractResponse;
import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "PaymentModifyResponse")
public class PaymentModifyResponse extends AbstractResponse {
    
    @XmlElement(name = "ConfirmationNumber")
    private String confirmationNumber;
    
    @XmlElement(name = "NextPaymentDate")
    private BldrDate nextPaymentDate;
    
    @XmlElement(name = "NextRecurringPaymentDate")
    private BldrDate nextRecurringPaymentDate;
    
    @XmlElement(name = "PaymentId")
    private String paymentId;
    
    @Builder
    public PaymentModifyResponse(String confirmationNumber, BldrDate nextPaymentDate, 
                                 BldrDate nextRecurringPaynentDate, String paymentId, ResultType result) {
        super(result);
        this.confirmationNumber = confirmationNumber;
        this.nextPaymentDate = nextPaymentDate;
        this.nextRecurringPaymentDate = nextRecurringPaynentDate;
        this.paymentId = paymentId;
    }

}
