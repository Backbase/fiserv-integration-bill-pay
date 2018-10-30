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
@XmlRootElement(name = "RecurringModelAddRequest")
public class RecurringModelAddRequest extends AbstractRequest {
    
    @XmlElement(name = "AccountId")
    private BankAccountId accountId;
    
    @XmlElement(name = "PayeeId")
    private Long payeeId;
    
    @XmlElement(name = "ModelExpirationAlert")
    private Boolean modelExpirationAlert;
    
    @XmlElement(name = "ModelInfo")
    private RecurringModelAddInfo modelInfo;
    
    @XmlElement(name = "PaymentScheduledAlert")
    private Boolean paymentScheduledAlert;
    
    @XmlElement(name = "PaymentSentAlert")
    private Boolean paymentSentAlert;

    @Builder
    public RecurringModelAddRequest(BankAccountId accountId, Long payeeId, Boolean modelExpirationAlert,
                                    RecurringModelAddInfo modelInfo, Boolean paymentScheduledAlert, 
                                    Boolean paymentSentAlert, Header header) {
        super(header);
        this.accountId = accountId;
        this.payeeId = payeeId;
        this.modelExpirationAlert = modelExpirationAlert;
        this.modelInfo = modelInfo;
        this.paymentScheduledAlert = paymentScheduledAlert;
        this.paymentSentAlert = paymentSentAlert;
    }
}