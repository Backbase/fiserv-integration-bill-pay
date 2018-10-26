package com.backbase.billpay.fiserv.payments.recurring.model;

import com.backbase.billpay.fiserv.common.model.Header;

public class RecurringModelCancelRequest {
    
    private Header header;
    private Boolean cancelPendingPayments;
    private String recurringModelId;

}
