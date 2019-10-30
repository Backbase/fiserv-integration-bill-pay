package com.backbase.billpay.fiserv.accounts;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.fiserv.utils.AbstractController;
import com.backbase.billpay.integration.rest.spec.serviceapi.v2.billpay.accounts.BillPayAccountsApi;
import com.backbase.billpay.integration.rest.spec.v2.billpay.accounts.BillPayAccountsGetResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Bill Pay account operations.
 */
@RestController
public class AccountsController extends AbstractController implements BillPayAccountsApi {
    
    private AccountsService accountsService;

    @Autowired
    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @Override
    public BillPayAccountsGetResponseBody getBillPayAccounts(String subscriberId, HttpServletRequest httpServletRequest,
                    HttpServletResponse httpServletResponse) {
        Header header = fiservUtils.createHeader(httpServletRequest, subscriberId);
        BillPayAccountsGetResponseBody response = accountsService.getAccounts(header);
        logger.debug("Found accounts for subscriberID: {}, returned accounts: {}", subscriberId, response);
        return response;
    }

}
