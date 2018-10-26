package com.backbase.billpay.fiserv.accounts;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.integration.rest.spec.v2.billpay.accounts.BillPayAccountsGetResponseBody;

/**
 * Interface for account operations.
 */
public interface AccountsService {
    
    /**
     * Get the Accounts for the subscriber.
     * Information includes account details and the balance.
     * @param header fiserv header including the subscriber id and ip address.
     * @return accounts response.
     */
    BillPayAccountsGetResponseBody getAccounts(Header header);
}