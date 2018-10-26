package com.backbase.billpay.fiserv.search;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessearch.BillPaySearchGetResponseBody;

/**
 * Interface for search operations.
 */
public interface SearchService {

    /**
     * Search for payees based upon a name.
     * @param header fiserv header including the subscriber id and ip address.
     * @param name the name of the payee to search for.
     * @param zipCode payee zip code.
     * @param accountNumber account number with the payee.
     * @return search response.
     */
    BillPaySearchGetResponseBody getBillPayPayeesSearch(Header header, String name, 
        String zipCode, String accountNumber);
}