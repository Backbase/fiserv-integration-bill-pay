package com.backbase.billpay.fiserv.payments;

import com.backbase.billpay.fiserv.payments.model.BankAccountId;
import com.backbase.billpay.fiserv.payments.model.Payment;
import com.backbase.billpay.fiserv.payments.model.PaymentAddRequest;
import com.backbase.billpay.fiserv.payments.model.PaymentDetail;
import com.backbase.billpay.fiserv.payments.model.PaymentListResponse;
import com.backbase.billpay.fiserv.payments.model.PaymentModifyRequest;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModel;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelAddRequest;
import com.backbase.billpay.fiserv.payments.recurring.model.RecurringModelModifyRequest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayRecurringPaymentsPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.OneOffPayment;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentAccount;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdPutRequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = PaymentMapper.class)
public interface PaymentsMapper {

    @Mappings({
        @Mapping(target = "additions", ignore = true),
        @Mapping(target = "fee", ignore = true),
        @Mapping(target = "paymentAccount", source = "paymentDetail.bankAccountId"),
        @Mapping(target = "payeeElectronic", ignore = true),
        @Mapping(target = "id", source = "paymentDetail.paymentId"),
        @Mapping(target = "paymentMemo", source = "paymentDetail.memo"),
        @Mapping(target = "ebillID", source = "paymentDetail.ebillId"),
        @Mapping(target = "recurring", source = "paymentDetail.recurringModelPayment"),
        @Mapping(target = "payeeAccountNumber", source = "paymentDetail.payee.accountNumber"),
        @Mapping(target = "payeeName", source = "paymentDetail.payee.name"),
        @Mapping(target = "payeeNickName", source = "paymentDetail.payee.nickName"),
        @Mapping(target = "payeeID", source = "paymentDetail.payee.payeeId"),
        @Mapping(target = "amount", source = "paymentDetail.amount"),
        @Mapping(target = "automaticPayment", source = "paymentDetail.ebillAutoPayment")
    }) 
    OneOffPayment toOneOffPayment(PaymentDetail paymentDetail);

    @Mappings({
        @Mapping(target = "header", ignore = true),
        @Mapping(target = "paymentList", source = "request.payments")
    })
    PaymentAddRequest toPaymentAddRequest(BillPayPaymentsPostRequestBody request);

    @Mappings({
        @Mapping(target = "header", ignore = true),
        @Mapping(target = "ebillId", source = "request.payment.ebillID"),
        @Mapping(target = "paymentMemo", source = "request.payment.paymentMemo"),
        @Mapping(target = "paymentId", source = "id"),
        @Mapping(target = "paymentDate", source = "request.payment.paymentDate"),
        @Mapping(target = "paymentAmount", source = "request.payment.amount.amount"),
        @Mapping(target = "bankAccountId", source = "request.payment.paymentAccount")
    })
    PaymentModifyRequest toPaymentModifyRequest(String id, PaymentByIdPutRequestBody request);

    @Mappings({
        @Mapping(target = "accountNumber", source = "account.accountNumber"),
        @Mapping(target = "routingTransitNumber", source = "account.routingNumber"),
        @Mapping(target = "accountType", source = "account.accountType")
    }) 
    BankAccountId map(PaymentAccount account);

    @Mappings({
        @Mapping(target = "additions", ignore = true),
        @Mapping(target = "totalCount", ignore = true)
    })
    BillPayPaymentsGetResponseBody map(PaymentListResponse fiservResponse);

    @Mappings({
        @Mapping(target = "additions", ignore = true),
        @Mapping(target = "fee", ignore = true),
        @Mapping(target = "payeeElectronic", ignore = true),
        @Mapping(target = "id", source = "payment.paymentId"),
        @Mapping(target = "paymentMemo", source = "payment.memo"),
        @Mapping(target = "ebillID", source = "payment.ebillId"),
        @Mapping(target = "recurring", source = "payment.recurringModelPayment"),
        @Mapping(target = "payeeAccountNumber", source = "payment.payee.accountNumber"),
        @Mapping(target = "paymentAccount", source = "payment.bankAccountId"),
        @Mapping(target = "payeeName", source = "payment.payee.name"),
        @Mapping(target = "payeeNickName", source = "payment.payee.nickName"),
        @Mapping(target = "payeeID", source = "payment.payee.payeeId"),
        @Mapping(target = "amount", source = "payment.amount"),
        @Mapping(target = "numberOfInstances", ignore = true),
        @Mapping(target = "frequency", ignore = true),
        @Mapping(target = "paymentScheduledAlert", ignore = true),
        @Mapping(target = "paymentSentAlert", ignore = true),
        @Mapping(target = "modelExpirationAlert", ignore = true),
        @Mapping(target = "automaticPayment", source = "payment.ebillAutoPayment")
    })
    com.backbase.billpay.integration.rest.spec.v2.billpay.payments.Payment map(Payment payment);

    @Mappings({
        @Mapping(target = "additions", ignore = true),
        @Mapping(target = "payment.id", source = "recurringModel.recurringModelId"),
        @Mapping(target = "payment.payeeID", source = "recurringModel.payeeId"),
        @Mapping(target = "payment.paymentScheduledAlert", source = "recurringModel.paymentScheduledAlert"),
        @Mapping(target = "payment.paymentSentAlert", source = "recurringModel.paymentSentAlert"),
        @Mapping(target = "payment.modelExpirationAlert", source = "recurringModel.modelExpirationAlert"),
        @Mapping(target = "payment.frequency", source = "recurringModel.recurringModelInfo.frequency"),
        @Mapping(target = "payment.numberOfInstances", source = "recurringModel.recurringModelInfo.numberOfPayments"),
        @Mapping(target = "payment.paymentAccount", source = "recurringModel.fundingAccount"),
        @Mapping(target = "payment.amount", source = "recurringModel.recurringModelInfo.recurringPaymentAmount"),
        @Mapping(target = "payment.paymentMemo", source = "recurringModel.recurringModelInfo.memo"),
        @Mapping(target = "payment.automaticPayment", constant = "false")
    })
    RecurringPaymentByIdGetResponseBody map(RecurringModel recurringModel);

    @Mappings({
        @Mapping(target = "header", ignore = true),
        @Mapping(target = "payeeId", source = "request.payment.payeeID"),
        @Mapping(target = "accountId", source = "request.payment.paymentAccount"),
        @Mapping(target = "modelExpirationAlert", source = "request.payment.modelExpirationAlert"),
        @Mapping(target = "paymentScheduledAlert", source = "request.payment.paymentScheduledAlert"),
        @Mapping(target = "paymentSentAlert", source = "request.payment.paymentSentAlert"),
        @Mapping(target = "modelInfo.numberOfPayments", source = "request.payment.numberOfInstances"),
        @Mapping(target = "modelInfo.frequency", source = "request.payment.frequency"),
        @Mapping(target = "modelInfo.memo", source = "request.payment.paymentMemo"),
        @Mapping(target = "modelInfo.isIndefinite", ignore = true),
        @Mapping(target = "modelInfo.recurringPaymentAmount", source = "request.payment.amount.amount"),
        @Mapping(target = "modelInfo.nextPaymentDate", ignore = true)
    })
    RecurringModelAddRequest toRecurringModelAddRequest(BillPayRecurringPaymentsPostRequestBody request);

    @Mappings({
        @Mapping(target = "header", ignore = true),
        @Mapping(target = "recurringModelId", source = "id"),
        @Mapping(target = "accountId", source = "request.payment.paymentAccount"),
        @Mapping(target = "modelExpirationAlert", source = "request.payment.modelExpirationAlert"),
        @Mapping(target = "paymentScheduledAlert", source = "request.payment.paymentScheduledAlert"),
        @Mapping(target = "paymentSentAlert", source = "request.payment.paymentSentAlert"),
        @Mapping(target = "modelInfo.numberOfPayments", source = "request.payment.numberOfInstances"),
        @Mapping(target = "modelInfo.frequency", source = "request.payment.frequency"),
        @Mapping(target = "modelInfo.memo", source = "request.payment.paymentMemo"),
        @Mapping(target = "modelInfo.isIndefinite", ignore = true),
        @Mapping(target = "modelInfo.recurringPaymentAmount", source = "request.payment.amount.amount"),
        @Mapping(target = "modelInfo.nextPaymentDate", ignore = true)
    })
    RecurringModelModifyRequest toRecurringModelModifyRequest(String id, RecurringPaymentByIdPutRequestBody request);
}
