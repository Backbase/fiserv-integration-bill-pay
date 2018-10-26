package com.backbase.billpay.fiserv.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.backbase.billpay.fiserv.accounts.model.BankAccountListRequest;
import com.backbase.billpay.fiserv.accounts.model.BankAccountListResponse;
import com.backbase.billpay.fiserv.accounts.model.BankAccountSummary;
import com.backbase.billpay.fiserv.common.model.ResultType;
import com.backbase.billpay.fiserv.payments.model.BankAccountId;
import com.backbase.billpay.fiserv.payments.model.BankAccountId.BankAccountType;
import com.backbase.billpay.fiserv.utils.AbstractWebServiceTest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.accounts.Account;
import com.backbase.billpay.integration.rest.spec.v2.billpay.accounts.BillPayAccountsGetResponseBody;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountsListenerTest extends AbstractWebServiceTest {

    @Autowired
    private AccountsListener listener;

    @Test
    public void getAccounts() {
        
        // create the mock response
        BankAccountListResponse fiservResponse = createFiservBankAccountResponse();

        // set up mock server to return the response
        setupWebServiceResponse(fiservResponse);

        // call the get accounts
        BillPayAccountsGetResponseBody response =
                        listener.getBillPayAccounts(createRequestWrapper(null), null, SUBSCRIBER_ID)
                                .getRequest().getData();

        // validate the results
        Account accountOne = response.getAccounts().get(0);
        assertEquals("Acc1", accountOne.getAccountNickName());
        assertEquals("001", accountOne.getAccountNumber());
        assertEquals(BankAccountType.DDA.toString(), accountOne.getAccountType());
        assertEquals("100", accountOne.getRoutingNumber());
        Account accountTwo = response.getAccounts().get(1);
        assertNull(accountTwo.getAccountNickName());
        assertEquals("002", accountTwo.getAccountNumber());
        assertEquals(BankAccountType.CCA.toString(), accountTwo.getAccountType());
        assertEquals("200", accountTwo.getRoutingNumber());

        // validate the request header
        assertHeader(SUBSCRIBER_ID, retrieveRequest(BankAccountListRequest.class).getHeader());
    }
    
    private BankAccountListResponse createFiservBankAccountResponse() {
        BankAccountSummary bankAccountOne = BankAccountSummary.builder()
                                                              .accountNickName("Acc1")
                                                              .accountId(BankAccountId.builder()
                                                                                      .accountNumber("001")
                                                                                      .accountType(BankAccountType.DDA)
                                                                                      .routingTransitNumber("100")
                                                                                      .build())
                                                              .build();
        BankAccountSummary bankAccountTwo = BankAccountSummary.builder()
                                                              .accountId(BankAccountId.builder()
                                                                                      .accountNumber("002")
                                                                                      .accountType(BankAccountType.CCA)
                                                                                      .routingTransitNumber("200")
                                                                                      .build())
                                                              .build();
        List<BankAccountSummary> summaries = Arrays.asList(bankAccountOne, bankAccountTwo);
        return BankAccountListResponse.builder()
                                      .result(ResultType.builder()
                                                        .success(true)
                                                        .build())
                                      .summaries(summaries)
                                      .build();
    }
}