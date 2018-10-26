package com.backbase.billpay.fiserv.payments.model;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.NoArgsConstructor;
import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payees.model.BldrDate;

@NoArgsConstructor
@XmlRootElement(name = "ExpeditedPaymentAddRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExpeditedPaymentAddRequest extends AbstractRequest {
    
    @XmlElement(name = "BankAccountId")
    private BankAccountId bankAccountId;
    
    @XmlElement(name = "EbillId")
    private String ebillId;
    
    @XmlElement(name = "PayeeId")
    private Long payeeId;
    
    @XmlElement(name = "PaymentAmount")
    private BigDecimal paymentAmount;
    
    @XmlElement(name = "PaymentDate")
    private BldrDate paymentDate;
    
    @XmlElement(name = "PaymentDeliveryMethod")
    private ExpeditedPaymentDelivery paymentDeliveryMethod;
    
    @XmlElement(name = "PaymentFee")
    private BigDecimal paymentFee;
    
    public enum ExpeditedPaymentDelivery {
        EXPEDITED_PAYMENT("ExpeditedPayment");
        
        private String name;
        
        private ExpeditedPaymentDelivery(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    @Builder
    public ExpeditedPaymentAddRequest(BankAccountId bankAccountId, String ebillId, Long payeeId,
                    BigDecimal paymentAmount, BldrDate paymentDate, ExpeditedPaymentDelivery paymentDeliveryMethod,
                    BigDecimal paymentFee, Header header) {
        super(header);
        this.bankAccountId = bankAccountId;
        this.ebillId = ebillId;
        this.payeeId = payeeId;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.paymentDeliveryMethod = paymentDeliveryMethod;
        this.paymentFee = paymentFee;
    }

}
