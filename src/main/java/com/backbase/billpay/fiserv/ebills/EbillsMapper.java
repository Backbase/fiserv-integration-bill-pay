package com.backbase.billpay.fiserv.ebills;

import com.backbase.billpay.fiserv.ebills.model.EbillFileRequest;
import com.backbase.billpay.fiserv.ebills.model.EbillFileRequest.FileEbillPaymentMethod;
import com.backbase.billpay.fiserv.payees.model.PayeeSummary.EbillActivationStatusServiceType;
import com.backbase.billpay.fiserv.payeessummary.model.Ebill.EbillStatus;
import com.backbase.billpay.fiserv.payments.PaymentMapper;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.Ebill;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payees.electronic.id.ebills.EbillByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payeessummary.LatestBill;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = PaymentMapper.class)
public interface EbillsMapper {

    List<Ebill> toEbillList(List<com.backbase.billpay.fiserv.payeessummary.model.Ebill> source);
    
    @Mapping(target = "id", source = "ebillId")
    @Mapping(target = "payeeID", source = "payee.payeeId")
    @Mapping(target = "paymentDate", source = "dueDate")
    @Mapping(target = "amount", source = "amountDue")
    @Mapping(target = "minAmountDue", source = "minimumAmountDue")
    @Mapping(target = "outstandingBalance", source = "balance")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "url", source = "billReferenceLinkUrl")
    @Mapping(target = "statementAvailable", constant = "false")
    @Mapping(target = "additions", ignore = true)
    Ebill toEbill(com.backbase.billpay.fiserv.payeessummary.model.Ebill source);
    
    default String toStatus(EbillStatus status) {
        switch (status) {
            case FILED:
                return "PAID";
            case PAID:
                return "PAID";
            case PAYMENT_CANCELLED:
                return "PAYMENT_CANCELLED";
            case PAYMENT_FAILED:
                return "PAYMENT_FAILED";
            case UNPAID:
                return "UNPAID";
            default:
                return null;
        }
    }
    
    @Mapping(target = "header", ignore = true)
    @Mapping(target = "billNote", source = "source.note")
    @Mapping(target = "ebillId", source = "ebillId")
    @Mapping(target = "filedBillReason", source = "source.paymentMethod")
    EbillFileRequest toEbillFileRequest(EbillByIdPutRequestBody source, String ebillId);
    
    default FileEbillPaymentMethod toFileEbillPaymentMethod(String source) {
        switch (source) {
            case "BANK":
                return FileEbillPaymentMethod.BANK;
            case "CHECK":
                return FileEbillPaymentMethod.CHECK;
            case "CASH":
                return FileEbillPaymentMethod.CASH;
            case "NOT_PAID":
                return FileEbillPaymentMethod.NOT_PAID;
            case "BILLER_WEBSITE":
                return FileEbillPaymentMethod.BILLER_WEBSITE;
            case "PHONE":
                return FileEbillPaymentMethod.PHONE;
            case "MAIL":
                return FileEbillPaymentMethod.MAIL;
            case "OTHER":
                return FileEbillPaymentMethod.OTHER;
            default:
                return FileEbillPaymentMethod.NONE_SPECIFIED;
        }
    }

    @Mapping(target = "id", source = "ebillId")
    @Mapping(target = "paymentDate", source = "dueDate")
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

}
