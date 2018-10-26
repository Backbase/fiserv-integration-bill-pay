package com.backbase.billpay.fiserv.payments;

import com.backbase.billpay.fiserv.payees.model.BldrDate;
import com.backbase.billpay.fiserv.payments.model.BankAccountId;
import com.backbase.billpay.fiserv.payments.model.BankAccountId.BankAccountType;
import com.backbase.billpay.fiserv.payments.model.PaymentAddResponse;
import com.backbase.billpay.fiserv.payments.model.StandardPaymentAddInfo;
import com.backbase.billpay.integration.rest.spec.v2.billpay.accounts.Account;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentRequest;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentResponse;
import com.backbase.rest.spec.common.types.Currency;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Currency map(BigDecimal amount) {
        return new Currency().withAmount(amount)
                             .withCurrencyCode("USD");
    }
    
    public Date map(BldrDate bldrDate) {
        if (bldrDate == null) {
            return null;
        } else {
            try {
                DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                return format.parse(bldrDate.getDate());
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
    
    public BldrDate map(String date) {
        return BldrDate.builder().date(date).build();
    }
    
    public Account map(BankAccountId bankAccountId) {
        return new Account()
                     .withAccountNumber(bankAccountId.getAccountNumber())
                     .withAccountType(bankAccountId.getAccountType().toString())
                     .withRoutingNumber(bankAccountId.getRoutingTransitNumber());
    }
    
    public BillPayPaymentsPostResponseBody toBillPayPaymentsPostResponseBody(
                    BillPayPaymentsPostRequestBody request, PaymentAddResponse addResponse) {
        List<PaymentResponse> paymentsResponse = new ArrayList<>();
        paymentsResponse.add(new PaymentResponse()
                                    .withPayeeID(request.getPayments().get(0).getPayeeID())
                                    .withPaymentID(addResponse.getPayments().get(0).getPaymentId()));
        return new BillPayPaymentsPostResponseBody()
                     .withPayments(paymentsResponse);
    }
    
    public StandardPaymentAddInfo map(PaymentRequest paymentRequest) {
        return StandardPaymentAddInfo.builder()
                                     .ebillId(paymentRequest.getEbillID())
                                     .bankAccountId(BankAccountId.builder()
                                                                 .accountNumber(paymentRequest.getPaymentAccount().getAccountNumber())
                                                                 .accountType(BankAccountType.valueOf(paymentRequest.getPaymentAccount().getAccountType()))
                                                                 .routingTransitNumber(paymentRequest.getPaymentAccount().getRoutingNumber())
                                                                 .build())
                                     .payeeId(Long.valueOf(paymentRequest.getPayeeID()))
                                     .paymentAmount(paymentRequest.getAmount().getAmount())
                                     .paymentDate(BldrDate.builder()
                                                           .date(paymentRequest.getPaymentDate())
                                                           .build())
                                     .paymentMemo(paymentRequest.getPaymentMemo())
                                     .listItemId(0)
                                     .build();
    }
}
