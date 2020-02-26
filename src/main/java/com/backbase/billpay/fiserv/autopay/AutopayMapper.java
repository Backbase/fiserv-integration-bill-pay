package com.backbase.billpay.fiserv.autopay;

import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo.AutoPayAmount;
import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo.AutoPayDaysBefore;
import com.backbase.billpay.fiserv.autopay.model.AutoPayInfo.AutoPayOn;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayAddRequest;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayListResultInfo;
import com.backbase.billpay.fiserv.autopay.model.EbillAutoPayModifyRequest;
import com.backbase.billpay.fiserv.payments.PaymentMapper;
import com.backbase.billpay.fiserv.payments.model.BankAccountId;
import com.backbase.billpay.integration.rest.spec.v2.billpay.accounts.Account;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.Autopay;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = PaymentMapper.class)
public interface AutopayMapper {

    @Mappings({
        @Mapping(target = "autopayInfo.bankAccountId", source = "paymentAccount"),
        @Mapping(target = "autopayInfo.paymentScheduledAlert", source = "paymentScheduledAlert"),
        @Mapping(target = "autopayInfo.paymentSentAlert", source = "paymentSentAlert"),
        @Mapping(target = "autopayInfo.autoPay.daysBefore", source = "daysBeforePayOn"),
        @Mapping(target = "autopayInfo.autoPay.autoPayOn", source = "payOn"),
        @Mapping(target = "autopayInfo.autoPay.autoPayAmount", source = "payAmount"),
        @Mapping(target = "autopayInfo.autoPay.maxAuthorizedAmount", source = "maxPaymentAmount.amount"),
        @Mapping(target = "autopayInfo.autoPay.fixedAmount", ignore = true),
        @Mapping(target = "header", ignore = true)
    })
    EbillAutoPayAddRequest toEbillAutoPayAddRequest(Autopay source);

    @Mappings({
        @Mapping(target = "autopayInfo.bankAccountId", source = "paymentAccount"),
        @Mapping(target = "autopayInfo.paymentScheduledAlert", source = "paymentScheduledAlert"),
        @Mapping(target = "autopayInfo.paymentSentAlert", source = "paymentSentAlert"),
        @Mapping(target = "autopayInfo.autoPay.daysBefore", source = "daysBeforePayOn"),
        @Mapping(target = "autopayInfo.autoPay.autoPayOn", source = "payOn"),
        @Mapping(target = "autopayInfo.autoPay.autoPayAmount", source = "payAmount"),
        @Mapping(target = "autopayInfo.autoPay.maxAuthorizedAmount", source = "maxPaymentAmount.amount"),
        @Mapping(target = "autopayInfo.autoPay.fixedAmount", ignore = true),
        @Mapping(target = "header", ignore = true)
    })
    EbillAutoPayModifyRequest toEbillAutoPayModifyRequest(Autopay source);

    @Mappings({
        @Mapping(target = "accountNumber", source = "accountNumber"),
        @Mapping(target = "accountType", source = "accountType"),
        @Mapping(target = "routingTransitNumber", source = "routingNumber")
    })   
    BankAccountId toBankAccountId(Account source);

    default AutoPayDaysBefore toAutoPayDaysBefore(Integer source) {
        if (source == null) {
            return null;
        }
        switch (source) {
            case 1:
                return AutoPayDaysBefore.ONE;
            case 2:
                return AutoPayDaysBefore.TWO;
            case 3:
                return AutoPayDaysBefore.THREE;
            case 4:
                return AutoPayDaysBefore.FOUR;
            case 5:
                return AutoPayDaysBefore.FIVE;
            default:
                return null;
        }
    }

    default AutoPayAmount toAutoPayAmount(String source) {
        switch (source) {
            case "FULL_AMOUNT":
                return AutoPayAmount.AMOUNT_DUE;
            case "MINIMUM_AMOUNT":
                return AutoPayAmount.MINIMUM_AMOUNT_DUE;
            case "TOTAL_BALANCE":
                return AutoPayAmount.ACCOUNT_BALANCE;
            case "FIXED_AMOUNT":
                return AutoPayAmount.FIXED_AMOUNT;
            default:
                return null;
        }
    }

    default AutoPayOn toAutoPayOn(String source) {
        switch (source) {
            case "BILL_DUE_DATE":
                return AutoPayOn.DUE_DATE;
            case "BILL_ARRIVAL":
                return AutoPayOn.UPON_RECEIPT;
            default:
                return null;
        }
    }

    @Mappings({
        @Mapping(target = "paymentAccount", source = "bankAccountId"),
        @Mapping(target = "daysBeforePayOn", source = "autoPay.daysBefore"),
        @Mapping(target = "maxPaymentAmount", source = "autoPay.maxAuthorizedAmount"),
        @Mapping(target = "payOn", source = "autoPay.autoPayOn"),
        @Mapping(target = "payAmount", source = "autoPay.autoPayAmount"),
        @Mapping(target = "paymentScheduledAlert", source = "paymentScheduledAlert"),
        @Mapping(target = "paymentSentAlert", source = "paymentSentAlert"),
        @Mapping(target = "paymentCompleteAlert", ignore = true),
        @Mapping(target = "additions", ignore = true)
    })
    Autopay toAutopay(EbillAutoPayListResultInfo source);

    default String toPayOn(AutoPayOn source) {
        switch (source) {
            case DUE_DATE:
                return "BILL_DUE_DATE";
            case UPON_RECEIPT:
                return "BILL_ARRIVAL";
            default:
                return null;
        }
    }

    default String toPayAmount(AutoPayAmount source) {
        switch (source) {
            case MINIMUM_AMOUNT_DUE:
                return "MINIMUM_AMOUNT";
            case AMOUNT_DUE:
                return "FULL_AMOUNT";
            case ACCOUNT_BALANCE:
                return "TOTAL_BALANCE";
            case FIXED_AMOUNT:
                return "FIXED_AMOUNT";
            default:
                return null;
        }
    }

    default Integer toDaysBeforePayOn(AutoPayDaysBefore source) {
        switch (source) {
            case ONE:
                return 1;
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            default:
                return null;
        }
    }
}

