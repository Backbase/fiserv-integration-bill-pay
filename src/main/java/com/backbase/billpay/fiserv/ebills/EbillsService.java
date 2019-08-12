package com.backbase.billpay.fiserv.ebills;

import com.backbase.billpay.fiserv.common.model.Header;

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

}
