package com.backbase.billpay.fiserv.payeessummary;

import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.BillPayPayeesSummaryGetResponseBody;

/**
 * Interface for payees summary operations.
 */
public interface PayeesSummaryService {
    
    /**
     * Get the Payee Summary for the specified subscriber ID.
     * Information includes next payment information, payment services etc.
     * @param subscriberId the id of the subscriber.
     * @return payee summary response.
     */
    BillPayPayeesSummaryGetResponseBody getBillPayPayeesSummary(String subscriberId);
}