package com.backbase.billpay.fiserv.search;

import com.backbase.billpay.fiserv.search.model.PayeeSearchResponse;
import com.backbase.billpay.fiserv.search.model.PayeeSearchResultInfo;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessearch.BillPaySearchGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessearch.Payee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Map between Backbase and Fiserv payee search objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SearchMapper {

    @Mapping(target = "payees", source = "payeeResult")
    @Mapping(target = "additions", ignore = true)
    BillPaySearchGetResponseBody toBillPaySearchGetResponseBody(PayeeSearchResponse source);

    @Mapping(target = "merchantID", source = "merchantNumber")
    @Mapping(target = "name", source = "onlineName")
    @Mapping(target = "zipRequired", source = "merchantZipRequired")
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "additions", ignore = true)
    Payee toPayee(PayeeSearchResultInfo source);
}
