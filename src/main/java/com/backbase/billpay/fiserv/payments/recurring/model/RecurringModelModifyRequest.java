package com.backbase.billpay.fiserv.payments.recurring.model;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payments.model.BankAccountId;
import javax.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement(name = "RecurringModelModifyRequest")
public class RecurringModelModifyRequest {
    
    private Header header;
    private BankAccountId bankAccount;
    private Boolean modelExpirationAlert;
    private RecurringModelAddInfo modelInfo;
    private Boolean paymentScheduledAlert;
    private Boolean paymentSentAlert;
    private String recurringModelId;

}
