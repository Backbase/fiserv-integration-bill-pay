package com.backbase.billpay.fiserv.payeessummary;

import com.backbase.billpay.fiserv.autopay.AutopayMapper;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResultInfo;
import com.backbase.billpay.fiserv.ebills.EbillsMapper;
import com.backbase.billpay.fiserv.payees.PaymentServicesMapper;
import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payees.model.UsAddress;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill;
import com.backbase.billpay.fiserv.payments.model.Payment;
import com.backbase.billpay.fiserv.utils.FiservUtils;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Address;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.NextPayment;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.PayeeSummary;
import com.backbase.rest.spec.common.types.Currency;
import java.math.BigDecimal;
import java.util.Date;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {PaymentServicesMapper.class,
                EbillsMapper.class, AutopayMapper.class})
public interface PayeesSummaryMapper {

    @Mapping(target = "id", source = "payeeSource.payeeId")
    @Mapping(target = "electronic", expression = "java(payeeSource.getMerchantId() == null ? false : true)")
    @Mapping(target = "accountNumber", source = "payeeSource.accountNumber")
    @Mapping(target = "paymentServices", source = "payeeSource")
    @Mapping(target = "nextPayment", source = "paymentSource")
    @Mapping(target = "ebill.latestBill", source = "ebillSource")
    @Mapping(target = "ebill.capable", source = "payeeSource.ebillActivationStatus", qualifiedByName = "EbillCapable")
    @Mapping(target = "ebill.enabled", source = "payeeSource.ebillActivationStatus", qualifiedByName = "EbillEnabled")
    @Mapping(target = "ebill.autopay", source = "autopaySource")
    @Mapping(target = "overnightDeliveryAddress", source = "payeeSource.overNightAddress")
    @Mapping(target = "additions", ignore = true)
    PayeeSummary toPayeeSummary(com.backbase.billpay.fiserv.payees.model.PayeeSummary payeeSource, Ebill ebillSource,
                    EbillAutoPayListResultInfo autopaySource, Payment paymentSource);

    @Mapping(target = "paymentDate", source = "paymentDate")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "recurring", source = "recurringModelPayment")
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
