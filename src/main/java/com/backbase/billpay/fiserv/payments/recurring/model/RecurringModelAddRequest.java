package com.backbase.billpay.fiserv.payments.recurring.model;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payments.model.BankAccountId;

public class RecurringModelAddRequest {
    
    private Header header;
    private BankAccountId accountId;
    private Boolean modelExpirationAlert;
    private RecurringModelAddInfo modelInfo;
    private Long payeeId;
    private Boolean paymentScheduledAlert;
    private Boolean paymentSentAlert;

}
