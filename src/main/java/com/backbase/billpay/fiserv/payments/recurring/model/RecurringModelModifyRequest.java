package com.backbase.billpay.fiserv.payments.recurring.model;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.payments.model.BankAccountId;

public class RecurringModelModifyRequest {
    
    private Header header;
    private BankAccountId bankAccount;
    private Boolean modelExpirationAlert;
    private RecurringModelAddInfo modelInfo;
    private Boolean paymentScheduledAlert;
    private Boolean paymentSentAlert;
    private String recurringModelId;

}
