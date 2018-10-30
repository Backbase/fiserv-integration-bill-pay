package com.backbase.billpay.fiserv.payeessummary;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.BillPayPayeesSummaryGetResponseBody;

/**
 * Interface for payees summary operations.
 */
public interface PayeesSummaryService {
    
    /**
     * Get the Payee Summary for the specified subscriber ID.
     * Information includes next payment information, payment services etc.
     * @param header fiserv header including the subscriber id and ip address.
     * @return payee summary response.
     */
    BillPayPayeesSummaryGetResponseBody getBillPayPayeesSummary(Header header);
}