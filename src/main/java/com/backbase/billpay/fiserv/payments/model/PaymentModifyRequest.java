package com.backbase.billpay.fiserv.payments.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import java.math.BigDecimal;
import java.util.List;
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
