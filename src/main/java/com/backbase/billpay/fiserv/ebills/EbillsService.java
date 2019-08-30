package com.backbase.billpay.fiserv.ebills;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.BillPayEbillsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutResponseBody;
import java.util.Date;

/**
 * Interface for eBill operations.
 */
public interface EbillsService {
    
    /**
     * Disable eBills for the payee.
     * @param header fiserv header including the subscriber id and ip address.
     * @param payeeId identifier for the payee.
     */
    void disableEbills(Header header, String payeeId);
    
    /**
     * Retrieve a list of eBills for the payee.
     * @param header fiserv header including the subscriber id and ip address.
     * @param payeeId identifier for the payee.
     * @param status the status of eBills to filter by.
     * @param startDate the start date of eBills to filter by.
     * @param endDate the end date of eBills to filter by.
     * @param from pagination based index.
     * @param size pagination based size.
     * @param orderBy field to order eBills by.
     * @param direction direction to return eBills.
     * @return eBills
     */
    BillPayEbillsGetResponseBody getEbills(Header header, String payeeId, String status, Date startDate, Date endDate, //NOSONAR
                    Integer from, Integer size, String orderBy, String direction);
    
    /**
     * Update the status of an eBill for a payee.
     * @param header fiserv header including the subscriber id and ip address.
     * @param request request containing details of the eBill to update.
     * @param ebillId identifier for the eBill.
     * @return response.
     */
    EbillByIdPutResponseBody updateEbillStatus(Header header, EbillByIdPutRequestBody request, String ebillId);

}
