package com.backbase.billpay.fiserv.payments.recurring.model;

import com.backbase.billpay.fiserv.common.model.AbstractRequest;
import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payments.model.BankAccountId;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "RecurringModelModifyRequest")
public class RecurringModelModifyRequest extends AbstractRequest {
    
    @XmlElement(name = "BankAccount")
    private BankAccountId accountId;
    
    @XmlElement(name = "RecurringModelId")
    private String recurringModelId;
    
    @XmlElement(name = "ModelExpirationAlert")
    private Boolean modelExpirationAlert;
    
    @XmlElement(name = "ModelInfo")
    private RecurringModelAddInfo modelInfo;
    
    @XmlElement(name = "PaymentScheduledAlert")
    private Boolean paymentScheduledAlert;
    
    @XmlElement(name = "PaymentSentAlert")
    private Boolean paymentSentAlert;

    @Builder
    public RecurringModelModifyRequest(Header header, BankAccountId accountId, String recurringModelId,
                    Boolean modelExpirationAlert, RecurringModelAddInfo modelInfo, Boolean paymentScheduledAlert,
                    Boolean paymentSentAlert) {
        super(header);
        this.accountId = accountId;
        this.recurringModelId = recurringModelId;
        this.modelExpirationAlert = modelExpirationAlert;
        this.modelInfo = modelInfo;
        this.paymentScheduledAlert = paymentScheduledAlert;
        this.paymentSentAlert = paymentSentAlert;
    }

    
}