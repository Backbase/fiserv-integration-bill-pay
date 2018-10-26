package com.backbase.billpay.fiserv.payments;

import com.backbase.billpay.fiserv.common.model.Header;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayPaymentsPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayRecurringPaymentsPostRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.BillPayRecurringPaymentsPostResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.PaymentByIdPutResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdGetResponseBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdPutRequestBody;
import com.backbase.billpay.integration.rest.spec.v2.billpay.payments.RecurringPaymentByIdPutResponseBody;
import java.util.Date;

/**
 * Interface for payment operations.
 */
public interface PaymentsService {
    
    /**
     * Deletes a payment by ID for the provided subscriber.
     * @param header fiserv header including the subscriber id and ip address.
     * @param id payment id.
     */
    void deletePaymentById(Header header, String id);
    
    /**
     * Get the payments for the provided subscriber using the optional filters.
     * @param header fiserv header including the subscriber id and ip address.
     * @param status the status of payments to filter by.
     * @param startDate the start date for payments to filter by.
     * @param endDate the end date for payments to filter by.
     * @param payeeId the id of the payee to filter by.
     * @param from pagination from.
     * @param size pagination size.
     * @return payments response.
     */
    BillPayPaymentsGetResponseBody getBillPayPayments(
            Header header, String status, Date startDate, Date endDate, String payeeId, 
            Integer from, Integer size);
    
    /**
     * Creates a payment.
     * @param header fiserv header including the subscriber id and ip address.
     * @param request payment request.
     * @return response.
     */
    BillPayPaymentsPostResponseBody postBillPayPayments(Header header, BillPayPaymentsPostRequestBody request);
    
    /**
     * Retrieve a payment using the specified payment id and subscriber.
     * @param header fiserv header including the subscriber id and ip address.
     * @param id payment id.
     * @return payment
     */
    PaymentByIdGetResponseBody getPaymentById(Header header, String id);
    
    /**
     * Update a single payment using the specified id.
     * @param header fiserv header including the subscriber id and ip address.
     * @param id payment id.
     * @param request payment request.
     * @return payment response.
     */
    PaymentByIdPutResponseBody putBillPayPayments(Header header, String id, PaymentByIdPutRequestBody request);
    
    /**
     * Retrieve a recurring payment using the specified payment id and subscriber.
     * @param header fiserv header including the subscriber id and ip address.
     * @param id recurring payment id.
     * @return payment response.
     */
    RecurringPaymentByIdGetResponseBody getRecurringPaymentById(Header header, String id);
    
    /**
     * Creates a recurring payment.
     * @param header fiserv header including the subscriber id and ip address.
     * @param request recurring payment request.
     * @return payment response.
     */
    BillPayRecurringPaymentsPostResponseBody postBillPayRecurringPayments(
                    Header header, BillPayRecurringPaymentsPostRequestBody request);

    /**
     * Deletes a recurring payment by ID for the provided subscriber.
     * @param header fiserv header including the subscriber id and ip address.
     * @param id recurring payment id.
     */
    void deleteRecurringPaymentById(Header header, String id);
    
    /**
     * Update a recurring payment using the specified id.
     * @param header fiserv header including the subscriber id and ip address.
     * @param id recurring payment id.
     * @param request recurring payment request.
     * @return payment response.
     */
    RecurringPaymentByIdPutResponseBody putBillPayRecurringPayments(
                    Header header, String id, RecurringPaymentByIdPutRequestBody request);
}