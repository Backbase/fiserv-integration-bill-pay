package com.backbase.billpay.fiserv.accounts;

import com.backbase.billpay.fiserv.accounts.model.BankAccountListResponse;
import com.backbase.billpay.fiserv.accounts.model.BankAccountSummary;
import com.backbase.billpay.integration.rest.spec.v2.billpay.accounts.Account;
import com.backbase.billpay.integration.rest.spec.v2.billpay.accounts.BillPayAccountsGetResponseBody;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Map between Backbase and Fiserv account objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccountsMapper {

    @Mapping(target = "accounts", source = "summaries")
    @Mapping(target = "additions", ignore = true)
    BillPayAccountsGetResponseBody toBillPayAccountsGetResponseBody(BankAccountListResponse source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", source = "accountId.accountNumber")
    @Mapping(target = "accountType", source = "accountId.accountType")
    @Mapping(target = "routingNumber", source = "accountId.routingTransitNumber")
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "accountBalance", ignore = true)
    @Mapping(target = "additions", ignore = true)
    Account toAccount(BankAccountSummary source);
}
