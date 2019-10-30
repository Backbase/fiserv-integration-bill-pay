package com.backbase.billpay.fiserv.payees;

import com.backbase.billpay.fiserv.autopay.AutopayMapper;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResultInfo;
import com.backbase.billpay.fiserv.ebills.EbillsMapper;
import com.backbase.billpay.fiserv.payees.model.PayeeAddInfo;
import com.backbase.billpay.fiserv.payees.model.PayeeAddRequest;
import com.backbase.billpay.fiserv.payees.model.PayeeAddResponse;
import com.backbase.billpay.fiserv.payees.model.PayeeModifyRequest;
import com.backbase.billpay.fiserv.payees.model.PayeeModifyResponse;
import com.backbase.billpay.fiserv.payees.model.PayeeSummary;
import com.backbase.billpay.fiserv.payees.model.UsAddress;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Address;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayElectronicPayeesPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayElectronicPayeesPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayPayeesPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.BillPayPayeesPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.ElectronicPayeeByIdPutResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.LatestBill;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Payee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PayeeByIdPutResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PostElectronicRequestPayee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PostRequestPayee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PutElectronicRequestPayee;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.PutRequestPayee;
import com.backbase.rest.spec.common.types.Currency;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {PaymentServicesMapper.class,
                EbillsMapper.class, AutopayMapper.class})
public interface PayeesMapper {

    @Mapping(target = "header", ignore = true)
    @Mapping(target = "overrideAddressValidation", constant = "false")
    @Mapping(target = "payeeAddInfo", source = "payee")
    PayeeAddRequest toPayeeAddRequest(BillPayPayeesPostRequestBody source);

    @Mapping(target = "header", ignore = true)
    @Mapping(target = "overrideAddressValidation", constant = "false")
    @Mapping(target = "payeeAddInfo", source = "payee")
    PayeeAddRequest toPayeeAddRequest(BillPayElectronicPayeesPostRequestBody source);

    @Mapping(target = "header", ignore = true)
    @Mapping(target = "payeeAddInfo", source = "payee")
    @Mapping(target = "modifyPendingPayments", source = "payee.modifyPendingPayments")
    @Mapping(target = "payeeId", ignore = true)
    PayeeModifyRequest toPayeeModifyRequest(PayeeByIdPutRequestBody source);

    @Mapping(target = "header", ignore = true)
    @Mapping(target = "modifyPendingPayments", source = "payee.modifyPendingPayments")
    @Mapping(target = "payeeAddInfo", source = "payee")
    @Mapping(target = "payeeId", ignore = true)
    PayeeModifyRequest toPayeeModifyRequest(ElectronicPayeeByIdPutRequestBody source);

    @Mapping(target = "addressOnFile", constant = "false")
    @Mapping(target = "isImageCapture", constant = "false")
    @Mapping(target = "overnightAddress", ignore = true)
    @Mapping(target = "merchant", ignore = true)
    PayeeAddInfo toPayeeAddInfo(PostRequestPayee source);

    @Mapping(target = "addressOnFile", ignore = true)
    @Mapping(target = "isImageCapture", ignore = true)
    @Mapping(target = "overnightAddress", ignore = true)
    @Mapping(target = "merchant", ignore = true)
    PayeeAddInfo toPayeeAddInfo(PutRequestPayee source);

    @Mapping(target = "merchant.merchantNumber", source = "merchantID")
    @Mapping(target = "merchant.zip5", source = "merchantZipCode")
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "addressOnFile", constant = "true")
    @Mapping(target = "isImageCapture", ignore = true)
    @Mapping(target = "overnightAddress", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    PayeeAddInfo toPayeeAddInfo(PostElectronicRequestPayee source);

    @Mapping(target = "address", ignore = true)
    @Mapping(target = "addressOnFile", constant = "true")
    @Mapping(target = "isImageCapture", ignore = true)
    @Mapping(target = "merchant", ignore = true)
    @Mapping(target = "overnightAddress", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    PayeeAddInfo toPayeeAddInfo(PutElectronicRequestPayee source);

    @Mapping(target = "zip5", source = "postalCode")
    UsAddress toUsAddress(Address source);

    @Mapping(target = "id", source = "payeeResultInfo.payeeId")
    @Mapping(target = "additions", ignore = true)
    BillPayPayeesPostResponseBody toBillPayPayeesPostResponseBody(PayeeAddResponse source);

    @Mapping(target = "id", source = "payeeResultInfo.payeeId")
    @Mapping(target = "additions", ignore = true)
    PayeeByIdPutResponseBody toPayeeByIdPutResponseBody(PayeeModifyResponse source);

    @Mapping(target = "id", source = "payeeId")
    @Mapping(target = "overnightDeliveryAddress", source = "overNightAddress")
    @Mapping(target = "additions", ignore = true)
    @Mapping(target = "paymentServices", source = "source")
    Payee toPayee(PayeeSummary source);

    @Mapping(target = "postalCode", source = "zip5")
    @Mapping(target = "additions", ignore = true)
    Address toAddress(UsAddress source);

    @Mapping(target = "amount", source = "source")
    @Mapping(target = "currencyCode", constant = "USD")
    @Mapping(target = "additions", ignore = true)
    Currency toCurrency(BigDecimal source);

    @Mapping(target = "id", source = "payeeResultInfo.payeeId")
    @Mapping(target = "additions", ignore = true)
    BillPayElectronicPayeesPostResponseBody toBillPayElectronicPayeesPostResponseBody(PayeeAddResponse source);

    @Mapping(target = "id", source = "payeeResultInfo.payeeId")
    @Mapping(target = "additions", ignore = true)
    ElectronicPayeeByIdPutResponseBody toElectronicPayeeByIdPutResponseBody(PayeeModifyResponse source);

    @Mapping(target = "id", source = "payeeSource.payeeId")
    @Mapping(target = "overnightDeliveryAddress", source = "payeeSource.overNightAddress")
    @Mapping(target = "paymentServices", source = "payeeSource")
    @Mapping(target = "ebill.latestBill", source = "ebillSource")
    @Mapping(target = "ebill.capable", source = "payeeSource.ebillActivationStatus", qualifiedByName = "EbillCapable")
    @Mapping(target = "ebill.enabled", source = "payeeSource.ebillActivationStatus", qualifiedByName = "EbillEnabled")
    @Mapping(target = "ebill.autopay", source = "autopaySource")
    @Mapping(target = "additions", ignore = true)
    ElectronicPayee toElectronicPayee(Ebill ebillSource,
                    EbillAutoPayListResultInfo autopaySource,
                    PayeeSummary payeeSource);


    @Mapping(target = "id", source = "ebillId")
    @Mapping(target = "paymentDate", source = "dueDate")
    @Mapping(target = "amount", source = "amountDue")
    @Mapping(target = "minAmountDue", source = "minimumAmountDue")
    @Mapping(target = "outstandingBalance", source = "balance")
    @Mapping(target = "url", source = "billReferenceLinkUrl")
    @Mapping(target = "additions", ignore = true)
    @Mapping(target = "statementAvailable", constant = "false")
    LatestBill toLatestBill(com.backbase.billpay.fiserv.payeessummary.model.Ebill source);

}
