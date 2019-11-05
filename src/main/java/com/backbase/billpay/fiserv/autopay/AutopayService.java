package com.backbase.billpay.fiserv.autopay;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.autopay.BillPayAutopayPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.autopay.BillPayAutopayPutResponseBody;

/**
 * Interface for Autopay operations.
 */
public interface AutopayService {

    /**
     * Update or enable autopay for a given payee.
     *
     * @param header fiserv header including the subscriber id and ip address.
     * @param payeeId unique identifier for the payee
     * @param request update autopay request
     * @return autopay response
     */

    BillPayAutopayPutResponseBody putAutopayByPayeeId(Header header, String payeeId,
                    BillPayAutopayPutRequestBody request);

    /**
     * Disable autopay for a given payee.
     *
     * @param header fiserv header including the subscriber id and ip address.
     * @param payeeId unique identifier for the payee
     */
    void deleteAutopayByPayeeId(Header header, String payeeId);
}
