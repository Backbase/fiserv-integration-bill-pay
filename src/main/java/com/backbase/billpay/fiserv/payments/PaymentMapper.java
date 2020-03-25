package com.backbase.billpay.fiserv.payments;

import static com.backbase.billpay.fiserv.payees.PaymentServicesMapper.CURRENCY;

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
                             .withCurrencyCode(CURRENCY);
    }
    
    /**
     * Convert a BldrDate to a Date object.
     * @param bldrDate Date to convert
     * @return Converted date
     */
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
    
    /**
     * Convert a BankAccountId to an Account object.
     * @param bankAccountId Account to convert
     * @return Converted account
     */
    public Account map(BankAccountId bankAccountId) {
        return new Account()
                     .withAccountNumber(bankAccountId.getAccountNumber())
                     .withAccountType(bankAccountId.getAccountType().toString())
                     .withRoutingNumber(bankAccountId.getRoutingTransitNumber());
    }
    
    /**
     * Convert a PaymentRequest to a StandardPaymentAddInfo object.
     * @param paymentRequest Request to convert
     * @return Converted request
     */
    public StandardPaymentAddInfo map(PaymentRequest paymentRequest) {
        return StandardPaymentAddInfo.builder()
                        .ebillId(paymentRequest.getEbillID())
                        .bankAccountId(BankAccountId.builder()
                                        .accountNumber(paymentRequest.getPaymentAccount().getAccountNumber())
                                        .accountType(BankAccountType
                                                        .valueOf(paymentRequest.getPaymentAccount().getAccountType()))
                                        .routingTransitNumber(paymentRequest.getPaymentAccount().getRoutingNumber())
                                        .build())
                        .payeeId(Long.valueOf(paymentRequest.getPayeeID()))
                        .paymentAmount(paymentRequest.getAmount().getAmount())
                        .paymentDate(BldrDate.builder().date(paymentRequest.getPaymentDate().toString()).build())
                        .paymentMemo(paymentRequest.getPaymentMemo()).listItemId(0)
                        .build();
    }
    
    /**
     * Convert a BillPayPaymentsPostRequestBody to a BillPayPaymentsPostResponseBody.
     * @param request Request to convert.
     * @param addResponse Response containing payment ids.
     * @return Converted response.
     */
    public BillPayPaymentsPostResponseBody toBillPayPaymentsPostResponseBody(
                    BillPayPaymentsPostRequestBody request, PaymentAddResponse addResponse) {
        List<PaymentResponse> paymentsResponse = new ArrayList<>();
        paymentsResponse.add(new PaymentResponse()
                                    .withPayeeID(request.getPayments().get(0).getPayeeID())
                                    .withPaymentID(addResponse.getPayments().get(0).getPaymentId()));
        return new BillPayPaymentsPostResponseBody()
                     .withPayments(paymentsResponse);
    }
}
