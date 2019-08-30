package com.backbase.billpay.fiserv.payeessummary;

import com.backbase.billpay.fiserv.payees.PaymentServicesMapper;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payees.model.PayeeSummary.EbillActivationStatusServiceType;
import com.backbase.billpay.fiserv.payees.model.UsAddress;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill;
import com.backbase.billpay.fiserv.payments.model.Payment;
import com.backbase.billpay.fiserv.utils.FiservUtils;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Address;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.LatestBill;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.NextPayment;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.PayeeSummary;
import com.backbase.rest.spec.common.types.Currency;
import java.math.BigDecimal;
import java.util.Date;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = PaymentServicesMapper.class)
public interface PayeesSummaryMapper {

    @Mapping(target = "id", source = "payeeSource.payeeId")
    @Mapping(target = "electronic", expression = "java(payeeSource.getMerchantId() == null ? false : true)")
    @Mapping(target = "accountNumber", source = "payeeSource.accountNumber")
    @Mapping(target = "paymentServices", source = "payeeSource")
    @Mapping(target = "nextPayment", source = "paymentSource")
    @Mapping(target = "ebill.latestBill", source = "ebillSource")
    @Mapping(target = "ebill.capable", source = "payeeSource.ebillActivationStatus", qualifiedByName = "EbillCapable")
    @Mapping(target = "ebill.enabled", source = "payeeSource.ebillActivationStatus", qualifiedByName = "EbillEnabled")
    @Mapping(target = "overnightDeliveryAddress", source = "payeeSource.overNightAddress")
    @Mapping(target = "additions", ignore = true)
    PayeeSummary toPayeeSummary(com.backbase.billpay.fiserv.payees.model.PayeeSummary payeeSource, Ebill ebillSource,
                    Payment paymentSource);

    @Mapping(target = "id", source = "ebillId")
    @Mapping(target = "paymentDate", source = "dueDate")
    @Mapping(target = "paymentAmount", source = "amountDue")
    @Mapping(target = "amount", source = "amountDue")
    @Mapping(target = "minAmountDue", source = "minimumAmountDue")
    @Mapping(target = "outstandingBalance", source = "balance")
    @Mapping(target = "url", source = "billReferenceLinkUrl")
    @Mapping(target = "additions", ignore = true)
    @Mapping(target = "statementAvailable", constant = "false")
    LatestBill toLatestBill(com.backbase.billpay.fiserv.payeessummary.model.Ebill source);

    @Named("EbillCapable")
    default Boolean toEbillCapable(EbillActivationStatusServiceType source) {
        return source != EbillActivationStatusServiceType.EBILL_NOT_AVAILABLE;
    }

    @Named("EbillEnabled")
    default Boolean toEbillEnabled(EbillActivationStatusServiceType source) {
        return (source == EbillActivationStatusServiceType.EBILL_ACTIVE
                        || source == EbillActivationStatusServiceType.EBILL_PENDING);
    }

    @Mapping(target = "paymentDate", source = "paymentDate")
    @Mapping(target = "paymentAmount", source = "amount")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "recurring", source = "recurringModelPayment")
    @Mapping(target = "paymentFee", ignore = true)
    @Mapping(target = "fee", ignore = true)
    @Mapping(target = "additions", ignore = true)
    NextPayment toNextPayment(Payment source);

    @Mapping(target = "postalCode", source = "zip5")
    @Mapping(target = "additions", ignore = true)
    Address toAddress(UsAddress source);

    @Mapping(target = "amount", source = "source")
    @Mapping(target = "currencyCode", constant = "USD")
    @Mapping(target = "additions", ignore = true)
    Currency toCurrency(BigDecimal source);

    default Date toDate(BldrDate source) {
        return FiservUtils.fromFiservDate(source);
    }
}
