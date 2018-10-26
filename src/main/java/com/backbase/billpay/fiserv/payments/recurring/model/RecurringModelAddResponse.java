package com.backbase.billpay.fiserv.payments.recurring.model;

import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.payees.model.BldrDate;

public class RecurringModelAddResponse {
    
    private ResultType result;
    private BldrDate adjustedNextPaymentDate;
    private String recurringModelId;

}
