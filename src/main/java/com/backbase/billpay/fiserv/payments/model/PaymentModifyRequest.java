package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import java.math.BigDecimal;
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
@XmlRootElement(name = "PaymentModifyRequest")
public class PaymentModifyRequest extends AbstractRequest {
    
    @XmlElement(name = "BankAccountId")
    private BankAccountId bankAccountId;
    
    @XmlElement(name = "EbillId")
    private String ebillId;
    
    @XmlElement(name = "PaymentAmount")
    private BigDecimal paymentAmount;
    
    @XmlElement(name = "PaymentDate")
    private BldrDate paymentDate;
    
    @XmlElement(name = "PaymentId")
    private String paymentId;
    
    @XmlElement(name = "PaymentMemo")
    private String paymentMemo;
    
    /**
     * Constructor with header from abstract request.
     * @param bankAccountId Details of the bank account
     * @param ebillId Id of the associated ebill
     * @param paymentAmount Amount to pay the payee
     * @param paymentDate Date the payment is scheduled for
     * @param paymentId Id of the payment
     * @param paymemtMemo Memo for the payment
     * @param header Fiserv header including the subscriber id and ip address
     */
    @Builder
    public PaymentModifyRequest(BankAccountId bankAccountId, String ebillId, BigDecimal paymentAmount,
                    BldrDate paymentDate, String paymentId, String paymemtMemo, Header header) {
        super(header);
        this.bankAccountId = bankAccountId;
        this.ebillId = ebillId;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.paymentId = paymentId;
        this.paymentMemo = paymemtMemo;
    }

}
